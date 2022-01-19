package com.beskyd.ms_control.business.repairreports.response;

import com.beskyd.ms_control.business.general.StatesResponse;
import com.beskyd.ms_control.business.repairreports.entity.RepairReports;
import com.beskyd.ms_control.business.repairreports.entity.RepairReportsOperators;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RepairReportsResponse {

    private Integer id;

    private StatesResponse state;
    
    private BikeStationsResponse location;
    
    private BikesResponse bike;
    
    private ReportReasonsResponse reportReason;
    
    private Set<RepairReasonsResponse> repairReason;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp reportDate;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp collectedDate;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
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
    
    private Set<RepairReportsOperatorsResponse> operators;
    
    private RepairReportsOperatorsResponse theLatestOperator;
    
    private List<ReportUsedAssetsResponse> sparepartsToRemove;

    private List<RepairReportsOperators> repairReportsOperators;

    private boolean completeAfter;
    
    public RepairReportsResponse(RepairReports original) {
        this.id = original.getId();
        this.state = new StatesResponse(original.getState());
        this.location = new BikeStationsResponse(original.getLocation());
        if(original.getBike() != null) {
            this.bike = new BikesResponse(original.getBike());
        }
        this.reportReason = new ReportReasonsResponse(original.getReportReason());
        this.repairReason = RepairReasonsResponse.createSetFrom(original.getRepairReason());
        this.reportDate = original.getReportDate();
        this.repairDate = original.getRepairDate();
        this.collectedDate = original.getCollectedDate();
        this.onStreetOperator = original.getOnStreetOperator();
        this.onDepotOperator = original.getOnDepotOperator();
        this.streetComments = original.getStreetComments();
        this.depotComments = original.getDepotComments();
        this.bollardNumbers = original.getBollardNumbers();
        this.bollardComments = original.getBollardComments();
        this.routineReview = original.getRoutineReview();
        this.onStreetRepair = original.getOnStreetRepair();
        this.geoLocation = original.getGeoLocation();
        this.stationItself = original.getStationItself();
        if(original.getOperators() != null) {
            this.operators = RepairReportsOperatorsResponse.createSetFrom(original.getOperators());
        }
    }

    public static List<RepairReportsResponse> createListFrom(List<RepairReports> originals){
        List<RepairReportsResponse> list = new ArrayList<>();
        
        for(RepairReports or : originals) {
            list.add(new RepairReportsResponse(or));
        }
        
        return list;
    }
    
    public RepairReports toOriginal() {
        var rr = RepairReports.builder()
                .state(state != null ? state.toOriginal() : null)
                .location(location.toOriginal())
                .bike(bike != null ? bike.toOriginal() : null)
                .reportReason(reportReason.toOriginal())
                .repairReason(repairReason != null ? RepairReasonsResponse.toOriginals(repairReason) : null)
                .reportDate(reportDate)
                .repairDate(repairDate)
                .collectedDate(collectedDate)
                .onStreetOperator(onStreetOperator)
                .onDepotOperator(onDepotOperator)
                .streetComments(streetComments)
                .bollardNumbers(bollardNumbers)
                .bollardComments(bollardComments)
                .depotComments(depotComments)
                .routineReview(routineReview)
                .onStreetRepair(onStreetRepair)
                .geoLocation(geoLocation)
                .stationItself(stationItself)
                .id(id)
                .build();

        if(operators != null) {
            rr.setOperators(RepairReportsOperatorsResponse.toOriginals(operators, rr));
        }
        return rr;
    }

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
        RepairReportsResponse other = (RepairReportsResponse) obj;
        return Objects.equals(bike, other.bike) && Objects.equals(id, other.id) && Objects.equals(location, other.location) && Objects.equals(repairReason, other.repairReason)
            && Objects.equals(reportReason, other.reportReason) && Objects.equals(state, other.state);
    }

    @Override
    public String toString() {
        return "RepairReportsResponse [" +
                "id=" + id +
                ", state=" + state +
                ", location=" + location +
                ", bike=" + bike +
                ", reportReason=" + reportReason +
                ", repairReason=" + repairReason +
                ", reportDate=" + reportDate +
                ", repairDate=" + repairDate +
                ", onStreetOperator=" + onStreetOperator +
                ", onDepotOperator=" + onDepotOperator +
                ", streetComments=" + streetComments +
                ", depotComments=" + depotComments +
                ", routineReview=" + routineReview +
                ", onStreetRepair=" + onStreetRepair +
                ", geoLocation=" + geoLocation +
                ", stationItself=" + stationItself + "]";
    }
}
