package com.beskyd.ms_control.business.schemestocksontrol.values;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AssetsTransferQueueRepository extends JpaRepository<AssetsTransferQueue, Integer>{

    AssetsTransferQueue findByProductType_IdAndTransferFromScheme_NameAndTransferToScheme_Name(Integer productId, 
        String transferFromSchemeName, String transferToSchemeName);
    
    @Transactional
    @Modifying
    void deleteByTransferToScheme_Name(String name);
    
    @Transactional
    @Modifying
    void deleteByTransferToScheme_NameAndProductType_AssetGroup(String name, String assetGroup);
}
