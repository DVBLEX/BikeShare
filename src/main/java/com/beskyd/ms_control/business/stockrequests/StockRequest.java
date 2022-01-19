package com.beskyd.ms_control.business.stockrequests;

import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.States;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stock_requests")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockRequest implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToMany(mappedBy = "stockRequest", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private List<RequestProductsTypesList> requestedProductTypes;
    
    @ManyToOne
    @JoinColumn(name = "`scheme`")
    private Scheme scheme;
    
    @ManyToOne
    @JoinColumn(name = "state_id")
    private States state;

    @EqualsAndHashCode.Exclude
    private Timestamp creationDate;

    @EqualsAndHashCode.Exclude
    private Timestamp stateChangeDate;

    @EqualsAndHashCode.Exclude
    private Boolean manual;

    @EqualsAndHashCode.Exclude
    private String notes;

    public StockRequest(Integer id, List<RequestProductsTypesList> requestedProductTypes, Scheme scheme, States state, Boolean manual, String notes) {
        this.id = id;
        this.requestedProductTypes = requestedProductTypes;
        this.scheme = scheme;
        this.state = state;
        this.creationDate = Timestamp.valueOf(LocalDateTime.now());
        this.stateChangeDate = this.creationDate;
        this.manual = manual;
        this.notes = notes;
    }

    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }


    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        
        JSONArray pt = new JSONArray();
        for(RequestProductsTypesList type : requestedProductTypes) {
            pt.put(type.toJSONObject());
        }
        jo.put("requestedProductTypes", pt);
        jo.put("scheme", scheme.getName());
        jo.put("state", state.getName());
        jo.put("creationDate", creationDate);
        jo.put("stateChangeDate", stateChangeDate);
        jo.put("manual", manual);
        jo.put("notes", notes);
        return jo;
    }
    
    
}
