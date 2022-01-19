package com.beskyd.ms_control.business.distributions;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.json.JSONObject;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.config.addLogic.JsonAware;

@Entity
@IdClass(DistributionAssets.ComplexId.class)
public class DistributionAssets implements JsonAware{
    
    @Id
    @Column(name = "dist_id")
    private Integer distId;
    
    @Id
    @Column(name = "type_of_asset_id")
    private Integer typeOfAssetsId;
    
    @MapsId
    @ManyToOne
    @JoinColumn(name = "dist_id")
    private Distribution distribution;
    
    @MapsId
    @OneToOne
    @JoinColumn(name = "type_of_asset_id")
    private TypeOfAssets typeOfAssets;
    
    private int quantity;

        
    public DistributionAssets() {
    }


    public DistributionAssets(Distribution distribution, TypeOfAssets typeOfAssets, int quantity) {
        if(distribution != null) {
            this.distId = distribution.getId();
        }
        this.distribution = distribution;
        if(typeOfAssets != null) {
            this.typeOfAssetsId = typeOfAssets.getId();
        }
        this.typeOfAssets = typeOfAssets;
        this.quantity = quantity;
    }


    public static class ComplexId implements Serializable{
        @Column(name = "dist_id")
        private Integer distId;
        
        @Column(name = "type_of_asset_id")
        private Integer typeOfAssetsId;
        
        public ComplexId() {
        }

        public ComplexId(Integer distId, Integer typeOfAssetsId) {
            this.distId = distId;
            this.typeOfAssetsId = typeOfAssetsId;
        }

        
        public Integer getDistId() {
            return distId;
        }

        
        public void setDistId(Integer distId) {
            this.distId = distId;
        }

        
        public Integer getTypeOfAssetsId() {
            return typeOfAssetsId;
        }

        
        public void setTypeOfAssetsId(Integer typeOfAssetsId) {
            this.typeOfAssetsId = typeOfAssetsId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(distId, typeOfAssetsId);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ComplexId other = (ComplexId) obj;
            return Objects.equals(distId, other.distId) && Objects.equals(typeOfAssetsId, other.typeOfAssetsId);
        }

        @Override
        public String toString() {
            return "ComplexId [distId=" + distId + ", typeOfAssetsId=" + typeOfAssetsId + "]";
        }
        
        
    }
    
    public Integer getDistId() {
        return distId;
    }

    
    public void setDistId(Integer distId) {
        this.distId = distId;
    }

    
    public Distribution getDistribution() {
        return distribution;
    }

    /**
     * Also sets {@code distId}
     * @param distribution
     */
    public void setDistribution(Distribution distribution) {
        this.distribution = distribution;
        if(distribution != null){
            this.distId = distribution.getId();
        }
    }

    
    public Integer getTypeOfAssetsId() {
        return typeOfAssetsId;
    }

    
    public void setTypeOfAssetsId(Integer typeOfAssetsId) {
        this.typeOfAssetsId = typeOfAssetsId;
    }


    public TypeOfAssets getTypeOfAssets() {
        return typeOfAssets;
    }

    
    /**
     * Also sets {@code typeOfAssetsId}
     * @param typeOfAssets
     */
    public void setTypeOfAssets(TypeOfAssets typeOfAssets) {
        this.typeOfAssets = typeOfAssets;
        if(typeOfAssets != null) {
            this.typeOfAssetsId = typeOfAssets.getId();
        }
    }

    
    public int getQuantity() {
        return quantity;
    }

    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    @Override
    public int hashCode() {
        return Objects.hash(distId, distribution, typeOfAssets);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DistributionAssets other = (DistributionAssets) obj;
        return Objects.equals(distId, other.distId) && Objects.equals(distribution, other.distribution) && Objects.equals(typeOfAssets, other.typeOfAssets);
    }


    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }


    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("distId", distId);
        jo.put("typeOfAssetsId", typeOfAssets.getId());
        jo.put("typeOfAssetsName", typeOfAssets.getTypeName());
        jo.put("quantity", quantity);
        return jo;
    }
    
    
}
