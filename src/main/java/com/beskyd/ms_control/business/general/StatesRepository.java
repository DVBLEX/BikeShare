package com.beskyd.ms_control.business.general;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatesRepository extends JpaRepository<States, Integer>{
    
    List<States> findByType(int type);
}
