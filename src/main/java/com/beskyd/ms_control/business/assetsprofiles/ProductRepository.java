package com.beskyd.ms_control.business.assetsprofiles;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>{
    
    List<Product> findByType_Id(Integer typeId);
    
    List<Product> findByType_GroupNameAndProductId_ProductNameAndProductId_Supplier_Id(String groupName, String productName, Integer supplierId);
    
    @Query("select p from Product p where p.productId.productName like CONCAT('%', :nameFilter, '%')")
    List<Product> filterByName(@Param("nameFilter") String name, Pageable pageable);
    

    @Query("select count(p.id) from Product p where p.productId.productName like CONCAT('%', :nameFilter, '%')")
    Long countByNameFilter(@Param("nameFilter") String name);
}
