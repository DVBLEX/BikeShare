package com.beskyd.ms_control.business.reports.total;

import com.beskyd.ms_control.business.repairreports.entity.RoutineReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TotalRoutineReviewsRepository extends JpaRepository<RoutineReview, Integer> {
    @Query(value = "select bs.scheme.name as scheme, count (rr) as cnt" +
            " from RoutineReview rr" +
            " left join BikeStations bs on rr.station.id = bs.id" +
            " where rr.creationDate >= :start_date and rr.creationDate <= :end_date" +
            " group by bs.scheme.name")
    List<TotalPerPeriod> getTotalGroupedByScheme(@Param("start_date") Timestamp startDate,
                                                 @Param("end_date") Timestamp endDate);
}
