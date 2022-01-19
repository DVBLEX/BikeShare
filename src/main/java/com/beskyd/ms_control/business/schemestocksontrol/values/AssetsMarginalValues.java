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
@IdClass(AssetsMarginalValues.AMVId.class)
public class AssetsMarginalValues implements JsonAware, Serializable{
    
    @Id
    private Integer productTypeId;
    
    @Id
    @Column(name = "`scheme`")
    private String schemeName;
    
    @MapsId("productTypeId")
    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private TypeOfAssets productType;

    private int orderValue;
    
    private int lowerValue;

    @Column(name = "`trigger`")
    private Integer trigger;

    @MapsId("schemeName")
    @ManyToOne
    @JoinColumn(name = "`scheme`")
    private Scheme scheme;
    
    public AssetsMarginalValues(TypeOfAssets productType, int orderValue, int lowerValue, int trigger, Scheme scheme) {
        this.productTypeId = productType.getId();
        this.productType = productType;
        this.orderValue = orderValue;
        this.lowerValue = lowerValue;
        this.trigger = trigger;
        this.schemeName = scheme.getName();
        this.scheme = scheme;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class AMVId implements Serializable{
        
        @Column(name = "product_type_id")
        private Integer productTypeId;
        
        @Column(name = "`scheme`")
        private String schemeName;
    }
    
    /**
     * Also sets productTypeId. May throw {@link NullPointerException}, if {@code productType} is null
     * @param productType
     */
    public void setProductType(TypeOfAssets productType) {
        this.productTypeId = productType.getId();
        this.productType = productType;
    }

    /**
     * Also sets schemeName. May throw {@link NullPointerException}, if {@code scheme} is null
     * @param scheme
     */
    public void setScheme(Scheme scheme) {
        this.schemeName = scheme.getName();
        this.scheme = scheme;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("productTypeId", productTypeId);
        jo.put("productTypeName", productType.getTypeName());
        jo.put("orderValue", orderValue);
        jo.put("lowerValue", lowerValue);
        jo.put("schemeName", schemeName);
        return jo;
    }
}
