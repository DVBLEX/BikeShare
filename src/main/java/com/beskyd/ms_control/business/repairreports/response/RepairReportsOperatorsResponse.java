package com.beskyd.ms_control.business.repairreports.response;

import com.beskyd.ms_control.business.repairreports.entity.RepairReports;
import com.beskyd.ms_control.business.repairreports.entity.RepairReportsOperators;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RepairReportsOperatorsResponse {
    
    private Integer id;
    
    private Integer reportId;

    private String userName;
    
    private Integer timeOfWorkMilis;
    
    private String jobsDone;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
//    @JsonFormat(pattern="dd/MM/yyyy")
    private Timestamp jobDoneDatetime;

    @EqualsAndHashCode.Exclude
    private Set<ReportUsedAssetsResponse> usedSpareparts;

    public RepairReportsOperatorsResponse(RepairReportsOperators original) {

        this(original.getId(),
                original.getReport() !=null ? original.getReport().getId() : null,
                original.getUserName(),
                original.getTimeOfWorkMillis(),
                original.getJobsDone(),
                original.getJobDoneDatetime(),
                ReportUsedAssetsResponse.createSetFrom(original.getUsedSpareparts()));
    }
    
    public static Set<RepairReportsOperatorsResponse> createSetFrom(Set<RepairReportsOperators> originals){
        Set<RepairReportsOperatorsResponse> list = new HashSet<>();

        for(RepairReportsOperators or : originals) {
            list.add(new RepairReportsOperatorsResponse(or));
        }

        return list;
    }
    
    public RepairReportsOperators toOriginal(RepairReports report) {
        RepairReportsOperators operator = new RepairReportsOperators(id, report, userName, timeOfWorkMilis, jobsDone, jobDoneDatetime, null);
        operator.setUsedSpareparts(ReportUsedAssetsResponse.toOriginals(usedSpareparts, operator));
        
        return operator;
    }
    
    public static Set<RepairReportsOperators> toOriginals(Set<RepairReportsOperatorsResponse> responses, RepairReports report){
        Set<RepairReportsOperators> originals = new HashSet<>();
        
        for(var resp : responses) {
            var original = resp.toOriginal(report);
            originals.add(original);
        }
        
        return originals;
    }
}
