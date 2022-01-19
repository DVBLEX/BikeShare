package com.beskyd.ms_control.business.distributions;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution, Integer>{
    
    Distribution findByStockRequest_Id(@NotNull Integer id);
    
    @Query("select d from Distribution d left join fetch d.assets where d.stockRequest != null")
    List<Distribution> findDistributionsWhereStockRequestNotNull();
}
