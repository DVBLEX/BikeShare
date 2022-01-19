package com.beskyd.ms_control.business.general;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.json.JSONObject;

import com.beskyd.ms_control.config.addLogic.JsonAware;

@Entity
public class SystemParameters implements JsonAware{

    @Id
    private String parameterName;
    
    private String parameterValue;

    
    public SystemParameters() {
    }


    public SystemParameters(String parameterName, String parameterValue) {
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
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
        SystemParameters other = (SystemParameters) obj;
        return Objects.equals(parameterName, other.parameterName) && Objects.equals(parameterValue, other.parameterValue);
    }


    @Override
    public String toString() {
        return "SystemParameters [parameterName=" + parameterName + ", parameterValue=" + parameterValue + "]";
    }


    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }


    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("parameterName", parameterName);
        jo.put("parameterValue", parameterValue);
        return jo;
    }
    
    
}
