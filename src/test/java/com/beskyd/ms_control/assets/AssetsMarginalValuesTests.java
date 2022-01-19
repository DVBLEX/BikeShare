package com.beskyd.ms_control.assets;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.SchemeService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValuesService;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
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

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class AssetsMarginalValuesTests {

    @Autowired
    private AssetsMarginalValuesService amvService;
    
    @Autowired
    private SchemeService schemaService;
    
    @Autowired
    private TypeOfAssetsService typeOfAssetsService;
    
    @Autowired
    private WebApplicationContext wac;
    
    private MockMvc mockMvc;
    
    @Before 
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    @Test
    public void saveDeleteTest() {
        List<TypeOfAssets> productsTypes = typeOfAssetsService.findAll();
        if(productsTypes.isEmpty()) {
            Assert.fail("There are no product types in database");
        }
        List<Scheme> schemas = schemaService.findAll();
        if(schemas.isEmpty()) {
            Assert.fail("There are no schemas in database");
        }
        if(amvService.findByComplexId(productsTypes.get(0), schemas.get(0)) != null) {
            Assert.fail("Zero product already has it's marginal values for Zero scheme. Can't proceed with test");
        }
        
        AssetsMarginalValues amv = new AssetsMarginalValues(productsTypes.get(0), 120, 20, 25, schemas.get(0));
        
        //int amvCountInDB = amvService.findAll().size();
        
        //System.out.println(amv.toString());
        
        amvService.save(amv);
        
        Assert.assertNotNull(amvService.findByComplexId(productsTypes.get(0), schemas.get(0)));
        
        amvService.deleteByComplexId(amv.getProductTypeId(), amv.getSchemeName());
        
        Assert.assertNull(amvService.findByComplexId(productsTypes.get(0), schemas.get(0)));
        
    }
}
