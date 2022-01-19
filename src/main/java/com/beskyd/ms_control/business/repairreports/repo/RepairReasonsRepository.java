package com.beskyd.ms_control.business.repairreports.repo;

import com.beskyd.ms_control.business.repairreports.entity.RepairReasons;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairReasonsRepository extends JpaRepository<RepairReasons, Integer>{

    /**
     * 
     * @param forWhat 1 - station, 2 - bike
     * @return
     */
    List<RepairReasons> findByForWhat(int forWhat, Sort sort);
}
