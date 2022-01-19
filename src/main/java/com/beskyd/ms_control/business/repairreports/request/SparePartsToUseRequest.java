package com.beskyd.ms_control.business.repairreports.request;

import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesResponse;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SparePartsToUseRequest {
    
    private AssetsCurrentValuesResponse sparePart;
    
    private int amount;
}
