package com.beskyd.ms_control.business.audit;

import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.json.JSONObject;

import com.beskyd.ms_control.config.addLogic.JsonAware;

@Entity
public class OperationsLog implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "action_code")
    private SystemOperations operation;
    
    private String userEmail;
    
    private String dataObject;
    
    private Timestamp timeStamp;

    
    public OperationsLog() {
    }


    public OperationsLog(SystemOperations operation, String userEmail, JSONObject dataObject) {
        this.operation = operation;
        this.userEmail = userEmail;
        if(dataObject != null) {
            this.dataObject = dataObject.toString();
        }
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
        OperationsLog other = (OperationsLog) obj;
        return Objects.equals(id, other.id);
    }


    public Integer getId() {
        return id;
    }

    
    public void setId(Integer id) {
        this.id = id;
    }

    
    public SystemOperations getOperation() {
        return operation;
    }

    
    public void setOperation(SystemOperations operation) {
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
    public String toJSON() {
        return toJSONObject().toString();
    }


    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("operation", operation.getActionCode() + " " + operation.getActionGroup() + " " + operation.getActionName());
        jo.put("userEmail", userEmail);
        jo.put("dataObject", dataObject);
        jo.put("timeStamp", timeStamp);
        return jo;
    }
    
    
}
