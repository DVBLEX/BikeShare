package com.beskyd.ms_control.business.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ACVReduceRequest implements ParentRequest, Serializable{
    
    private Integer productTypeId;
    
    private String schemeName;
    
    private Integer minusQuantity;
}
