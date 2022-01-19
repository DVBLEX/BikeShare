package com.beskyd.ms_control.business.repairreports.entity;

import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RepairReasons implements JsonAware{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String reason;
    
    private int forWhat;
}
