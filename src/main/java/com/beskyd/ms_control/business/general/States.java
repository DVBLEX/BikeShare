package com.beskyd.ms_control.business.general;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.json.JSONObject;

import com.beskyd.ms_control.config.addLogic.JsonAware;

@Entity
public class States implements JsonAware{

    @Id
    private int id;
    
    private String name;
    
    private int type;

        
    public States() {
    }


    public States(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
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
        States other = (States) obj;
        return id == other.id;
    }


    @Override
    public String toString() {
        return "States [id=" + id + ", name=" + name + ", type=" + type + "]";
    }


    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }


    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("name", name);
        jo.put("type", type);
        return jo;
    }
    
    
}
