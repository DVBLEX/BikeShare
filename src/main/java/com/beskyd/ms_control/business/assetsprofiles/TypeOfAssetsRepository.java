package com.beskyd.ms_control.business.assetsprofiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TypeOfAssetsRepository extends JpaRepository<TypeOfAssets, Integer>{

    TypeOfAssets findByGroupName(String typeName);
    
    @Modifying
    @Transactional
    void deleteByTypeName(String typeName);
}
