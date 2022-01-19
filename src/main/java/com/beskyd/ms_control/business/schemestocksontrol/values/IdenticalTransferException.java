package com.beskyd.ms_control.business.schemestocksontrol.values;


public class IdenticalTransferException extends Exception{

    public IdenticalTransferException() {
        super("There is already the same transfer in transfer queue");
    }

}
