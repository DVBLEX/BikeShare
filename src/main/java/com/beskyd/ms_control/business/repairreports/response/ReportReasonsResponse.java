package com.beskyd.ms_control.business.repairreports.response;

import com.beskyd.ms_control.business.repairreports.entity.ReportReasons;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ReportReasonsResponse {

    private Integer id;
    
    private String reason;

    public ReportReasonsResponse(ReportReasons original) {
        this.id = original.getId();
        this.reason = original.getReason();
    }
    
    public static List<ReportReasonsResponse> createListFrom(List<ReportReasons> originals){
        List<ReportReasonsResponse> list = new ArrayList<>();
        
        for(ReportReasons or : originals) {
            list.add(new ReportReasonsResponse(or));
        }
        
        return list;
    }
    
    public ReportReasons toOriginal() {
        return new ReportReasons(id, reason);
    }
}
