package com.beskyd.ms_control.business.repairreports.repo;

import com.beskyd.ms_control.business.repairreports.entity.RepairReportsOperators;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairReportsOperatorsRepository extends JpaRepository<RepairReportsOperators, Integer>{

}
