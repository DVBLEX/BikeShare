package com.beskyd.ms_control.business.repairreports.response;

import com.beskyd.ms_control.business.general.SchemeResponseItem;
import com.beskyd.ms_control.business.repairreports.entity.BikeStations;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BikeStationsResponse {
    
    private Integer id;
    
    private SchemeResponseItem scheme;
    
    private String location;

    @EqualsAndHashCode.Exclude
    private Double geoLat;

    @EqualsAndHashCode.Exclude
    private Double geoLong;

    private Integer bollardsTotalNumber;
    
    public BikeStationsResponse(BikeStations original) {
        this.id = original.getId();
        this.scheme = new SchemeResponseItem(original.getScheme());
        this.location = original.getLocation();
        this.geoLat = original.getGeoLat();
        this.geoLong = original.getGeoLong();
        this.bollardsTotalNumber = original.getBollardsTotalNumber();
    }
    
    public static List<BikeStationsResponse> createListFrom(List<BikeStations> originals){
        List<BikeStationsResponse> list = new ArrayList<>();
        
        for(BikeStations or : originals) {
            list.add(new BikeStationsResponse(or));
        }
        
        return list;
    }

    public BikeStations toOriginal() {
        return new BikeStations(id, scheme.toOriginal(), location, geoLat, geoLong, bollardsTotalNumber);
    }

    @Override
    public String toString() {
        return "BikeStationsResponse [id=" + id + ", scheme=" + scheme + ", location=" + location + ", geoLat=" + geoLat + ", geoLong=" + geoLong + ", bollardsTotalNumber=" + bollardsTotalNumber + "]";
    }
    
    
}
