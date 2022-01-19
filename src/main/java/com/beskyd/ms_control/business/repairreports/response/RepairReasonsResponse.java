package com.beskyd.ms_control.business.repairreports.response;

import com.beskyd.ms_control.business.repairreports.entity.RepairReasons;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RepairReasonsResponse {
    
    private Integer id;
    
    private String reason;
    
    private int forWhat;

    public RepairReasonsResponse(RepairReasons original) {
        this.id = original.getId();
        this.reason = original.getReason();
        this.forWhat = original.getForWhat();
    }
    
    public static List<RepairReasonsResponse> createListFrom(List<RepairReasons> originals){
        List<RepairReasonsResponse> list = new ArrayList<>();
        
        for(RepairReasons or : originals) {
            list.add(new RepairReasonsResponse(or));
        }
        
        return list;
    }
    
    public static Set<RepairReasonsResponse> createSetFrom(Set<RepairReasons> originals){
        Set<RepairReasonsResponse> set = new HashSet<>();
        
        for(RepairReasons or : originals) {
            set.add(new RepairReasonsResponse(or));
        }
        
        return set;
    }
    
    public RepairReasons toOriginal() {
        return new RepairReasons(id, reason, forWhat);
    }
    
    public static Set<RepairReasons> toOriginals(Set<RepairReasonsResponse> responses){
        Set<RepairReasons> set = new HashSet<>();
        
        for(RepairReasonsResponse resp : responses) {
            set.add(resp.toOriginal());
        }
        
        return set;
    }

}
