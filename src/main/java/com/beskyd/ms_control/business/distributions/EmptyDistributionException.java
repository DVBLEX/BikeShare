package com.beskyd.ms_control.business.distributions;


public class EmptyDistributionException extends RuntimeException{

    public EmptyDistributionException() {
        super("Distribution with empty assets list!");
    }
    
}
