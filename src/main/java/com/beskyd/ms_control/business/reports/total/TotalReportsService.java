package com.beskyd.ms_control.business.reports.total;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public final class TotalReportsService {
    private final TotalRepairReportsRepository totalRepairReportsRepository;
    private final TotalRoutineReviewsRepository totalRoutineReviewsRepository;

    public List<TotalPerPeriod> getTotalRepairReports(LocalDate startDate, LocalDate endDate) {
        return totalRepairReportsRepository.getTotalGroupedByScheme(Timestamp.valueOf(startDate.atStartOfDay()),
                Timestamp.valueOf(endDate.atStartOfDay().plusDays(1)));
    }

    public List<TotalPerPeriod> getTotalRoutineReviews(LocalDate startDate, LocalDate endDate) {
        return totalRoutineReviewsRepository.getTotalGroupedByScheme(Timestamp.valueOf(startDate.atStartOfDay()),
                Timestamp.valueOf(endDate.atStartOfDay().plusDays(1)));
    }
}
