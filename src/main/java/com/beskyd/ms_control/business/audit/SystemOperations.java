package com.beskyd.ms_control.business.audit;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.json.JSONObject;

import com.beskyd.ms_control.config.addLogic.JsonAware;

@Entity
public class SystemOperations implements JsonAware{
    
    @Id
    private int actionCode;
    
    private String actionGroup;
    
    private String actionName;

    
    public SystemOperations() {
    }
    
    
    public SystemOperations(int actionCode, String actionGroup, String actionName) {
        this.actionCode = actionCode;
        this.actionGroup = actionGroup;
        this.actionName = actionName;
    }


    public int getActionCode() {
        return actionCode;
    }

    
    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    
    public String getActionGroup() {
        return actionGroup;
    }

    
    public void setActionGroup(String actionGroup) {
        this.actionGroup = actionGroup;
    }

    
    public String getActionName() {
        return actionName;
    }

    
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }


    @Override
    public int hashCode() {
        return Objects.hash(actionCode);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SystemOperations other = (SystemOperations) obj;
        return actionCode == other.actionCode;
    }


    @Override
    public String toString() {
        return "SystemOperations [actionCode=" + actionCode + ", actionGroup=" + actionGroup + ", actionName=" + actionName + "]";
    }


    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }


    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("actionCode", actionCode);
        jo.put("actionGroup", actionGroup);
        jo.put("actionName", actionName);
        return jo;
    }
    
    

}
