package com.beskyd.ms_control.business.stockrequests;

import com.beskyd.ms_control.business.distributions.DistributionResponse;
import com.beskyd.ms_control.business.general.SchemeResponseItem;
import com.beskyd.ms_control.business.general.StatesResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Getter
@Setter
public class StockRequestResponse {

    private Integer id;

    @ToString.Exclude
    private List<RequestProductsTypesListResponse> requestedProductTypes;
    
    private SchemeResponseItem scheme;
    
    private StatesResponse state;
    
    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp creationDate;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp stateChangeDate;

    @JsonIgnoreProperties(value = {"stockRequest"}, allowSetters = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private DistributionResponse distribution;

    @EqualsAndHashCode.Exclude
    private String distributionNotes;

    @EqualsAndHashCode.Exclude
    private Boolean manual;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private String notes;
    

    public StockRequestResponse(Integer id, List<RequestProductsTypesListResponse> requestedProductTypes, SchemeResponseItem scheme, StatesResponse state, Timestamp creationDate,
        Timestamp stateChangeDate, Boolean manual, String notes) {
        this.id = id;
        this.requestedProductTypes = requestedProductTypes;
        this.scheme = scheme;
        this.state = state;
        this.creationDate = creationDate;
        this.stateChangeDate = stateChangeDate;
        this.manual = manual;
        this.notes = notes;
    }
    
    public StockRequestResponse(Integer id, List<RequestProductsTypesListResponse> requestedProductTypes, SchemeResponseItem scheme, StatesResponse state, Timestamp creationDate,
        Timestamp stateChangeDate, DistributionResponse distribution, Boolean manual, String notes) {
        this.id = id;
        this.requestedProductTypes = requestedProductTypes;
        this.scheme = scheme;
        this.state = state;
        this.creationDate = creationDate;
        this.stateChangeDate = stateChangeDate;
        this.distribution = distribution;
        this.manual = manual;
        this.notes = notes;
    }

    public StockRequestResponse(StockRequest original) {
        this.id = original.getId();
        this.requestedProductTypes = RequestProductsTypesListResponse.createListFrom(original.getRequestedProductTypes());
        this.scheme = new SchemeResponseItem(original.getScheme());
        this.state = new StatesResponse(original.getState());
        this.creationDate = original.getCreationDate();
        this.stateChangeDate = original.getStateChangeDate();
        this.manual = original.getManual();
        this.notes = original.getNotes();
    }

    public static List<StockRequestResponse> createListFrom(List<StockRequest> originals){
        List<StockRequestResponse> list = new ArrayList<>();
        
        
        for(StockRequest or : originals) {
            list.add(new StockRequestResponse(or));
        }
        
        return list;
    }
    
    public StockRequest toOriginal() {
        List<RequestProductsTypesList> rtList = RequestProductsTypesListResponse.toOriginals(getRequestedProductTypes());
        
        StockRequest sr = new StockRequest(getId(), rtList, getScheme().toOriginal(), getState() != null ? getState().toOriginal() : null, getManual(), getNotes());
        
        for(RequestProductsTypesList rt : rtList) {
            rt.setStockRequest(sr);
        }
        
        return sr;
    }
    
    public static List<StockRequest> toOriginals(List<StockRequestResponse> responses){
        List<StockRequest> originals = new ArrayList<>();
        for(StockRequestResponse r : responses) {
            originals.add(r.toOriginal());
        }
        
        return originals;
    }
}
