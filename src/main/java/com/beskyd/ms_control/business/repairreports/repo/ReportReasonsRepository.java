package com.beskyd.ms_control.business.repairreports.repo;

import com.beskyd.ms_control.business.repairreports.entity.ReportReasons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportReasonsRepository extends JpaRepository<ReportReasons, Integer>{

}
