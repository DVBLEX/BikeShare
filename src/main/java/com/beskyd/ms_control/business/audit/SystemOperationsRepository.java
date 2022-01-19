package com.beskyd.ms_control.business.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemOperationsRepository extends JpaRepository<SystemOperations, Integer>{

    
}
