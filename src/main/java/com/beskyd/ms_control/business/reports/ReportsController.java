package com.beskyd.ms_control.business.reports;

import com.beskyd.ms_control.business.reports.assets.AssetsReportService;
import com.beskyd.ms_control.business.reports.responses.AssetsResponse;
import com.beskyd.ms_control.business.reports.responses.StocksResponse;
import com.beskyd.ms_control.business.reports.responses.TotalReportResponse;
import com.beskyd.ms_control.business.reports.stocks.StocksReportService;
import com.beskyd.ms_control.business.reports.total.TotalPerPeriod;
import com.beskyd.ms_control.business.reports.total.TotalReportsService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/msc-api/reports")
@AllArgsConstructor
public final class ReportsController {
    private final AssetsReportService assetsReportService;
    private final StocksReportService stocksReportService;
    private final TotalReportsService totalReportsService;

    @GetMapping("/total/{reportType}")
    public List<TotalReportResponse> getTotalReports(
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PathVariable String reportType
    ) {
        List<TotalPerPeriod> totalPerPeriodList;
        if ("RepairReports".equals(reportType)) {
            totalPerPeriodList = totalReportsService.getTotalRepairReports(startDate, endDate);
        } else if ("RoutineReviews".equals(reportType)) {
            totalPerPeriodList = totalReportsService.getTotalRoutineReviews(startDate, endDate);
        } else {
            throw new ReportNotFoundException(reportType);
        }
        return totalPerPeriodList.stream().map(TotalReportResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/stocksRequestedDistributed")
    public List<StocksResponse> getStocksReport(
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return stocksReportService.getReport(startDate, endDate);
    }

    @GetMapping("/assetsAcquiredOrdered")
    public List<AssetsResponse> getAssetsReport(
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return assetsReportService.getReport(startDate, endDate);
    }
}
