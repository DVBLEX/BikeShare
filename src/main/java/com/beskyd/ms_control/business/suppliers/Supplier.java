package com.beskyd.ms_control.business.suppliers;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import com.beskyd.ms_control.config.addLogic.JsonAware;

@Entity
@Table(name = "suppliers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Supplier implements JsonAware, Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull
    private String name;
    
    private String phone;
    
    private String email;
    
    private String contact;
    
    private String website;

    private Boolean miscellaneous;
    
    @Override
    public String toString() {
        return "Supplier [id=" + id + ", name=" + name + ", phone=" + phone + ", email=" + email + ", contact=" + contact + ", website=" + website + ", miscellaneous=" + miscellaneous
            + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(contact, email, miscellaneous, id, name, phone, website);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Supplier other = (Supplier) obj;
        return Objects.equals(contact, other.contact) && Objects.equals(email, other.email) && miscellaneous == other.miscellaneous && Objects.equals(id, other.id) && Objects.equals(
            name, other.name) && Objects.equals(phone, other.phone) && Objects.equals(website, other.website);
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
        jo.put("phone", phone);
        jo.put("email", email);
        jo.put("contact", contact);
        jo.put("website", website);
        jo.put("fulfillment", miscellaneous);
        return jo;
    }
    
    
    
}
