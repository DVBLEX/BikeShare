package com.beskyd.ms_control.business.requests;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValues;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AMVRequest implements ParentRequest {
    
    private TypeOfAssets productType;

    private int orderValue;
    
    private int lowerValue;

    private int trigger;

    private Scheme scheme;

    public AssetsMarginalValues toAssetsMarginalValues() {
        return new AssetsMarginalValues(productType, orderValue, lowerValue, trigger, scheme);
    }
}
