package com.beskyd.ms_control.business.assetsprofiles;

import com.beskyd.ms_control.business.assetsprofiles.Product.ProductIndex;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ProductResponse {

    private Integer id;

    @EqualsAndHashCode.Include
    private ProductIndex productId;

    @EqualsAndHashCode.Include
    private TypeOfAssetsResponse type;

    @EqualsAndHashCode.Exclude
    private Integer minOrder;

    @EqualsAndHashCode.Exclude
    private String fullName;

    @EqualsAndHashCode.Exclude
    private Integer deliveryTime;


    public ProductResponse(Integer id, ProductIndex productId, TypeOfAssetsResponse type, Integer minOrder, Integer deliveryTime) {
        this.id = id;
        this.productId = productId;
        this.type = type;
        this.minOrder = minOrder;
        this.deliveryTime = deliveryTime;
        this.fullName = this.type.getGroupName() + " " + this.productId.getProductName();
    }

    public ProductResponse(Product original) {
        this.id = original.getId();
        this.productId = original.getProductId();
        this.type = new TypeOfAssetsResponse(original.getType(), true);
        this.minOrder = original.getMinOrder();
        this.deliveryTime = original.getDeliveryTime();
        this.fullName = this.type.getGroupName() + " " + this.productId.getProductName();
    }
    
    public ProductResponse(Product original, boolean nullType) {
        this.id = original.getId();
        this.productId = original.getProductId();
        this.deliveryTime = original.getDeliveryTime();
        if(!nullType) {
            this.type = new TypeOfAssetsResponse(original.getType(), true);
            this.fullName = this.type.getGroupName() + " " + this.productId.getProductName();
        } else {
            this.fullName = this.productId.getProductName();
        }
        this.minOrder = original.getMinOrder();

    }
    
    public ProductResponse(Product original, boolean nullType, boolean doNotNullProductsInType) {
        this.id = original.getId();
        this.productId = original.getProductId();
        this.deliveryTime = original.getDeliveryTime();
        if(!nullType) {
            this.type = new TypeOfAssetsResponse(original.getType(), !doNotNullProductsInType);
            this.fullName = this.type.getTypeName() + " " + this.productId.getProductName();
        } else {
            this.fullName = this.productId.getProductName();
        }
        this.minOrder = original.getMinOrder();
    }

    public static List<ProductResponse> createListFrom(List<Product> originals){
        return createListFrom(originals, false);
    }
    
    public static List<ProductResponse> createListFrom(List<Product> originals, boolean nullTypes){
        List<ProductResponse> list = new ArrayList<>();
        
        
        for(Product or : originals) {
            list.add(new ProductResponse(or, nullTypes));
        }
        
        return list;
    }
    
    
    public static Set<ProductResponse> createSetFrom(Set<Product> originals){
        return createSetFrom(originals, false);
    }
    
    public static Set<ProductResponse> createSetFrom(Set<Product> originals, boolean nullTypes){
        Set<ProductResponse> set = new HashSet<>();
        
        
        for(Product or : originals) {
            set.add(new ProductResponse(or, nullTypes));
        }
        
        return set;
    }

    public Product toOriginal() {
        return new Product(id, productId.getProductName(), productId.getSupplier(), type.toOriginal(), minOrder, deliveryTime);
    }
}
