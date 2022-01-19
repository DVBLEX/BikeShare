package com.beskyd.ms_control.business.requests;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserCredentialsRequest implements Serializable, ParentRequest{

    @NotNull
    private String userEmail;
    
    private String rawPassword;

}
