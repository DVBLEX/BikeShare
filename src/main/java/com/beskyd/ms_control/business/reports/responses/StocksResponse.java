package com.beskyd.ms_control.business.reports.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StocksResponse {
    private String name;
    private int requested;
    private int distributed;
}
