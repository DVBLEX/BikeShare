package com.beskyd.ms_control.assets;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
@Rollback
public class TypeOfAssetsTests {

    @Autowired
    private TypeOfAssetsService typeOfAssetsService;
    
    @Test
    public void saveDeleteTest() {
        TypeOfAssets type = new TypeOfAssets();
        type.setAssetGroup("Byke Assets");
        type.setTypeName("test_ Bike Chain");
        
        typeOfAssetsService.save(type);
        
        Assert.assertNotNull(typeOfAssetsService.findByName(type.getTypeName()));
        
        typeOfAssetsService.deleteByName(type.getTypeName());
        
        Assert.assertNull(typeOfAssetsService.findByName(type.getTypeName()));
    }
}
