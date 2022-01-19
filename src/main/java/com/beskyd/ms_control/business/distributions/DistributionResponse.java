package com.beskyd.ms_control.business.distributions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.beskyd.ms_control.business.general.SchemeResponseItem;
import com.beskyd.ms_control.business.general.StatesResponse;
import com.beskyd.ms_control.business.stockrequests.StockRequestResponse;
import com.fasterxml.jackson.annotation.JsonFormat;


public class DistributionResponse {

    private Integer id;

    private StockRequestResponse stockRequest;

    private SchemeResponseItem schemeFrom;

    private SchemeResponseItem schemeTo;

    private StatesResponse state;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp creationDate;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp stateChangeDate;

    private String notes;
    
    private Set<DistributionAssetsResponse> assets;

    public DistributionResponse() {    
    }
    
    public DistributionResponse(Integer id, StockRequestResponse stockRequest, SchemeResponseItem schemeFrom, SchemeResponseItem schemeTo, StatesResponse state,
        Timestamp creationDate, Timestamp stateChangeDate, String notes, Set<DistributionAssetsResponse> assets) {
        this.id = id;
        this.stockRequest = stockRequest;
        this.schemeFrom = schemeFrom;
        this.schemeTo = schemeTo;
        this.state = state;
        this.creationDate = creationDate;
        this.stateChangeDate = stateChangeDate;
        this.notes = notes;
        this.assets = assets;
    }

    public DistributionResponse(Distribution original, boolean ignotreAssets) {
        this.id = original.getId();
        if(original.getStockRequest() != null) {
            this.stockRequest = new StockRequestResponse(original.getStockRequest());
        }
        if(original.getSchemeFrom() != null) {
            this.schemeFrom = new SchemeResponseItem(original.getSchemeFrom());
        }
        this.schemeTo = new SchemeResponseItem(original.getSchemeTo());
        this.state = new StatesResponse(original.getState());
        this.creationDate = original.getCreationDate();
        this.stateChangeDate = original.getStateChangeDate();
        this.notes = original.getNotes();
        if(!ignotreAssets) {
            this.assets = DistributionAssetsResponse.createListFrom(original.getAssets());
        }
    }
    
    public static List<DistributionResponse> createListFrom(List<Distribution> originals){
        List<DistributionResponse> list = new ArrayList<>();
        
        for(Distribution or : originals) {
            list.add(new DistributionResponse(or, false));
        }
        
        return list;
    }
    
    public Distribution toOriginal() {
        return new Distribution(id, stockRequest.toOriginal(), schemeFrom.toOriginal(), schemeTo.toOriginal(), state.toOriginal(), creationDate, stateChangeDate, notes, DistributionAssetsResponse.toOriginals(assets));
    }
    
    public Integer getId() {
        return id;
    }

    
    public void setId(Integer id) {
        this.id = id;
    }

    
    public StockRequestResponse getStockRequest() {
        return stockRequest;
    }

    
    public void setStockRequest(StockRequestResponse stockRequest) {
        this.stockRequest = stockRequest;
    }

    
    public SchemeResponseItem getSchemeFrom() {
        return schemeFrom;
    }

    
    public void setSchemeFrom(SchemeResponseItem schemeFrom) {
        this.schemeFrom = schemeFrom;
    }

    
    public SchemeResponseItem getSchemeTo() {
        return schemeTo;
    }

    
    public void setSchemeTo(SchemeResponseItem schemeTo) {
        this.schemeTo = schemeTo;
    }

    
    public StatesResponse getState() {
        return state;
    }

    
    public void setState(StatesResponse state) {
        this.state = state;
    }

    
    public Timestamp getCreationDate() {
        return creationDate;
    }

    
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    
    public Timestamp getStateChangeDate() {
        return stateChangeDate;
    }

    
    public void setStateChangeDate(Timestamp stateChangeDate) {
        this.stateChangeDate = stateChangeDate;
    }

    
    public String getNotes() {
        return notes;
    }

    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    
    public Set<DistributionAssetsResponse> getAssets() {
        return assets;
    }

    
    public void setAssets(Set<DistributionAssetsResponse> assets) {
        this.assets = assets;
    }


    @Override
    public int hashCode() {
        return Objects.hash(creationDate, id, schemeFrom, schemeTo, state, stateChangeDate, stockRequest);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DistributionResponse other = (DistributionResponse) obj;
        return Objects.equals(creationDate, other.creationDate) && Objects.equals(id, other.id) && Objects.equals(schemeFrom, other.schemeFrom) && Objects.equals(schemeTo,
            other.schemeTo) && Objects.equals(state, other.state) && Objects.equals(stateChangeDate, other.stateChangeDate) && Objects.equals(stockRequest, other.stockRequest);
    }


    @Override
    public String toString() {
        return "DistributionResponse [id=" + id + ", stockRequest=" + stockRequest + ", schemeFrom=" + schemeFrom + ", schemeTo=" + schemeTo + ", state=" + state
            + ", creationDate=" + creationDate + ", stateChangeDate=" + stateChangeDate + ", notes=" + notes + "]";
    }
    
    
}
