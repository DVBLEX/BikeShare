package com.beskyd.ms_control.business.purchaseorders;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer>{
    
    List<PurchaseOrder> findBySupplier_IdAndState_Id(Integer supplierId, Integer stateId);

}
