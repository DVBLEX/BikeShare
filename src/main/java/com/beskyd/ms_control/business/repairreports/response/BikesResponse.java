package com.beskyd.ms_control.business.repairreports.response;

import com.beskyd.ms_control.business.general.SchemeResponseItem;
import com.beskyd.ms_control.business.repairreports.entity.Bikes;
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
public class BikesResponse {
    
    private Integer id;
    
    private SchemeResponseItem scheme;
    
    private String number;

    public BikesResponse(Bikes original) {
        this.id = original.getId();
        this.scheme = new SchemeResponseItem(original.getScheme());
        this.number = original.getNumber();
    }
    
    public static List<BikesResponse> createListFrom(List<Bikes> originals){
        List<BikesResponse> list = new ArrayList<>();
        
        for(Bikes or : originals) {
            list.add(new BikesResponse(or));
        }
        
        return list;
    }
    
    public static Set<BikesResponse> createSetFrom(Set<Bikes> originals){
        Set<BikesResponse> set = new HashSet<>();

        for(Bikes or : originals) {
            set.add(new BikesResponse(or));
        }

        return set;
    }

    public Bikes toOriginal() {
        return new Bikes(id, scheme.toOriginal(), number);
    }
    
    public static Set<Bikes> toOriginalsSet(Set<BikesResponse> responses){
        Set<Bikes> set = new HashSet<>();
        
        for(BikesResponse resp : responses) {
            set.add(resp.toOriginal());
        }
        
        return set;
    }
}
