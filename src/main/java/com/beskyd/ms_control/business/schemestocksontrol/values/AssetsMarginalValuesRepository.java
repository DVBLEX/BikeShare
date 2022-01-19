package com.beskyd.ms_control.business.schemestocksontrol.values;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AssetsMarginalValuesRepository extends JpaRepository<AssetsMarginalValues, AssetsMarginalValues.AMVId>{

    List<AssetsMarginalValues> findByScheme_Name(String name, Sort sort);
    
    @Transactional
    @Modifying
    @Query("delete from AssetsMarginalValues where productTypeId=:productTypeId")
    void deleteByProductTypeId(@Param("productTypeId") Integer productTypeId);
    
    
}
