package com.beskyd.ms_control.business.requests;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ACVRequest implements ParentRequest{

    private TypeOfAssets productType;
    
    private Scheme scheme;
    
    private Integer quantity;

    public AssetsCurrentValues toAssetsCurrentValues() {
        return new AssetsCurrentValues(productType, scheme, quantity);
    }
}
