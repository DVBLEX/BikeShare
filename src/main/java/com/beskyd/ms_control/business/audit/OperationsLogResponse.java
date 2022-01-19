package com.beskyd.ms_control.business.audit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OperationsLogResponse {
    private Integer id;
    
    private SystemOperationsResponse operation;
    
    private String userEmail;
    
    private String dataObject;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private Timestamp timeStamp;

    
    public OperationsLogResponse() {
    }


    public OperationsLogResponse(Integer id, SystemOperationsResponse operation, String userEmail, String dataObject, Timestamp timeStamp) {
        this.id = id;
        this.operation = operation;
        this.userEmail = userEmail;
        this.dataObject = dataObject;
        this.timeStamp = timeStamp;
    }

    public OperationsLogResponse(OperationsLog original) {
        this.id = original.getId();
        this.operation = new SystemOperationsResponse(original.getOperation());
        this.userEmail = original.getUserEmail();
        this.dataObject = original.getDataObject();
        this.timeStamp = original.getTimeStamp();
    }

    public static List<OperationsLogResponse> createListFrom(List<OperationsLog> originals){
        List<OperationsLogResponse> list = new ArrayList<>();
        
        for(OperationsLog or : originals) {
            list.add(new OperationsLogResponse(or));
        }
        
        return list;
    }
    
    public Integer getId() {
        return id;
    }

    
    public void setId(Integer id) {
        this.id = id;
    }

    
    public SystemOperationsResponse getOperation() {
        return operation;
    }

    
    public void setOperation(SystemOperationsResponse operation) {
        this.operation = operation;
    }

    
    public String getUserEmail() {
        return userEmail;
    }

    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    
    public String getDataObject() {
        return dataObject;
    }

    
    public void setDataObject(String dataObject) {
        this.dataObject = dataObject;
    }

    
    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    
    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OperationsLogResponse other = (OperationsLogResponse) obj;
        return Objects.equals(id, other.id);
    }


    @Override
    public String toString() {
        return "OperationsLogResponse [id=" + id + ", operation=" + operation + ", userEmail=" + userEmail + ", dataObject=" + dataObject + ", timeStamp=" + timeStamp + "]";
    }
    
    
}
