package com.beskyd.ms_control.business.balance;

import com.beskyd.ms_control.business.repairreports.entity.BikeStations;
import com.beskyd.ms_control.business.repairreports.entity.Bikes;
import com.beskyd.ms_control.business.repairreports.entity.RepairReports;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BikesLocationOnStreetRepository extends JpaRepository<BikesLocationOnStreet, Integer>{
    List<BikesLocationOnStreet> findBySchemeName(String schemeName, Sort sort);
    List<BikesLocationOnStreet> findBySchemeNameAndNumberIn(String schemeName, List<String> numbers, Sort sort);
    List<RepairReports> findDone(int stateId);

}
