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
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsTransferQueue;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsTransferQueueService;
import com.beskyd.ms_control.business.schemestocksontrol.values.IdenticalTransferException;
import com.beskyd.ms_control.business.schemestocksontrol.values.TransferToTheSameSchemeException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class AssetsTransferQueueTests {
    
    @Autowired
    private AssetsTransferQueueService atqService;
    
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
    public void saveDeleteTest() throws TransferToTheSameSchemeException, IdenticalTransferException {
        List<TypeOfAssets> productTypes = typesService.findAll();
        if(productTypes.isEmpty()) {
            Assert.fail("There are no product types in database");
        }
        List<Scheme> schemas = schemeService.findAll();
        if(schemas.size() < 2) {
            Assert.fail("There are no enough schemes in database");
        }
        
        AssetsTransferQueue atq = new AssetsTransferQueue(productTypes.get(0), schemas.get(0), schemas.get(1), 50);
        
        atqService.save(atq);
        
        Assert.assertNotNull(atq.getId());
        
        atqService.deleteById(atq.getId());
        
        Assert.assertNull("The record wasn't deleted", atqService.findById(atq.getId()));
    }
    
    @Test
    public void findIdenticalTest() throws TransferToTheSameSchemeException, IdenticalTransferException {
        List<TypeOfAssets> productTypes = typesService.findAll();
        if(productTypes.isEmpty()) {
            Assert.fail("There are no product types in database");
        }
        List<Scheme> schemas = schemeService.findAll();
        if(schemas.size() < 2) {
            Assert.fail("There are no enough schemas in database");
        }
        
        AssetsTransferQueue atq = new AssetsTransferQueue(productTypes.get(0), schemas.get(0), schemas.get(1), 50);
        
        atqService.save(atq);
        
        Assert.assertNotNull(atq.getId());
        
        Assert.assertNotNull(atqService.findIdentical(atq.getProductType().getId(), atq.getTransferFromScheme().getName(), atq.getTransferToScheme().getName()));
        
        atqService.deleteById(atq.getId());
    }
    
    @Test
    public void transferToTheSameSchemeTest() throws IdenticalTransferException {
        List<TypeOfAssets> productTypes = typesService.findAll();
        if(productTypes.isEmpty()) {
            Assert.fail("There are no product types in database");
        }
        List<Scheme> schemas = schemeService.findAll();
        if(schemas.size() < 2) {
            Assert.fail("There are no enough schemas in database");
        }
        
        AssetsTransferQueue atq = new AssetsTransferQueue(productTypes.get(0), schemas.get(0), schemas.get(0), 50);
        
        try {
            atqService.save(atq);
            
            Assert.fail("Transfer to the same scheme was allowed!");
        } catch (TransferToTheSameSchemeException ex) {
        }
    }
}
