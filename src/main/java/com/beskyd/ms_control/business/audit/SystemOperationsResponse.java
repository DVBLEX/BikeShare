package com.beskyd.ms_control.business.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SystemOperationsResponse {

    private int actionCode;
    
    private String actionGroup;
    
    private String actionName;

    public SystemOperationsResponse(int actionCode, String actionGroup, String actionName) {
        this.actionCode = actionCode;
        this.actionGroup = actionGroup;
        this.actionName = actionName;
    }

    public SystemOperationsResponse() {
    }
    
    public SystemOperationsResponse(SystemOperations original) {
        this.actionCode = original.getActionCode();
        this.actionGroup = original.getActionGroup();
        this.actionName = original.getActionName();
    }

    public SystemOperations toOriginal() {
        return new SystemOperations(actionCode, actionGroup, actionName);
    }
    
    public static List<SystemOperationsResponse> createListFrom(List<SystemOperations> originals){
        List<SystemOperationsResponse> list = new ArrayList<>();
        
        for(SystemOperations or : originals) {
            list.add(new SystemOperationsResponse(or));
        }
        
        return list;
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
        return Objects.hash(actionCode, actionGroup, actionName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SystemOperationsResponse other = (SystemOperationsResponse) obj;
        return actionCode == other.actionCode && Objects.equals(actionGroup, other.actionGroup) && Objects.equals(actionName, other.actionName);
    }

    @Override
    public String toString() {
        return "SystemOperationsResponse [actionCode=" + actionCode + ", actionGroup=" + actionGroup + ", actionName=" + actionName + "]";
    }
    
    
}
