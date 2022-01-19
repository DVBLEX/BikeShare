package com.beskyd.ms_control.business.purchaseorders;

import com.beskyd.ms_control.business.assetsprofiles.ProductResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SplitPurchaseOrderQueueResponse {
    
    private Integer id;
    
    private ProductResponse product;
    
    private Integer quantity;

    private Integer oldOrderId;
        
    public SplitPurchaseOrderQueueResponse(SplitPurchaseOrderQueue original) {
        this.id = original.getId();
        this.product = new ProductResponse(original.getProduct(), false, true);
        this.quantity = original.getQuantity();
        this.oldOrderId = original.getOldOrderId();
    }
    
    public static List<SplitPurchaseOrderQueueResponse> createListFrom(List<SplitPurchaseOrderQueue> originals){
        List<SplitPurchaseOrderQueueResponse> list = new ArrayList<>();
        
        for(SplitPurchaseOrderQueue or : originals) {
            list.add(new SplitPurchaseOrderQueueResponse(or));
        }
        
        return list;
    }
    
    public SplitPurchaseOrderQueue toOriginal() {
        return new SplitPurchaseOrderQueue(id, product.toOriginal(), quantity, oldOrderId);
    }
}
