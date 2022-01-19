package com.beskyd.ms_control.business.schemestocksontrol.reports;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockUsageReportAssetsRepository extends JpaRepository<StockUsageReportAssets, StockUsageReportAssets.ComplexId>{

}
