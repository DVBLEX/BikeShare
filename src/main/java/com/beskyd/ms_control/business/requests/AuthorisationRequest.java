package com.beskyd.ms_control.business.requests;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter

public class AuthorisationRequest implements ParentRequest{

    private String token;

}
