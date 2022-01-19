package com.beskyd.ms_control.business.purchaseorders;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderProductsRepository extends JpaRepository<PurchaseOrderProducts, PurchaseOrderProducts.ComplexId>{
    
    List<PurchaseOrderProducts> findByIdPurchaseOrder(Integer idPurchaseOrder);
    
    @Transactional
    @Modifying
    void deleteByIdProductAndIdPurchaseOrder(Integer idProduct, Integer idPurchaseOrder);

}
