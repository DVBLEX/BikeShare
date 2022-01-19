package com.beskyd.ms_control.business.distributions;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsResponse;

public class DistributionAssetsResponse {
    
    private DistributionResponse distribution;
    
    private TypeOfAssetsResponse typeOfAssets;
    
    private int quantity;

    public DistributionAssetsResponse() {
        
    }
    
    public DistributionAssetsResponse(DistributionResponse distribution, TypeOfAssetsResponse typeOfAssets, int quantity) {
        this.distribution = distribution;
        this.typeOfAssets = typeOfAssets;
        this.quantity = quantity;
    }
    
    public DistributionAssetsResponse(DistributionAssets original) {
        this.distribution = new DistributionResponse(original.getDistribution(), true);
        this.typeOfAssets = new TypeOfAssetsResponse(original.getTypeOfAssets(), true);
        this.quantity = original.getQuantity();
    }
    
    public static Set<DistributionAssetsResponse> createListFrom(Set<DistributionAssets> originals){
        Set<DistributionAssetsResponse> list = new HashSet<>();
        
        for(DistributionAssets or : originals) {
            list.add(new DistributionAssetsResponse(or));
        }
        
        return list;
    }
    
    public DistributionAssets toOriginal() {
        return new DistributionAssets(getDistribution().toOriginal(), getTypeOfAssets().toOriginal(), getQuantity());
    }
    
    public static Set<DistributionAssets> toOriginals(Set<DistributionAssetsResponse> responses){
        Set<DistributionAssets> list = new HashSet<>();
        
        for(DistributionAssetsResponse resp : responses) {
            list.add(resp.toOriginal());
        }
        
        return list;
    }


    public DistributionResponse getDistribution() {
        return distribution;
    }

    
    public void setDistribution(DistributionResponse distribution) {
        this.distribution = distribution;
    }

    
    public TypeOfAssetsResponse getTypeOfAssets() {
        return typeOfAssets;
    }

    
    public void setTypeOfAssets(TypeOfAssetsResponse typeOfAssets) {
        this.typeOfAssets = typeOfAssets;
    }

    
    public int getQuantity() {
        return quantity;
    }

    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    @Override
    public int hashCode() {
        return Objects.hash(distribution, quantity, typeOfAssets);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DistributionAssetsResponse other = (DistributionAssetsResponse) obj;
        return Objects.equals(distribution, other.distribution) && quantity == other.quantity && Objects.equals(typeOfAssets, other.typeOfAssets);
    }


    @Override
    public String toString() {
        return "DistributionAssetsResponse [distribution=" + distribution + ", typeOfAssets=" + typeOfAssets + ", quantity=" + quantity + "]";
    }
    
    
}
