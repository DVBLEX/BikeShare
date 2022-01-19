package com.beskyd.ms_control.business.stockrequests;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRequestRepository extends JpaRepository<StockRequest, Integer>{
    
    List<StockRequest> findByScheme_Name(String name);

    List<StockRequest> findByState_Id(Integer stateId);

    @Query("select sr from StockRequest sr where sr.creationDate >= :date")
    List<StockRequest> findByRangeFromDate(@Param("date") Timestamp date);
}
