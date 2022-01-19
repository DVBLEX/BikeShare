package com.beskyd.ms_control.business.schemestocksontrol.values;

import java.util.List;
import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;

@Service
public class AssetsTransferQueueService {

    private final AssetsTransferQueueRepository repo;

    @Inject
    public AssetsTransferQueueService(AssetsTransferQueueRepository repo) {
        this.repo = repo;
    }
    
    
    public List<AssetsTransferQueue> findAll(){
        return repo.findAll();
    }
    
    /**
     * Find AssetsTransferQueue by id.
     * @param id
     * @return AssetsTransferQueue object if present, null - if not
     */
    public AssetsTransferQueue findById(Integer id) {
        return repo.findById(id).orElse(null);
    }
    
    /**
     * Find identical transfer by product type, transfer-from-scheme and transfer-to-scheme
     * @param productTypeId
     * @param transferFromSchemeName
     * @param transferToSchemeName
     * @return AssetsTransferQueue object if present, null - if not
     */
    public AssetsTransferQueue findIdentical(Integer productTypeId, 
        String transferFromSchemeName, String transferToSchemeName) {
        return repo.findByProductType_IdAndTransferFromScheme_NameAndTransferToScheme_Name(productTypeId, 
            transferFromSchemeName, transferToSchemeName);
    }
    
    public AssetsTransferQueue save(AssetsTransferQueue savable) throws TransferToTheSameSchemeException, IdenticalTransferException {
        if(savable.getTransferFromScheme().equals(savable.getTransferToScheme())) {
            throw new TransferToTheSameSchemeException(savable.getTransferToScheme());
        }
        if(savable.getId() == null && findIdentical(savable.getProductType().getId(), savable.getTransferFromScheme().getName(), savable.getTransferToScheme().getName()) != null) {
            throw new IdenticalTransferException();
        }
        return repo.save(savable);
    }
    
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
    
    public void deleteByTransferToScheme(String schemeName) {
        repo.deleteByTransferToScheme_Name(schemeName);
    }
    
    public void deleteFulfillmentByTransferToScheme(String schemeName) {
        repo.deleteByTransferToScheme_NameAndProductType_AssetGroup(schemeName, TypeOfAssets.FULFILLMENT_GROUP);
    }
}
