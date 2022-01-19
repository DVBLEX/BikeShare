package com.beskyd.ms_control.business.repairreports.entity;

import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BikeStations implements JsonAware{

    @Id
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "scheme_name")
    private Scheme scheme;
    
    private String location;

    @EqualsAndHashCode.Exclude
    private Double geoLat;

    @EqualsAndHashCode.Exclude
    private Double geoLong;

    private Integer bollardsTotalNumber;
}
