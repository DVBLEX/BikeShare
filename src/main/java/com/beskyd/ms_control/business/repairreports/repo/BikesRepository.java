package com.beskyd.ms_control.business.repairreports.repo;

import com.beskyd.ms_control.business.repairreports.entity.Bikes;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BikesRepository extends JpaRepository<Bikes, Integer>{
    
    List<Bikes> findBySchemeName(String schemeName, Sort sort);
    
    List<Bikes> findBySchemeNameAndNumberIn(String schemeName, List<String> numbers, Sort sort);
}
