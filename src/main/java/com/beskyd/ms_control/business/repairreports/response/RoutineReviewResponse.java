package com.beskyd.ms_control.business.repairreports.response;

import com.beskyd.ms_control.business.repairreports.entity.RoutineReview;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RoutineReviewResponse {

    private Integer id;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp creationDate;
    
    private String operator;
    
    private BikeStationsResponse station;

    private int bollardsTotal;

    private int bikesAtStation;

    private boolean graffiti;

    private boolean weeds;

    private List<Bollard> inactiveBollards;
    
    private String reports;

    public RoutineReviewResponse(RoutineReview original) {
        this.id = original.getId();
        this.creationDate = original.getCreationDate();
        this.operator = original.getOperator();
        this.station = new BikeStationsResponse(original.getStation());
        this.bollardsTotal = original.getBollardsTotal();
        this.bikesAtStation = original.getBikesAtStation();
        this.graffiti = original.isGraffiti();
        this.weeds = original.isWeeds();
        this.inactiveBollards = new ArrayList<>();
        var al = new JSONArray(original.getBollardsInactive());
        for (int i = 0; i < al.length(); i++) {
            var jo = al.getJSONObject(i);
            this.inactiveBollards.add(new Bollard(jo.getInt("bollardNo"), jo.getString("reason")));
        }
        this.reports = original.getReports();
    }
    
    public static List<RoutineReviewResponse> createListFrom(List<RoutineReview> originals){
        List<RoutineReviewResponse> list = new ArrayList<>();
        
        for(RoutineReview or : originals) {
            list.add(new RoutineReviewResponse(or));
        }
        
        return list;
    }
    
    public RoutineReview toOriginal() {
        var al = new JSONArray();
        if(inactiveBollards != null) {
            for (var b : inactiveBollards) {
                al.put(new JSONObject().put("bollardNo", b.getBollardNo()).put("reason", b.getReason()));
            }
        }
        return new RoutineReview(id, creationDate, operator, bollardsTotal, station.toOriginal(), bikesAtStation, graffiti, weeds, reports, al.toString());
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Bollard {

        private int bollardNo;
        private String reason;
    }
}
