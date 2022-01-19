package com.beskyd.ms_control.business.reports.total;

import com.beskyd.ms_control.business.repairreports.entity.RepairReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TotalRepairReportsRepository extends JpaRepository<RepairReports, Integer> {
    @Query(value = "select bs.scheme.name AS scheme, COUNT(rr) AS cnt" +
            " from RepairReports rr" +
            " left join BikeStations bs on rr.location.id = bs.id" +
            " where rr.reportDate >= :start_date and rr.reportDate <= :end_date" +
            " group by bs.scheme.name")
    List<TotalPerPeriod> getTotalGroupedByScheme(@Param("start_date") Timestamp startDate,
                                                 @Param("end_date") Timestamp endDate);
}
