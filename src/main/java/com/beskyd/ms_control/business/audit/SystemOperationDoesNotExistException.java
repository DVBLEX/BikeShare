package com.beskyd.ms_control.business.audit;


public class SystemOperationDoesNotExistException extends RuntimeException{

    public SystemOperationDoesNotExistException(int actionCode) {
        super("System operation with action code " + actionCode + " does not exist!");
    }

    
}
