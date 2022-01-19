package com.beskyd.ms_control.business.requests;

import com.beskyd.ms_control.business.repairreports.response.BikeStationsResponse;
import com.beskyd.ms_control.business.repairreports.response.BikesResponse;
import com.beskyd.ms_control.business.repairreports.response.RepairReasonsResponse;
import com.beskyd.ms_control.business.repairreports.response.RoutineReviewResponse;
import lombok.*;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SaveRoutineReviewRequest {

    private RoutineReviewResponse review;

    private List<ReportRequest> reports;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class ReportRequest {
        private BikeStationsResponse location;
        private BikesResponse bike;
        private String streetComments;
        private String bollardNumbers;
        @EqualsAndHashCode.Exclude
        private String bollardComments;
        private boolean vandalism;
        private boolean pendingCollection;
        private Set<RepairReasonsResponse> repairReason;
        private String operator;
        private boolean onStreetRepair;

    }
}
