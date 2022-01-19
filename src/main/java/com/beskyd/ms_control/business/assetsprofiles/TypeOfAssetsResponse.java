package com.beskyd.ms_control.business.assetsprofiles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TypeOfAssetsResponse {
    private Integer id;
    
    @NotNull
    private String assetGroup;
    
    @NotNull
    private String typeName;

    @NotNull
    private String groupName;

    @EqualsAndHashCode.Exclude
    private String fullName;

    @JsonIgnoreProperties(value = {"type"}, allowSetters = true)
    @EqualsAndHashCode.Exclude
    private Set<ProductResponse> products;

    public TypeOfAssetsResponse(Integer id, @NotNull String assetGroup, @NotNull String typeName, @NotNull String groupName, Set<ProductResponse> products) {
        this.id = id;
        this.assetGroup = assetGroup;
        this.typeName = typeName;
        this.groupName = groupName;
        this.products = products;
        if(groupName == null) {
            groupName = "";
        }
        this.fullName = groupName + " " + typeName;
    }

    public TypeOfAssetsResponse(TypeOfAssets original, boolean nullProducts) {        
        this.id = original.getId();
        this.assetGroup = original.getAssetGroup();
        this.typeName = original.getTypeName();
        this.groupName = original.getGroupName();
        if(!nullProducts) {
            this.products = ProductResponse.createSetFrom(original.getProducts(), false);
        }
        if(original.getGroupName() == null) {
            original.setGroupName("");
        }
        this.fullName = original.getGroupName() + " " + original.getTypeName();
    }

    public static List<TypeOfAssetsResponse> createListFrom(List<TypeOfAssets> originals){
        List<TypeOfAssetsResponse> list = new ArrayList<>();
        
        for(TypeOfAssets or : originals) {
            list.add(new TypeOfAssetsResponse(or, false));
        }
        
        return list;
    }
    
    public TypeOfAssets toOriginal() {
        TypeOfAssets type =  new TypeOfAssets(id, assetGroup, typeName, groupName, null);
        Set<Product> productsSet = new HashSet<>();
        if(products != null) {
            for(ProductResponse pr : products) {
                productsSet.add(new Product(pr.getId(), pr.getProductId().getProductName(), pr.getProductId().getSupplier(), type, pr.getMinOrder(), pr.getDeliveryTime()));
            }
        }
        
        type.setProducts(productsSet);
        
        return type;
    }
}
