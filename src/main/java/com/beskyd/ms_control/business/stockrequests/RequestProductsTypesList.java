package com.beskyd.ms_control.business.stockrequests;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

@IdClass(RequestProductsTypesList.ComplexId.class)
@Entity
@NoArgsConstructor
@EqualsAndHashCode
public class RequestProductsTypesList implements JsonAware{
    
    @Id
    @Column(name = "id_request")
    @Getter @Setter
    private Integer idRequest;
    
    @Id
    @Column(name = "id_prod_type")
    @Getter @Setter
    private Integer idProdType;
    
    @MapsId
    @ManyToOne
    @JoinColumn(name = "id_request")
    @Getter
    @EqualsAndHashCode.Exclude
    private StockRequest stockRequest;
    
    @MapsId
    @ManyToOne
    @JoinColumn(name = "id_prod_type")
    @Getter
    @EqualsAndHashCode.Exclude
    private TypeOfAssets productType;

    @Getter @Setter
    @EqualsAndHashCode.Exclude
    private int orderValue;

    public RequestProductsTypesList(StockRequest stockRequest, TypeOfAssets productType, int orderValue) {
        if(stockRequest != null) {
            this.idRequest = stockRequest.getId();
        }
        if(productType != null) {
            this.idProdType = productType.getId();
        }
        this.stockRequest = stockRequest;
        this.productType = productType;
        this.orderValue = orderValue;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class ComplexId implements Serializable{

        @Column(name = "id_request")
        private Integer idRequest;
        
        @Column(name = "id_prod_type")
        private Integer idProdType;
    }
    
    /**
     * Also sets {@code idRequest}
     * @param stockRequest
     */
    public void setStockRequest(StockRequest stockRequest) {
        this.stockRequest = stockRequest;
        if(stockRequest != null) {
            this.idRequest = stockRequest.getId();
        }
    }

    /**
     * Also sets {@code idProdType}
     * @param productType
     */
    public void setProductType(TypeOfAssets productType) {
        this.productType = productType;
        if(productType != null) {
            this.idProdType = productType.getId();
        }
    }

    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("idRequest", idRequest);
        jo.put("typeOfAssetsName", productType.getTypeName());
        jo.put("orderValue", orderValue);
        return jo;
    }


    @Override
    public String toString() {
        return "ComplexId [idRequest=" + idRequest + ", idProdType=" + idProdType + "]";
    }

}
