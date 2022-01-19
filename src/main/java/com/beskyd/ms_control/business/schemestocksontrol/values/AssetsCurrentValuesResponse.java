package com.beskyd.ms_control.business.schemestocksontrol.values;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsResponse;
import com.beskyd.ms_control.business.general.SchemeResponseItem;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AssetsCurrentValuesResponse {
    private TypeOfAssetsResponse productType;
    
    private SchemeResponseItem scheme;
    
    private Integer quantity;

    
    public AssetsCurrentValuesResponse(AssetsCurrentValues original) {
        this.productType = new TypeOfAssetsResponse(original.getProductType(), false);
        this.scheme = new SchemeResponseItem(original.getSchemeName());
        this.quantity = original.getQuantity();
    }
    
    public static List<AssetsCurrentValuesResponse> createListFrom(List<AssetsCurrentValues> originals){
        List<AssetsCurrentValuesResponse> list = new ArrayList<>();
        
        for(AssetsCurrentValues or : originals) {
            list.add(new AssetsCurrentValuesResponse(or));
        }
        
        return list;
    }
}
