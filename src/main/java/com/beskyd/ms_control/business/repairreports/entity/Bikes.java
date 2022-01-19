package com.beskyd.ms_control.business.repairreports.entity;

import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Bikes implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "scheme_name")
    private Scheme scheme;
    
    private String number;
}
