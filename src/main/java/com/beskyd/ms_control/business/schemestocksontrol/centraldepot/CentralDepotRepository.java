package com.beskyd.ms_control.business.schemestocksontrol.centraldepot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentralDepotRepository extends JpaRepository<CentralDepot, Integer>{
    
    
}
