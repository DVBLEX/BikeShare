package com.beskyd.ms_control.business.stockrequests;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.general.*;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValuesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockRequestService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StockRequestService.class);
    
    public static final int NEW_REQUEST = 1;
    public static final int MERGED_REQUEST = 2;
    public static final int IN_DISTRIBUTION_REQUEST = 3;
    public static final int FULFILLED_REQUEST = 4;
    
    private final StockRequestRepository repo;
    private final RequestProductsTypesListRepository typesListRepo;
    private final AssetsMarginalValuesService amvService;
    private final AssetsCurrentValuesService acvService;
    private final SchemeService schemeService;
    private final StatesRepository statesRepo;
    private final SystemParametersService systemParametersService;
    
    @PersistenceContext
    private EntityManager em;
    
    @Inject
    public StockRequestService(StockRequestRepository repo, AssetsMarginalValuesService amvService, AssetsCurrentValuesService acvService,
        SchemeService schemeService, StatesRepository statesRepo, RequestProductsTypesListRepository typesListRepo, SystemParametersService systemParametersService) {
        this.repo = repo;
        this.amvService = amvService;
        this.acvService = acvService;
        this.schemeService = schemeService;
        this.statesRepo = statesRepo;
        this.typesListRepo = typesListRepo;
        this.systemParametersService = systemParametersService;
    }

    public List<StockRequest> findAll(){
        Sort sort = new Sort(Sort.Direction.DESC, "id", "state.id");
        return repo.findAll(sort);
    }
    
    public StockRequest findById(Integer id) {
        return repo.findById(id).orElse(null);
    }
    
    public List<StockRequest> findByStateId(int stateId){
        return repo.findByState_Id(stateId);
    }
    
    public List<StockRequest> findByScheme(Scheme scheme){
        return repo.findByScheme_Name(scheme.getName());
    }
    
    public List<StockRequest> findInRangeOfDateToNow(Timestamp dateFrom){
        return repo.findByRangeFromDate(dateFrom);
    }
    
    private AssetsMarginalValues findMarginalValuesInListByProductTypeId(int productTypeId, List<AssetsMarginalValues> list) {
        for(AssetsMarginalValues amv : list) {
            if(amv.getProductType().getId() == productTypeId) {
                return amv;
            }
        }
        
        return null;
    }

    /**
     * Check if type of product is already requested for this scheme
     * Check only requests with state 'New' (id 1 in DB)
     * @param productTypeId
     * @param schemeName
     * @return true, if type if requested
     */

    public boolean checkIfTypeOfProductForSchemeIsInRequest(int productTypeId, String schemeName) {

        //Not using repository just to make it simple and straightforward
        return !em.createNativeQuery("select id_request from request_products_types_list rp \r\n" +
            "inner join stock_requests sr on sr.id=rp.id_request\r\n" +
            "where rp.id_prod_type=" + productTypeId + " and sr.`scheme`='" + schemeName + "' and (sr.state_id<>3 and sr.state_id<>4)").getResultList().isEmpty();
    }

    //@Scheduled(fixedDelay = 600000)//delay - 10 min, just for a development stage
    @Scheduled(cron = "0 0 0 * * ? ")//To execute every day on 00:00
    public void generateRequests() {
        List<Scheme> schemes = schemeService.findAll();

        for(Scheme scheme : schemes) {
            generateRequestForScheme(scheme, false); //for all assets, except fulfillment

            generateRequestForScheme(scheme, true); //for fulfillment assets only
        }
    }

    /**
     * Changes state and saves {@link StockRequest}
     * @param request
     * @param stateId
     * @return state change datetime
     */
    public Timestamp changeStateAndSave(StockRequest request, int stateId) {
        States state = statesRepo.findById(stateId).orElseThrow();
        request.setState(state);
        request.setStateChangeDate(Timestamp.valueOf(LocalDateTime.now()));
        repo.save(request);
        return request.getStateChangeDate();
    }
    
    /**
     * Saves stock request.
     * If stock request is new, and contains fulfillment and other assets, separates it into two requests
     * @param request
     * @return
     */
    public List<StockRequest> save(StockRequest request) {
        List<StockRequest> requests = new ArrayList<>();
        
        if(request.getId() == null) {
            request.setState(statesRepo.findById(1).orElseThrow());
            request.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
            request.setStateChangeDate(request.getCreationDate());
            
            StockRequest fRequest = new StockRequest(null, new ArrayList<>(), request.getScheme(), request.getState(), request.getManual(), request.getNotes());
            
            for (int i = 0; i < request.getRequestedProductTypes().size(); i++) {
                if(request.getRequestedProductTypes().get(i).getProductType().getAssetGroup().toLowerCase().contains(TypeOfAssets.FULFILLMENT_GROUP_LC)) {
                    fRequest.getRequestedProductTypes().add(request.getRequestedProductTypes().remove(i--));
                }
            }
            
            if(!fRequest.getRequestedProductTypes().isEmpty()) {
                requests.add(fRequest);
            }
            if(!request.getRequestedProductTypes().isEmpty()) {
                requests.add(request);
            }
        }
        repo.saveAll(requests);
        
        for(var r : requests) {
            for(RequestProductsTypesList rp : r.getRequestedProductTypes()) {
                rp.setStockRequest(r);
            }
            
            typesListRepo.saveAll(r.getRequestedProductTypes());
        }
        return requests;
    }
    
    /**
     * Generate {@link StockRequest} for the set scheme. We can generate request for all types except fulfillment assets or just for fulfillment
     * @param scheme
     * @return id of generated request, or null if request was not generated, because there where no need
     */
    public Integer generateRequestForScheme(Scheme scheme, boolean fulfillment) {
        List<AssetsMarginalValues> amvList = amvService.findByScheme(scheme);
        
        List<AssetsCurrentValues> acvList = acvService.findByScheme(scheme);
        
        StockRequest request = new StockRequest();
        request.setScheme(scheme);
        request.setState(statesRepo.getOne(1));
        request.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
        request.setStateChangeDate(request.getCreationDate());
        
        //create list of types of products, that will be inserted into the request
        List<RequestProductsTypesList> productsForRequest = new ArrayList<>();
        
        //Loop over all current values in scheme
        for(AssetsCurrentValues acv : acvList) {
            AssetsMarginalValues amv = findMarginalValuesInListByProductTypeId(acv.getProductType().getId(), amvList);
            
            if(amv != null) {
                
                if(fulfillment) {
                    if(!acv.getProductType().getAssetGroup().toLowerCase().contains("fulfillment")) {
                        continue; 
                    }
                } else {
                    if(acv.getProductType().getAssetGroup().toLowerCase().contains("fulfillment")) {
                       continue; 
                    }
                }
                
                //check, if we need to add this type of products to the request
                if (((double) acv.getQuantity() <= amv.getTrigger()) && !checkIfTypeOfProductForSchemeIsInRequest(acv.getProductType().getId(), acv.getScheme().getName())) {
                    productsForRequest.add(new RequestProductsTypesList(request, new TypeOfAssets(acv.getProductType()), amv.getOrderValue()));
                }                        
            }
        }
        
        request.setRequestedProductTypes(productsForRequest);
        
        if(!productsForRequest.isEmpty()) {
            Integer requestId =  repo.save(request).getId();
            for(RequestProductsTypesList p : request.getRequestedProductTypes()) {
                p.setIdRequest(requestId);
            }
            typesListRepo.saveAll(request.getRequestedProductTypes());
            
            return requestId;
        }
        
        return null;
    }
    
    
    public void delete(int requestId) {
        repo.deleteById(requestId);
    }
}
