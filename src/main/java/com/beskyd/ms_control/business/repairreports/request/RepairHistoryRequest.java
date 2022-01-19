package com.beskyd.ms_control.business.repairreports.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RepairHistoryRequest {
    
    private String schemeName;
    
    private String bikeNumber;
    
    private String location;
}
