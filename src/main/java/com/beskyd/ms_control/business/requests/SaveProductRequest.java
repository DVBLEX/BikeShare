package com.beskyd.ms_control.business.requests;

import com.beskyd.ms_control.business.assetsprofiles.Product;
import lombok.*;
import org.json.JSONObject;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class SaveProductRequest implements ParentRequest, Serializable{
    
    @NotNull
    private Product savableProduct;
    
    private Product oldProduct;

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("savableProduct", savableProduct.toJSONObject());
        if(oldProduct != null) {
            jo.put("oldProduct", oldProduct.toJSONObject());
        }
        return jo;
    }
}
