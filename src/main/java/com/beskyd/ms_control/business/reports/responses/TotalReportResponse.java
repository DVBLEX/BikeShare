package com.beskyd.ms_control.business.reports.responses;

import com.beskyd.ms_control.business.reports.total.TotalPerPeriod;
import lombok.Data;

@Data
public class TotalReportResponse {
    private String scheme;
    private int count;

    public TotalReportResponse(TotalPerPeriod reports) {
        this.scheme = reports.getScheme();
        this.count = reports.getCnt();
    }
}
