package com.beskyd.ms_control.business.schemestocksontrol.values;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsResponse;
import com.beskyd.ms_control.business.general.SchemeResponseItem;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Setter
@Getter
public class AssetsMarginalValuesResponse {
    private TypeOfAssetsResponse productType;

    private int orderValue;
    
    private int lowerValue;

    private Integer trigger;

    private SchemeResponseItem scheme;

    
    public AssetsMarginalValuesResponse(AssetsMarginalValues original) {
        this.productType = new TypeOfAssetsResponse(original.getProductType(), false);
        this.orderValue = original.getOrderValue();
        this.lowerValue = original.getLowerValue();
        this.scheme = new SchemeResponseItem(original.getSchemeName());
        this.trigger = original.getTrigger();
    }
    
    public static List<AssetsMarginalValuesResponse> createListFrom(List<AssetsMarginalValues> originals){
        List<AssetsMarginalValuesResponse> list = new ArrayList<>();
        
        for(AssetsMarginalValues or : originals) {
            list.add(new AssetsMarginalValuesResponse(or));
        }
        
        return list;
    }
}
