package com.beskyd.ms_control.business.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class LogsFilterRequest implements ParentRequest{
    
    private List<String> userEmails;
    
    private List<String> groups;
    
    private String orderNumber;
    
    private Long startDateMilis;
    
    private Long endDateMilis;
}
