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
public class PurchaseOrderProductsResponse{

    private PurchaseOrderResponse purchaseOrder;
    
    private ProductResponse product;

    @EqualsAndHashCode.Exclude
    private Integer amount;

    @EqualsAndHashCode.Exclude
    private Integer confirmed;
    

    public PurchaseOrderProductsResponse(PurchaseOrderProducts original) throws PurchaseOrderException {
        this.purchaseOrder = new PurchaseOrderResponse(original.getPurchaseOrder(), true);
        this.product = new ProductResponse(original.getProduct(), false, true);
        this.amount = original.getAmount();
        this.confirmed = original.getConfirmed();
    }

    public static List<PurchaseOrderProductsResponse> createListFrom(List<PurchaseOrderProducts> originals) throws PurchaseOrderException{
        List<PurchaseOrderProductsResponse> list = new ArrayList<>();
        
        for(PurchaseOrderProducts or : originals) {
            list.add(new PurchaseOrderProductsResponse(or));
        }
        
        return list;
    }
    
    public PurchaseOrderProducts toOriginal() {
        return new PurchaseOrderProducts(purchaseOrder != null ? purchaseOrder.toOriginal() : null, product.toOriginal(), amount, confirmed);
    }
    
    public static List<PurchaseOrderProducts> toOriginals(List<PurchaseOrderProductsResponse> responses){
        List<PurchaseOrderProducts> list = new ArrayList<>();
        
        for(PurchaseOrderProductsResponse r : responses) {
            list.add(r.toOriginal());
        }
        
        return list;
    }
}
