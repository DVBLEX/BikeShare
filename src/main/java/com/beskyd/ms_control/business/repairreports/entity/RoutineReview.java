package com.beskyd.ms_control.business.repairreports.entity;

import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RoutineReview implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Timestamp creationDate;
    
    private String operator;
    
    private int bollardsTotal;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private BikeStations station;

    private int bikesAtStation;

    private boolean graffiti;

    private boolean weeds;
    
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "routine_review_bikes_checked",
//            joinColumns = @JoinColumn(name = "review_id"),
//            inverseJoinColumns = @JoinColumn(name = "bike_id")
//        )
//    private Set<Bikes> bikes;

    private String reports;

    private String bollardsInactive;

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("creationDate", creationDate);
        jo.put("operator", operator);
        jo.put("station", station.toJSONObject());
        jo.put("bollardsTotal", bollardsTotal);
        jo.put("bikesAtStation", bikesAtStation);
        jo.put("graffiti", graffiti);
        jo.put("weeds", weeds);
        jo.put("bollardsInactive", bollardsInactive);
        jo.put("reports", reports);
        return jo;
    }
}
