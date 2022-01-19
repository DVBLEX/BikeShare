package com.beskyd.ms_control.business.requests;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class ProdTypeIdAndSchemeNameRequest implements ParentRequest{
    
    private Integer productTypeId;
    
    private String schemeName;

}
