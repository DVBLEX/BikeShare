package com.beskyd.ms_control.business.general;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONObject;

import com.beskyd.ms_control.config.addLogic.JsonAware;

@Entity
@Table(name = "`schemes`")
public class Scheme implements Serializable, JsonAware{
    
    @Id
    private String name;
    
    public Scheme() {
    }

    public Scheme(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Scheme other = (Scheme) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }


    @Override
    public String toString() {
        return "Scheme [name=" + name + "]";
    }

    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("name", name);
        return jo;
    }
}
