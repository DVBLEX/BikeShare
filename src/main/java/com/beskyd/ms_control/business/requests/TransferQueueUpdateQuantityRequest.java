package com.beskyd.ms_control.business.requests;

import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@EqualsAndHashCode
public class TransferQueueUpdateQuantityRequest implements JsonAware{

    private Integer id;

    @EqualsAndHashCode.Exclude
    private Integer quantity;
}
