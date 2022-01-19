package com.beskyd.ms_control.business.schemestocksontrol.values;

import com.beskyd.ms_control.business.general.Scheme;

public class TransferToTheSameSchemeException extends Exception{

    public TransferToTheSameSchemeException(Scheme scheme) {
        super("You are attempting to transfer to the same scheme " + scheme.getName() + ". This is not allowed.");
    }

    
}
