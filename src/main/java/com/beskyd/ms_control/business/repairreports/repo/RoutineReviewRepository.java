package com.beskyd.ms_control.business.repairreports.repo;

import com.beskyd.ms_control.business.repairreports.entity.RoutineReview;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineReviewRepository extends JpaRepository<RoutineReview, Integer>{
    
    List<RoutineReview> findByStation_Scheme_Name(String schemeName, Sort sort);
}
