package com.beskyd.ms_control.business.schemestocksontrol.reports;

import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.States;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StockUsageReports implements JsonAware{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "scheme_name")
    private Scheme scheme;
    
    private Timestamp creationDate;
    
    @ManyToOne
    @JoinColumn(name = "state_id")
    private States state;

    @EqualsAndHashCode.Exclude
    private String notes;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "report", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private Set<StockUsageReportAssets> assets;

    public StockUsageReports(Integer id, Scheme scheme, Timestamp creationDate, States state, String notes) {
        this.id = id;
        this.scheme = scheme;
        this.creationDate = creationDate;
        this.state = state;
        this.notes = notes;
        this.assets = new HashSet<>();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("scheme", scheme.toJSONObject());
        jo.put("creationDate", creationDate);
        jo.put("state", state.toJSONObject());
        jo.put("notes", notes);
        
        JSONArray ja = new JSONArray();
       for(var asset : assets) {
           ja.put(asset.toJSONObject());
       }
       jo.put("assets", ja);
        return jo;
    }
}
