package com.beskyd.ms_control.business.repairreports.repo;

import com.beskyd.ms_control.business.repairreports.entity.RepairReports;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairReportsRepository extends JpaRepository<RepairReports, Integer> {

    List<RepairReports> findByLocation_Scheme_Name(String schemeName, Sort sort);
    
    List<RepairReports> findByLocation_Scheme_NameAndState_Id(String schemeName, int id, Sort sort);
    
    List<RepairReports> findByLocation_Scheme_NameAndLocation_Location(String schemeName, String locationName);
    
    List<RepairReports> findByBike_Scheme_NameAndBike_Number(String schemeName, String number);

    List<RepairReports> findByStateId(int stateId);
}
