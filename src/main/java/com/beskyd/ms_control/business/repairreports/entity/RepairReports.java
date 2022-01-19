package com.beskyd.ms_control.business.repairreports.entity;

import com.beskyd.ms_control.business.general.States;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(exclude = "operators") //because of cross link with usedSpareparts obj
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairReports implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "state_id")
    private States state;
    
    @ManyToOne
    @JoinColumn(name = "location_id")
    private BikeStations location;
    
    @ManyToOne
    @JoinColumn(name = "bike_id")
    private Bikes bike;
    
    @ManyToOne
    @JoinColumn(name = "report_reason_id")
    private ReportReasons reportReason;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "report_repair_reasons",
            joinColumns = @JoinColumn(name = "repair_report_id"),
            inverseJoinColumns = @JoinColumn(name = "repair_reason_id")
        )
    private Set<RepairReasons> repairReason;
    
    private Timestamp reportDate;

    private Timestamp collectedDate;
    
    private Timestamp repairDate;
    
    private String onStreetOperator;
    
    private String onDepotOperator;
    
    private String streetComments;
    
    private String depotComments;

    private String bollardNumbers;

    private String bollardComments;

    private Boolean routineReview;
    
    private Boolean onStreetRepair;
    
    private String geoLocation;

    private Boolean stationItself;
    
    @OneToMany(mappedBy = "report", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<RepairReportsOperators> operators;
    
    @Override
    public int hashCode() {
        return Objects.hash(bike, id, location, repairReason, reportReason, state);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RepairReports other = (RepairReports) obj;
        return Objects.equals(bike, other.bike) && Objects.equals(id, other.id) && Objects.equals(location, other.location) && Objects.equals(repairReason, other.repairReason)
            && Objects.equals(reportReason, other.reportReason) && Objects.equals(state, other.state);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("state", (state != null ? state.getName() : null));
        jo.put("location", location.getScheme() + " " + location.getLocation());
        jo.put("bike", (bike != null ? bike.getScheme() + " " + bike.getNumber() : ""));
        jo.put("reportReason", reportReason.getReason());
        jo.put("repairReason", repairReason);
        jo.put("reportDate", reportDate);
        jo.put("repairDate", repairDate);
        jo.put("onStreetOperator", onStreetOperator);
        jo.put("onDepotOperator", onDepotOperator);
        jo.put("streetComments", streetComments);
        jo.put("bollardNumbers", bollardNumbers);
        jo.put("bollardComments", bollardComments);
        jo.put("depotComments", depotComments);
        jo.put("routineReview", routineReview);
        jo.put("onStreetRepair", onStreetRepair);
        jo.put("geoLocation", geoLocation);
        jo.put("stationItself", stationItself);
        jo.put("operators", operators);
        return jo;
    }
}
