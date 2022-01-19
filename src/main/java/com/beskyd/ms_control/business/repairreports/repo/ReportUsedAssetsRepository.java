package com.beskyd.ms_control.business.repairreports.repo;

import com.beskyd.ms_control.business.repairreports.entity.ReportUsedAssets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReportUsedAssetsRepository extends JpaRepository<ReportUsedAssets, ReportUsedAssets.ComplexId>{

    @Modifying
    @Transactional
    @Query("delete from ReportUsedAssets rua where rua.repairOperatorId=:repairOperatorId")
    void deleteByRepairOperatorId(@Param("repairOperatorId") int repairOperatorId);
    
    @Modifying
    @Transactional
    @Query("delete from ReportUsedAssets rua where rua.repairOperatorId=:repairOperatorId and rua.productType.id=:typeId")
    void deleteByRepairOperatorIdAndTypeId(@Param("repairOperatorId") int repairOperatorId, @Param("typeId") int typeId);
}
