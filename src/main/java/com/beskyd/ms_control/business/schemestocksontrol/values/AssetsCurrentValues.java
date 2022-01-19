package com.beskyd.ms_control.business.schemestocksontrol.values;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(AssetsCurrentValues.ACVId.class)
public class AssetsCurrentValues implements Serializable, JsonAware{
    
    @Id
    private Integer productTypeId;
    
    @Id
    @Column(name = "`scheme`")
    private String schemeName;
    
    @MapsId("productTypeId")
    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private TypeOfAssets productType;
    
    @MapsId("schemeName")
    @ManyToOne
    @JoinColumn(name = "`scheme`")
    private Scheme scheme;
    
    private Integer quantity;

    public AssetsCurrentValues(TypeOfAssets productType, Scheme scheme, Integer quantity) {
        this.productTypeId = productType.getId();
        this.productType = productType;
        this.schemeName = scheme.getName();
        this.scheme = scheme;
        this.quantity = quantity;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    @Setter
    public static class ACVId implements Serializable{
        private static final long serialVersionUID = 1L;

        @Column(name = "product_type_id")
        private Integer productTypeId;
        
        @Column(name = "`scheme`")
        private String schemeName;
    }
    
    public void setProductType(TypeOfAssets productType) {
        this.productTypeId = productType.getId();
        this.productType = productType;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("productTypeId", productTypeId);
        jo.put("productType", productType.toJSONObject());
        jo.put("quantity", quantity);
        jo.put("schemeName", schemeName);
        return jo;
    }

}
