package com.beskyd.ms_control.assets;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.schemestocksontrol.centraldepot.CentralDepot;
import com.beskyd.ms_control.business.schemestocksontrol.centraldepot.CentralDepotService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class CentralDepotTests {

    @Autowired
    private CentralDepotService centralDepotService;
    
    @Autowired
    private TypeOfAssetsService typesService;
    
    @Test
    public void saveTest() {
        List<TypeOfAssets> productTypes = typesService.findAll();
        if(productTypes.isEmpty()) {
            Assert.fail("There are no product types in database");
        }
        
        CentralDepot cd = new CentralDepot(productTypes.get(0), 40);
        centralDepotService.save(cd);
        CentralDepot saved = centralDepotService.findByProductTypeId(cd.getProductTypeId());
        Assert.assertNotNull(saved);
        //Assert.assertTrue(cd == saved); //it is not the same object
        Assert.assertEquals(cd.getAmount(), saved.getAmount());
    }
}
