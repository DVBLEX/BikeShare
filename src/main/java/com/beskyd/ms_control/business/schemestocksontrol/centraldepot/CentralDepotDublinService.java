package com.beskyd.ms_control.business.schemestocksontrol.centraldepot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;

@Service
public class CentralDepotDublinService implements CentralDepotService{

    @Autowired
    public AssetsCurrentValuesService acvService;
    
    private static final String DUBLIN = "Dublin";
    
    @Override
    public List<CentralDepot> findAll() {
        List<AssetsCurrentValues> acvDulin = acvService.findByScheme(new Scheme(DUBLIN));
        List<CentralDepot> cdAll = new ArrayList<>();
        
        for(AssetsCurrentValues acv : acvDulin) {
            cdAll.add(new CentralDepot(acv.getProductType(), acv.getQuantity()));
        }
        
        return cdAll;
    }

    @Override
    public CentralDepot findByProductTypeId(Integer productTypeId) {
        AssetsCurrentValues acv = acvService.findByComplexId(productTypeId, DUBLIN);
        if(acv != null) {
            return new CentralDepot(acv.getProductType(), acv.getQuantity());
        } else {
            return null;
        }
    }

    @Override
    public CentralDepot save(CentralDepot savable) {
        acvService.save(new AssetsCurrentValues(savable.getProductType(), new Scheme(DUBLIN), savable.getAmount()));
        return savable;
    }

}
