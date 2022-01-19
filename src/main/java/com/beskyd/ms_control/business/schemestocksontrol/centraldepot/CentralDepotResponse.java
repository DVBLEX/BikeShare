package com.beskyd.ms_control.business.schemestocksontrol.centraldepot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsResponse;

public class CentralDepotResponse {
    
    private TypeOfAssetsResponse productType;
    
    private Integer amount;

    public CentralDepotResponse() {
    }


    public CentralDepotResponse(TypeOfAssetsResponse productType, Integer amount) {
        this.productType = productType;
        this.amount = amount;
    }

    public CentralDepotResponse(CentralDepot original) {
        this.productType = new TypeOfAssetsResponse(original.getProductType(), false);
        this.amount = original.getAmount();
    }
    
    public static List<CentralDepotResponse> createListFrom(List<CentralDepot> originals){
        List<CentralDepotResponse> list = new ArrayList<>();
        
        for(CentralDepot or : originals) {
            list.add(new CentralDepotResponse(or));
        }
        
        return list;
    }
    
    public CentralDepot toOriginal() {
        return new CentralDepot(productType.toOriginal(), amount);
    }
    
    public TypeOfAssetsResponse getProductType() {
        return productType;
    }

    
    public void setProductType(TypeOfAssetsResponse productType) {
        this.productType = productType;
    }

    
    public Integer getAmount() {
        return amount;
    }

    
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    
    public Integer returnProductTypeId() {
        return productType.getId();
    }


    @Override
    public int hashCode() {
        return Objects.hash(productType);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CentralDepotResponse other = (CentralDepotResponse) obj;
        return Objects.equals(productType, other.productType);
    }


    @Override
    public String toString() {
        return "CentralDepotResponse [productType=" + productType + ", amount=" + amount + "]";
    }
    
    
}
