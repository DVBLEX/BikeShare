package com.beskyd.ms_control.business.schemestocksontrol.values;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RequestTransferResponse {

    private AssetsTransferQueue saved;

    @EqualsAndHashCode.Exclude
    private String errorMessage;
}
