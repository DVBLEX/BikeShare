package com.beskyd.ms_control.business.suppliers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {

    private Integer id;
    
    private String name;
    
    private String phone;
    
    private String email;
    
    private String contact;
    
    private String website;

    private Boolean miscellaneous;
    
    public SupplierResponse(Supplier original) {
        this.id = original.getId();
        this.name = original.getName();
        this.phone = original.getPhone();
        this.email = original.getEmail();
        this.contact = original.getContact();
        this.website = original.getWebsite();
        this.miscellaneous = original.getMiscellaneous();
    }
    
    public static List<SupplierResponse> createListFrom(List<Supplier> originals){
        List<SupplierResponse> list = new ArrayList<>();
        
        for(Supplier or : originals) {
            SupplierResponse r = new SupplierResponse(or);
            list.add(r);
        }
        
        return list;
    }

    public Supplier toOriginal() {
        return new Supplier(id, name, phone, email, contact, website, miscellaneous);
    }
   
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SupplierResponse other = (SupplierResponse) obj;
        return Objects.equals(id, other.id) && Objects.equals(name, other.name);
    }


    @Override
    public String toString() {
        return "SupplierResponse [id=" + id + ", name=" + name + ", phone=" + phone + ", email=" + email + ", contact=" + contact + ", website=" + website + "]";
    }
       
    
}
