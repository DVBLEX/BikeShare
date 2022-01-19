package com.beskyd.ms_control.business.repairreports.repo;

import com.beskyd.ms_control.business.repairreports.entity.BikeStations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BikeStationsRepository extends JpaRepository<BikeStations, Integer>{
    
    List<BikeStations> findBySchemeName(String schemeName);

    @Query("select bs from BikeStations bs where ((:lat - bs.geoLat) * (:lat - bs.geoLat) + (:lng - bs.geoLong) * (:lng - bs.geoLong)) < :delta * :delta")
    BikeStations findByCoords(@Param("lat") double lat, @Param("lng") double lng, @Param("delta") double delta);
}
