package com.beskyd.ms_control.business.schemestocksontrol.centraldepot;

import java.util.List;

import javax.inject.Inject;

//@Service("centralDepoRealService")
public class CentralDepotRealService implements CentralDepotService{

    private final CentralDepotRepository repo;

    @Inject
    public CentralDepotRealService(CentralDepotRepository repo) {
        this.repo = repo;
    }
    
    public List<CentralDepot> findAll(){
        return repo.findAll();
    }
    
    public CentralDepot findByProductTypeId(Integer productTypeId) {
        return repo.findById(productTypeId).orElse(null);
    }
    
    public CentralDepot save(CentralDepot savable) {
        return repo.save(savable);
    }
}
