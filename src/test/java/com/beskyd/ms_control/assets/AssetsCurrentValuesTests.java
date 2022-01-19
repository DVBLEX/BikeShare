package com.beskyd.ms_control.assets;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.SchemeService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class AssetsCurrentValuesTests {

    @Autowired
    private AssetsCurrentValuesService acvService;
    
    @Autowired
    private SchemeService schemeService;
    
    @Autowired
    private TypeOfAssetsService typesService;
    
    @Autowired
    private WebApplicationContext wac;
    
    private MockMvc mockMvc;
    
    @Before 
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    @Test
    public void saveDeleteTest() {
        List<TypeOfAssets> productTypes = typesService.findAll();
        if(productTypes.isEmpty()) {
            Assert.fail("There are no product types in database");
        }
        List<Scheme> schemas = schemeService.findAll();
        if(schemas.isEmpty()) {
            Assert.fail("There are no schemas in database");
        }
        if(acvService.findByComplexId(productTypes.get(0), schemas.get(0)) != null) {
            Assert.fail("Zero product already has it's current values for Zero scheme. Can't proceed with test");
        }
        
        AssetsCurrentValues acv = new AssetsCurrentValues(productTypes.get(0), schemas.get(0), 20);
        
        acvService.save(acv);
        
        Assert.assertNotNull(acvService.findByComplexId(productTypes.get(0), schemas.get(0)));
        
        acvService.deleteByComplexId(acv.getProductTypeId(), acv.getSchemeName());
        
        Assert.assertNull(acvService.findByComplexId(productTypes.get(0), schemas.get(0)));
    
    }
}
