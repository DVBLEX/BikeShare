package com.beskyd.ms_control.business.distributions;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.schemestocksontrol.centraldepot.CentralDepot;
import com.beskyd.ms_control.business.schemestocksontrol.centraldepot.CentralDepotManagerService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsTransferQueue;
import com.beskyd.ms_control.business.stockrequests.RequestProductsTypesList;
import com.beskyd.ms_control.business.stockrequests.StockRequest;


@Service
public class DistributionService {

    public static final Integer ISSUED_DISTRIBUTION = 21;
    public static final Integer CLOSED_DISTRIBUTION = 22;
    public static final Integer SHIPPED_DISTRIBUTION = 23;
    
    private final DistributionRepository repo;
    private final DistributionAssetsService distAssetsService;
    private final TypeOfAssetsService typeOfAssetsService;
    private final StatesRepository statesRepo;
    private final CentralDepotManagerService centralDepotService;
    private final AssetsCurrentValuesService assetsCurrentValuesService;
    
    @Inject
    public DistributionService(DistributionRepository repo, DistributionAssetsService distAssetsService, TypeOfAssetsService typeOfAssetsService,
        StatesRepository statesRepo, CentralDepotManagerService centralDepotService,
        AssetsCurrentValuesService assetsCurrentValuesService) {
        this.repo = repo;
        this.distAssetsService = distAssetsService;
        this.typeOfAssetsService = typeOfAssetsService;
        this.statesRepo = statesRepo;
        this.centralDepotService = centralDepotService;
        this.assetsCurrentValuesService = assetsCurrentValuesService;
    }
    
    
    public List<Distribution> findAll(){        
        return repo.findAll(Sort.by("id").descending());
    }
    
    /**
     * Find distributions, which where made from stock requests and are attached to them
     * @return
     */
    public List<Distribution> findForStockRequests(){
        return repo.findDistributionsWhereStockRequestNotNull();
    }
    
    /**
     * Find {@link Distribution} by id
     * @param id
     * @return object if found, null - if not
     */
    public Distribution findById(Integer id) {
        return repo.findById(id).orElse(null);
    }
    
    public Distribution save(Distribution savable) {
        if(savable.getId() == null) {
            savable.setState(statesRepo.findById(ISSUED_DISTRIBUTION).orElseThrow());
            savable.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
            savable.setStateChangeDate(savable.getCreationDate());
        }
        
        repo.save(savable);
        
        for(DistributionAssets asset : savable.getAssets()) {
            asset.setDistribution(savable);
        }
        
        distAssetsService.saveAll(savable.getAssets());
        
        return savable;
    }
    
    /**
     * A wrapper for {@code generateFromStockRequest}.
     * Saves distribution by {@code save} method. Also saves updated values of {@link CentralDepot}
     */
    public Distribution createFromStockRequest(StockRequest stockRequest, String notes) {
        Distribution dist = generateFromStockRequest(stockRequest);
        dist.setNotes(notes);
        for(DistributionAssets asset : dist.getAssets()) {
            CentralDepot cd = centralDepotService.findByProductTypeId(asset.getTypeOfAssets().getId());
            cd.setAmount(cd.getAmount() - asset.getQuantity());
            centralDepotService.save(cd);
        }
        
        return save(dist);
    }
    
    public Distribution generateFromStockRequest(StockRequest stockRequest) {
        Set<DistributionAssets> assets = new HashSet<>();
        
        Scheme schemeFrom = Boolean.TRUE.equals(typeOfAssetsService.findById(stockRequest.getRequestedProductTypes().get(0).getProductType().getId()).getProducts().stream().findFirst().orElseThrow().getProductId().getSupplier().getMiscellaneous()) ? new Scheme("Dublin") : new Scheme("Cork");
        
        Distribution distribution = new Distribution(null, stockRequest, schemeFrom, stockRequest.getScheme(), null, null, null, null, assets);
        
        for(RequestProductsTypesList requestedType : stockRequest.getRequestedProductTypes()) {
            CentralDepot centralDepotRecort = centralDepotService.findByProductTypeId(requestedType.getProductType().getId());
            if(centralDepotRecort == null || centralDepotRecort.getAmount() == 0) {
                continue;
            }
            Integer quantity = requestedType.getOrderValue();
            if(quantity > centralDepotRecort.getAmount()) {
                quantity = centralDepotRecort.getAmount();
            }
            assets.add(new DistributionAssets(distribution, requestedType.getProductType(), quantity));
        }
        
        return distribution;
    }
    
    /**
     * Basically, a wrapper for {@code generateFromTransferQueue} method
     * Besides generation of distributions, saves them by {@code save} method and updates assets current values
     */
    public List<Distribution> createFromTransferQueue(List<AssetsTransferQueue> transferQueue){
        List<Distribution> distributions = generateFromTransferQueue(transferQueue);
        
        for(Distribution dist : distributions) {
            for(DistributionAssets asset : dist.getAssets()) {
                AssetsCurrentValues acv = assetsCurrentValuesService.findByComplexId(asset.getTypeOfAssets(), dist.getSchemeFrom());
                acv.setQuantity(acv.getQuantity() - asset.getQuantity());
                assetsCurrentValuesService.save(acv);
            }
            
            save(dist);
        }
        
        return distributions;
    }
    
    public List<Distribution> generateFromTransferQueue(List<AssetsTransferQueue> transferQueue){
        var queueBySchemeFrom = transferQueue.stream().collect(Collectors.groupingBy(q -> q.getTransferFromScheme().getName()));
        
        List<Distribution> distributions = new ArrayList<>();
        
        for(var entrySF : queueBySchemeFrom.entrySet()) {
            Scheme schemeFrom = entrySF.getValue().get(0).getTransferFromScheme();
            
            var queueBySchemeTo = entrySF.getValue().stream().collect(Collectors.groupingBy(q -> q.getTransferToScheme().getName()));
            
            for(var entryST : queueBySchemeTo.entrySet()) {
                Set<DistributionAssets> assets = new HashSet<>();
                
                Distribution dist = new Distribution(null, null, schemeFrom, entryST.getValue().get(0).getTransferToScheme(), null, null, null, null, assets);
                
                for(AssetsTransferQueue queuedType : entryST.getValue()) {
                    //we need to search, if this type isn't already in the distribution
                    Optional<DistributionAssets> presentAssetOpt = assets.stream().filter(a -> a.getTypeOfAssets().getId().equals(queuedType.getProductType().getId()))
                                                                        .findFirst();//findFirst because there should be only one instance of type in distribution
                    if(presentAssetOpt.isPresent()) {
                        presentAssetOpt.get().setQuantity(presentAssetOpt.get().getQuantity() + queuedType.getQuantity());
                    } else {
                        DistributionAssets newAsset = new DistributionAssets(dist, queuedType.getProductType(), queuedType.getQuantity());
                        assets.add(newAsset);
                    }   
                }
                
                distributions.add(dist);
            }
        }
        
        return distributions;
    }
    
}
