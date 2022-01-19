package com.beskyd.ms_control.business.general;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

public class StatesResponse {

    private int id;
    
    private String name;
    
    private int type;
    
    @JsonFormat(pattern="dd/MM/YYYY HH:mm")
    private Timestamp stateChangeDate;
    
    public StatesResponse() {
    }


    public StatesResponse(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    
    public StatesResponse(int id, String name, int type, Timestamp stateChangeDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.stateChangeDate = stateChangeDate;
    }

    public StatesResponse(States original) {
        this.id = original.getId();
        this.name = original.getName();
        this.type = original.getType();
    }

    public static List<StatesResponse> createListFrom(List<States> originals){
        List<StatesResponse> list = new ArrayList<>();
        
        for(States or : originals) {
            list.add(new StatesResponse(or));
        }
        
        return list;
    }
    
    public States toOriginal() {
        return new States(id, name, type);
    }
    
    public int getId() {
        return id;
    }

    
    public void setId(int id) {
        this.id = id;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public int getType() {
        return type;
    }

    
    public void setType(int type) {
        this.type = type;
    }

    
    public Timestamp getStateChangeDate() {
        return stateChangeDate;
    }

    
    public void setStateChangeDate(Timestamp stateChangeDate) {
        this.stateChangeDate = stateChangeDate;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StatesResponse other = (StatesResponse) obj;
        return id == other.id && Objects.equals(name, other.name) && type == other.type;
    }
    
    
}
