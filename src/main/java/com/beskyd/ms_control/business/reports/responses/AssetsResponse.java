package com.beskyd.ms_control.business.reports.responses;

import com.beskyd.ms_control.business.reports.assets.AssetsReport;
import lombok.Data;

@Data
public class AssetsResponse {
    private String name;
    private int acquired;
    private int ordered;

    public AssetsResponse(AssetsReport report) {
        this.name = report.getName();
        this.acquired = report.getAcquired() == null ? 0 : report.getAcquired();
        this.ordered = report.getOrdered() == null ? 0 : report.getOrdered();
    }
}
