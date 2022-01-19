package com.beskyd.ms_control.business.balance;

import javax.persistence.*;

import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.States;
import com.beskyd.ms_control.business.repairreports.entity.BikeStations;
import com.beskyd.ms_control.business.repairreports.entity.Bikes;
import com.beskyd.ms_control.business.repairreports.entity.ReportReasons;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "bikes_location_on-street")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BikesLocationOnStreet implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "scheme_name")
    private Scheme scheme;

    @ManyToOne
    @JoinColumn(name = "bike_id")
    private Bikes bike;

    private Boolean manually_selected;

    private String  reason;

    private Timestamp last_in_storage;

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("scheme", scheme.getName());  // choose all bikes from city
       // jo.put("location", location.getScheme() + " " + location.getLocation()); // chose all bikes from current city location
        jo.put("bike", (bike != null ? bike.getScheme() + " " + bike.getNumber() : ""));
        jo.put("manually_selected", manually_selected);
        jo.put("reason", reason);
        jo.put("last_in_storage", last_in_storage);
        return jo;
    }

}
