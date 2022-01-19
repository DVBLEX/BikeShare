package com.beskyd.ms_control.business.schemestocksontrol.centraldepot;

import java.util.List;

import org.springframework.stereotype.Service;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.usermanagement.User;

@Service
public class CentralDepotManagerService {
    
    private final CentralDepotCorkService corkService;
    private final CentralDepotDublinService dublinService;
    private final TypeOfAssetsService typesService;
    
    
    public CentralDepotManagerService(CentralDepotCorkService corkService, CentralDepotDublinService dublinService, TypeOfAssetsService typesService) {
        this.corkService = corkService;
        this.dublinService = dublinService;
        this.typesService = typesService;
    }
    
    public List<CentralDepot> findAll(User user) {
        if(user != null && user.hasRole(User.ROLE_FULFILLMENT_OPERATOR) && !user.hasRole(User.ROLE_ADMIN) && !user.hasRole(User.ROLE_PURCHASE_MANAGER)) {
            return dublinService.findAll();
        } else {
            List<CentralDepot> allCDRecords = corkService.findAll();
            
            List<CentralDepot> dublinRecords = dublinService.findAll();
            
            //our goal here is to replace (or add, if there is no such) all fulfillment products in Cork list from Dublin list,
            for(CentralDepot dublinRecord : dublinRecords) {                
                if(Boolean.TRUE.equals(dublinRecord.getProductType().getProducts().stream().findFirst().orElseThrow().getProductId().getSupplier().getMiscellaneous())) {
                    boolean present = false;
                    for (int i = 0; i < allCDRecords.size(); i++) {
                        if(allCDRecords.get(i).getProductType().getId().equals(dublinRecord.getProductType().getId())) {
                            allCDRecords.set(i, dublinRecord);
                            present = true;
                            
                            break;
                        }
                    }
                    if(!present) {
                        allCDRecords.add(dublinRecord);
                    }
                }
            }
            
            return allCDRecords;
        }
    }

    public CentralDepot findByProductTypeId(Integer productTypeId) {
        TypeOfAssets type = typesService.findById(productTypeId);
        for(var prod : type.getProducts()) {
            if(Boolean.TRUE.equals(prod.getProductId().getSupplier().getMiscellaneous())) {//we just need to check the first product and then
                return dublinService.findByProductTypeId(productTypeId);
            } else {
                return corkService.findByProductTypeId(productTypeId);
            }
        }
        
        return null;
        
    }

    public CentralDepot save(CentralDepot savable) {
        if(savable.getProductType().getProducts().isEmpty()) {
            savable.setProductType(typesService.findById(savable.getProductType().getId()));
        }
        for(var prod : savable.getProductType().getProducts()) {
            if(Boolean.TRUE.equals(prod.getProductId().getSupplier().getMiscellaneous())) {//we just need to check the first product and then
                return dublinService.save(savable);
            } else {
                return corkService.save(savable);
            }
        }
        
        return null;
    }
}
