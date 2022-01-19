package com.beskyd.ms_control.business.requests;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class DeleteSupplierRequest implements ParentRequest{
    
    private String supplierName;

}
