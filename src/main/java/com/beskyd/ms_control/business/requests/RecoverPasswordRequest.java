package com.beskyd.ms_control.business.requests;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
public class RecoverPasswordRequest implements Serializable, ParentRequest{
    
    @NotNull
    private String token;
    
    @NotNull
    private String newPassword;
}
