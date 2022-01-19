package com.beskyd.ms_control.business.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SystemParametersResponse {
    
    private String parameterName;
    
    private String parameterValue;

    
    public SystemParametersResponse() {
    }


    public SystemParametersResponse(String parameterName, String parameterValue) {
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
    }

    public SystemParametersResponse(SystemParameters original) {
        this.parameterName = original.getParameterName();
        this.parameterValue = original.getParameterValue();
    }
    
    public SystemParameters toOriginal() {
        return new SystemParameters(parameterName, parameterValue);
    }
    
    public static List<SystemParameters> toOriginals(List<SystemParametersResponse> responses){
        List<SystemParameters> list = new ArrayList<>();
        
        for(var resp : responses) {
            list.add(new SystemParameters(resp.getParameterName(), resp.getParameterValue()));
        }
        
        return list;
    }

    public String getParameterName() {
        return parameterName;
    }

    
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    
    public String getParameterValue() {
        return parameterValue;
    }

    
    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }


    @Override
    public int hashCode() {
        return Objects.hash(parameterName, parameterValue);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SystemParametersResponse other = (SystemParametersResponse) obj;
        return Objects.equals(parameterName, other.parameterName) && Objects.equals(parameterValue, other.parameterValue);
    }


    @Override
    public String toString() {
        return "SystemParametersResponse [parameterName=" + parameterName + ", parameterValue=" + parameterValue + "]";
    }
    
    

}
