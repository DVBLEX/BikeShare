package com.beskyd.ms_control.business.distributions;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONObject;

import com.beskyd.ms_control.config.addLogic.JsonAware;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.States;
import com.beskyd.ms_control.business.stockrequests.StockRequest;

@Entity
public class Distribution implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "stock_request_id")
    private StockRequest stockRequest;

    @ManyToOne
    @JoinColumn(name = "scheme_from_name")
    private Scheme schemeFrom;

    @ManyToOne
    @JoinColumn(name = "scheme_to_name")
    private Scheme schemeTo;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private States state;

    private Timestamp creationDate;

    private Timestamp stateChangeDate;

    private String notes;
    
    //@LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "distribution", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private Set<DistributionAssets> assets;

    public Distribution() {
    }

    public Distribution(Integer id, StockRequest stockRequest, Scheme schemeFrom, Scheme schemeTo, States state, Timestamp creationDate, Timestamp stateChangeDate, String notes) {
        this.id = id;
        this.stockRequest = stockRequest;
        this.schemeFrom = schemeFrom;
        this.schemeTo = schemeTo;
        this.state = state;
        this.creationDate = creationDate;
        this.stateChangeDate = stateChangeDate;
        this.notes = notes;
    }
    
    public Distribution(Integer id, StockRequest stockRequest, Scheme schemeFrom, Scheme schemeTo, States state, Timestamp creationDate, Timestamp stateChangeDate, String notes, Set<DistributionAssets> assets) {
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

    
    public Integer getId() {
        return id;
    }

    
    public void setId(Integer id) {
        this.id = id;
    }

    
    public StockRequest getStockRequest() {
        return stockRequest;
    }

    
    public void setStockRequest(StockRequest stockRequest) {
        this.stockRequest = stockRequest;
    }

    
    public Scheme getSchemeFrom() {
        return schemeFrom;
    }

    
    public void setSchemeFrom(Scheme schemeFrom) {
        this.schemeFrom = schemeFrom;
    }

    
    public Scheme getSchemeTo() {
        return schemeTo;
    }

    
    public void setSchemeTo(Scheme schemeTo) {
        this.schemeTo = schemeTo;
    }

    
    public States getState() {
        return state;
    }

    
    public void setState(States state) {
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
    
    
    public Set<DistributionAssets> getAssets() {
        return assets;
    }

    
    public void setAssets(Set<DistributionAssets> assets) {
        this.assets = assets;
    }

    @Override
    public int hashCode() {
        return Objects.hash(creationDate, id, schemeFrom, schemeTo, state, stockRequest);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Distribution other = (Distribution) obj;
        return Objects.equals(creationDate, other.creationDate) && Objects.equals(id, other.id) && Objects.equals(schemeFrom, other.schemeFrom) && Objects.equals(schemeTo,
            other.schemeTo) && Objects.equals(state, other.state) && Objects.equals(stockRequest, other.stockRequest);
    }

    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("stockRequestId", stockRequest.getId());
        jo.put("schemeFrom", schemeFrom.getName());
        jo.put("schemeTo", schemeTo.getName());
        jo.put("state", state.getName());
        jo.put("creationDate", creationDate);
        jo.put("stateChangeDate", stateChangeDate);
        jo.put("notes", notes);
        jo.put("assets", assets);
        return jo;
    }
    
    
}
