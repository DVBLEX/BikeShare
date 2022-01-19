package com.beskyd.ms_control.business.distributions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DistributionAssetsRepository extends JpaRepository<DistributionAssets, DistributionAssets.ComplexId> {

    List<DistributionAssets> findByDistId(@NotNull Integer id);

    @Query("select da from DistributionAssets da" +
            " inner join Distribution d on d.creationDate between :start_date and :end_date")
    List<DistributionAssets> findByRange(@Param("start_date") Timestamp startDate,
                                         @Param("end_date") Timestamp endDate);
}
