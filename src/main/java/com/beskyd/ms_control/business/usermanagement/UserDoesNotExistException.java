package com.beskyd.ms_control.business.usermanagement;


public class UserDoesNotExistException extends RuntimeException{

    public UserDoesNotExistException() {
        super ("Requested user does not exist!");
    }
    
    public UserDoesNotExistException(String userEmail) {
        super("User with email " + userEmail + " does not exist!");
    }
    
}
