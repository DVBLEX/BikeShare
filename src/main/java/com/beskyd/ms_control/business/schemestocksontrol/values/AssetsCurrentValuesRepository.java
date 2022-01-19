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
public interface AssetsCurrentValuesRepository extends JpaRepository<AssetsCurrentValues, AssetsCurrentValues.ACVId>{
    
    List<AssetsCurrentValues> findBySchemeName(String name, Sort sort);
    
    List<AssetsCurrentValues> findByProductTypeId(Integer id, Sort sort);
    
    @Query("select acv from AssetsCurrentValues acv where acv.quantity <= (select amv.lowerValue from AssetsMarginalValues amv where amv.productTypeId=acv.productTypeId "
        + "and amv.scheme.name=acv.scheme.name) * (1 + (select p.parameterValue from SystemParameters p where p.parameterName='low_stock_percentage')/100)")
    List<AssetsCurrentValues> findAssetsWithLowStock();

    @Transactional
    @Modifying
    @Query("delete from AssetsCurrentValues where productTypeId=:productTypeId")
    void deleteByProductTypeId(@Param("productTypeId") Integer productTypeId);
    
    @Transactional
    @Modifying
    @Query(value = "update assets_current_values set quantity=quantity-:minusQuantity where product_type_id=:ptId and scheme=:schemeName", nativeQuery = true)
    void reduce(@Param("minusQuantity") int minusQuantity, @Param("ptId") int productTypeId, @Param("schemeName") String schemeName);
}
