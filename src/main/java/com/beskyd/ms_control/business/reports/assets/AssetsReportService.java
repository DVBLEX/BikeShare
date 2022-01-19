package com.beskyd.ms_control.business.reports.assets;

import com.beskyd.ms_control.business.reports.responses.AssetsResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public final class AssetsReportService {
    private final AssetsReportRepository assetsReportRepository;

    public List<AssetsResponse> getReport(LocalDate startDate, LocalDate endDate) {
        List<AssetsReport> assetsReportList = assetsReportRepository
                .findByRange(Timestamp.valueOf(startDate.atStartOfDay()),
                        Timestamp.valueOf(endDate.atStartOfDay().plusDays(1)));
        return assetsReportList.stream().map(AssetsResponse::new).collect(Collectors.toList());
    }
}
