package com.beskyd.ms_control.business.requests;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class DeleteUserRequest implements ParentRequest{
    
    private String userEmail;
    
}
