package com.beskyd.ms_control.business.schemestocksontrol.centraldepot;

import java.util.List;

public interface CentralDepotService {
    
    public List<CentralDepot> findAll();
    
    /**
     * Find {@link CentralDepot} by productTypeId (which is its primary key)
     * @param productTypeId
     * @return {@link CentralDepot} object if present, null - if not
     */
    public CentralDepot findByProductTypeId(Integer productTypeId);
    
    public CentralDepot save(CentralDepot savable);
}