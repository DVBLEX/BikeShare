package com.beskyd.ms_control.business.assetsprofiles;

import com.beskyd.ms_control.business.suppliers.Supplier;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Product implements JsonAware, Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Embedded
    private ProductIndex productId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "type_id")
    private TypeOfAssets type;

    @EqualsAndHashCode.Exclude
    private Integer minOrder;

    private Integer deliveryTime;

    public Product(Integer id, String productName, Supplier supplier, TypeOfAssets type, Integer minOrder, Integer deliveryTime) {
        this.id = id;

        productId = new ProductIndex();
        productId.setProductName(productName);
        productId.setSupplier(supplier);

        this.type = type;
        this.minOrder = minOrder;
        this.deliveryTime = deliveryTime;
    }
    
    public Product(String productName, Supplier supplier, TypeOfAssets type, Integer minOrder,  Integer deliveryTime) {
        productId = new ProductIndex();
        productId.setProductName(productName);
        productId.setSupplier(supplier);

        this.deliveryTime = deliveryTime;
        this.type = type;
        this.minOrder = minOrder;
    }

    @Embeddable
    @EqualsAndHashCode
    @ToString
    @Getter
    @Setter
    public static class ProductIndex implements Serializable{
        
        @NotNull
        private String productName;
        
        @NotNull
        @ManyToOne
        @JoinColumn(name = "supplier_id")
        private Supplier supplier;
    }

    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        
        jo.put("id", id);
        jo.put("productName", productId.getProductName());
        jo.put("supplier", productId.getSupplier().getName());
        jo.put("type", type.getId() + " " + type.getTypeName());
        jo.put("minOrder", minOrder);
        jo.put("deliveryTime", deliveryTime);
        return jo;
    }
}
