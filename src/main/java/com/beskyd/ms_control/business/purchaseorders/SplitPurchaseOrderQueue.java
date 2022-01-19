package com.beskyd.ms_control.business.purchaseorders;

import com.beskyd.ms_control.business.assetsprofiles.Product;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "split_purchase_orders_queue")
public class SplitPurchaseOrderQueue implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;
    
    private Integer quantity;
    
    private Integer oldOrderId;
        

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("product", product.getType().getTypeName() + " " + product.getProductId().getProductName());
        jo.put("quantity", quantity);
        jo.put("oldOrderId", oldOrderId);
        return jo;
    }

    
}
