package com.beskyd.ms_control.business.purchaseorders;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface SplitPurchaseOrderQueueRepository extends JpaRepository<SplitPurchaseOrderQueue, Integer>{

    List<SplitPurchaseOrderQueue> findByProduct_Type_AssetGroup(String group);
    
    @Transactional
    @Modifying
    void deleteByProduct_Type_AssetGroup(String group);
}