package com.beskyd.ms_control.business.schemestocksontrol.values;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AssetsTransferQueue implements Serializable, JsonAware{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private TypeOfAssets productType;
    
    @ManyToOne
    @JoinColumn(name = "transfer_from_scheme")
    private Scheme transferFromScheme;
    
    @ManyToOne
    @JoinColumn(name = "transfer_to_scheme")
    private Scheme transferToScheme;
    
    private Integer quantity;

    public AssetsTransferQueue(TypeOfAssets productType, Scheme transferFromScheme, Scheme transferToScheme, Integer quantity) {
        this.productType = productType;
        this.transferFromScheme = transferFromScheme;
        this.transferToScheme = transferToScheme;
        this.quantity = quantity;
    }
    
    /**
     * Copy constructor
     * @param atq - object to copy from
     */
    public AssetsTransferQueue(AssetsTransferQueue atq) {
        this.id = atq.getId();
        this.productType = atq.getProductType();
        this.transferFromScheme = atq.getTransferFromScheme();
        this.transferToScheme = atq.getTransferToScheme();
        this.quantity = atq.getQuantity();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("productTypeName", productType.getTypeName());
        jo.put("transferFromScheme", transferFromScheme.getName());
        jo.put("transferToScheme", transferToScheme.getName());
        jo.put("quantity", quantity);
        return jo;
    }
}
