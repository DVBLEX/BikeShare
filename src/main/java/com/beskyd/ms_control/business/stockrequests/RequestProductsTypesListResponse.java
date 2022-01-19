package com.beskyd.ms_control.business.stockrequests;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RequestProductsTypesListResponse {

    private Integer idRequest;
    
    private TypeOfAssetsResponse productType;
    
    private int orderValue;

    public RequestProductsTypesListResponse(RequestProductsTypesList original) {
        this.idRequest = original.getIdRequest();
        this.productType = new TypeOfAssetsResponse(original.getProductType(), true);
        this.orderValue = original.getOrderValue();
    }
    
    public static List<RequestProductsTypesListResponse> createListFrom(List<RequestProductsTypesList> originals){
        List<RequestProductsTypesListResponse> list = new ArrayList<>();
        
        for(RequestProductsTypesList or : originals) {
            list.add(new RequestProductsTypesListResponse(or));
        }
        
        return list;
    }
    
    public static List<RequestProductsTypesList> toOriginals(List<RequestProductsTypesListResponse> responses){
        List<RequestProductsTypesList> originals = new ArrayList<>();
        
        for(RequestProductsTypesListResponse r : responses) {
            originals.add(new RequestProductsTypesList(null, r.getProductType().toOriginal(), r.getOrderValue()));
        }
        
        return originals;
    }
}
