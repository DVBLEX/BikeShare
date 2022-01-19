package com.beskyd.ms_control.business.schemestocksontrol.centraldepot;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CentralDepot implements JsonAware{

    @Id
    private Integer productTypeId;
    
    @MapsId
    @OneToOne
    @JoinColumn(name = "product_type_id")
    @EqualsAndHashCode.Exclude
    private TypeOfAssets productType;

    @EqualsAndHashCode.Exclude
    private Integer amount;



    public CentralDepot(TypeOfAssets productType, Integer amount) {
        if(productType != null) {
            this.productTypeId = productType.getId();
        }
        this.productType = productType;
        this.amount = amount;
    }


    /**
     * Besides setting of {@code productType}, also sets {@code productTypeId}
     * @param productType
     */
    public void setProductType(TypeOfAssets productType) {
        this.productType = productType;
        if(productType != null) {
            this.productTypeId = productType.getId();
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("productTypeName", productType.getTypeName());
        jo.put("amount", amount);
        return jo;
    }
}
