package com.beskyd.ms_control.business.repairreports.response;

import com.beskyd.ms_control.business.repairreports.entity.RepairJobs;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RepairJobsResponse {

    private Integer id;
    
    private String job;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private int forWhat;





    public RepairJobsResponse(RepairJobs original) {
        this.id = original.getId();
        this.job = original.getJob();
        this.forWhat = original.getForWhat();
    }
    
    public static List<RepairJobsResponse> createListFrom(List<RepairJobs> originals){
        List<RepairJobsResponse> list = new ArrayList<>();
        
        for(RepairJobs or : originals) {
            list.add(new RepairJobsResponse(or));
        }
        
        return list;
    }
    
    public RepairJobs toOriginal() {
        return new RepairJobs(id, job, forWhat);
    }
}
