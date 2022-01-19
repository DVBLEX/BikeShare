package com.beskyd.ms_control.business.usermanagement;


public class UserAlreadyExistsException extends Exception{

    public UserAlreadyExistsException() {
        super("User with this User Name already exists!");
    }
    
}
