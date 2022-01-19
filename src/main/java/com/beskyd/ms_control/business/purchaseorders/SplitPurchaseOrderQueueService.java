package com.beskyd.ms_control.business.purchaseorders;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;

@Service
public class SplitPurchaseOrderQueueService {
    
    private final SplitPurchaseOrderQueueRepository repo;
    
    @Inject
    public SplitPurchaseOrderQueueService(SplitPurchaseOrderQueueRepository repo) {
        this.repo = repo;
    }
    
    public List<SplitPurchaseOrderQueue> findAll(){
        return repo.findAll();
    }
    
    public List<SplitPurchaseOrderQueue> findFulfillmentQueue(){
        return repo.findByProduct_Type_AssetGroup(TypeOfAssets.FULFILLMENT_GROUP);
    }
    
    /**
     * Find {@link SplitPurchaseOrderQueue} by id
     * @param id
     * @return object, if present, null - if not
     */
    public SplitPurchaseOrderQueue findById(Integer id) {
        return repo.findById(id).orElse(null);
    }
    
    public Integer save(SplitPurchaseOrderQueue savable) {
        return repo.save(savable).getId();
    }
    
    public void delete(Integer id) {
        repo.deleteById(id);
    }
    
    public void deleteAll() {
        repo.deleteAll();
    }
    
    public void deleteAllFulfillment() {
        repo.deleteByProduct_Type_AssetGroup(TypeOfAssets.FULFILLMENT_GROUP);
    }
}
