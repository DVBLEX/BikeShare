package com.beskyd.ms_control.business.balance;

import com.beskyd.ms_control.business.repairreports.entity.BikeStations;
import com.beskyd.ms_control.business.repairreports.entity.Bikes;
import com.beskyd.ms_control.business.repairreports.entity.RepairReports;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BikesLocationInDepotRepository extends JpaRepository<BikesLocationInDepot, Integer> {
    List<BikesLocationInDepot> findBySchemeName(String schemeName, Sort sort);

    List<BikesLocationInDepot> findBySchemeNameAndNumberIn(String schemeName, List<String> numbers, Sort sort);

    List<RepairReports> findNew(int stateId);

    List<BikesLocationInDepot> findBikesLocationInDepotBySchemeName(String schemeName, Sort sort);

    List<BikesLocationInDepot> findBikesLocationInDepotByBike_Id();

    List<BikesLocationInDepot> findBikesByScheme(String schemeName);

    List<BikesLocationInDepot> findBikesBySchemeAndNumbers(String schemeName, List<String> numbers,Sort sort);
}
