package com.beskyd.ms_control.business.controller.exceptionhandler;

import java.util.Objects;

public class ExceptionResponseBody {
    
    private String message;

    
    public ExceptionResponseBody() {
    }


    public ExceptionResponseBody(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    
    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public int hashCode() {
        return Objects.hash(message);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExceptionResponseBody other = (ExceptionResponseBody) obj;
        return Objects.equals(message, other.message);
    }


    @Override
    public String toString() {
        return "ExceptionResponseBody [message=" + message + "]";
    }
    
    

}
