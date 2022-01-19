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
public class AssetsTransferQueueResponse {
    private Integer id;
    
    private TypeOfAssetsResponse productType;
    
    private SchemeResponseItem transferFromScheme;
    
    private SchemeResponseItem transferToScheme;
    
    private Integer quantity;

    public AssetsTransferQueueResponse(AssetsTransferQueue original) {
        this.id = original.getId();
        this.productType = new TypeOfAssetsResponse(original.getProductType(), false);
        this.transferFromScheme = new SchemeResponseItem(original.getTransferFromScheme().getName());
        this.transferToScheme = new SchemeResponseItem(original.getTransferToScheme().getName());
        this.quantity = original.getQuantity();
    }

    public static List<AssetsTransferQueueResponse> createFromList(List<AssetsTransferQueue> originals){
        List<AssetsTransferQueueResponse> list = new ArrayList<>();
        
        for(AssetsTransferQueue or : originals) {
            list.add(new AssetsTransferQueueResponse(or));
        }
        
        return list;
    }
    
    public AssetsTransferQueue toOriginal() {
        return new AssetsTransferQueue(productType.toOriginal(), transferFromScheme.toOriginal(), transferToScheme.toOriginal(), quantity);
    }
    
    public static List<AssetsTransferQueue> toOriginals(List<AssetsTransferQueueResponse> responses){
        List<AssetsTransferQueue> list = new ArrayList<>();
        
        for(AssetsTransferQueueResponse resp : responses) {
            list.add(new AssetsTransferQueue(resp.toOriginal()));
        }
        
        return list;
    }
}
