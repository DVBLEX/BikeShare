package com.beskyd.ms_control.business.purchaseorders;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

@Service
public class PurchaseOrderProductsService {

    private PurchaseOrderProductsRepository repo;
    
    @Inject
    public PurchaseOrderProductsService(PurchaseOrderProductsRepository repo) {
        this.repo = repo;
    }
    
    /**
     * Find {@link PurchaseOrderProducts} by product id and purchase order id
     * @param productId
     * @param orderId
     * @return object if found, null - if not
     */
    public PurchaseOrderProducts findByOrderAndProduct(Integer productId, Integer orderId){
        return repo.findById(new PurchaseOrderProducts.ComplexId(orderId, productId)).orElse(null);
    }
    
    public List<PurchaseOrderProducts> findByOrderId(Integer orderId){
        return repo.findByIdPurchaseOrder(orderId);
    }
    
    public void saveAll(List<PurchaseOrderProducts> list) {
        repo.saveAll(list);
    }
    
    public void addProductToOrder(PurchaseOrderProducts product) throws ProductAlreadyExistsInOrder {
        if(findByOrderAndProduct(product.getProduct().getId(), product.getPurchaseOrder().getId()) != null) {
            throw new ProductAlreadyExistsInOrder(product.getProduct().getId(), product.getPurchaseOrder().getId());
        }
        
        repo.save(product);
    }
    
    public void save(PurchaseOrderProducts product) {
        repo.save(product);
    }
    
    public void removeByProductIdAndOrderId(Integer productId, Integer orderId) {
        repo.deleteByIdProductAndIdPurchaseOrder(productId, orderId);
    }
}
