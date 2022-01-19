package com.beskyd.ms_control.business.schemestocksontrol.reports;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockUsageReportsRepository extends JpaRepository<StockUsageReports, Integer>{
    
    List<StockUsageReports> findByScheme_Name(String name, Sort sort);
}
