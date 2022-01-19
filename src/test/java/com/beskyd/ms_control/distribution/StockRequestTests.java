package com.beskyd.ms_control.distribution;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.business.assetsprofiles.ProductService;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.SchemeService;
import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValuesService;
import com.beskyd.ms_control.business.stockrequests.RequestProductsTypesList;
import com.beskyd.ms_control.business.stockrequests.StockRequest;
import com.beskyd.ms_control.business.stockrequests.StockRequestRepository;
import com.beskyd.ms_control.business.stockrequests.StockRequestService;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class StockRequestTests {

    @Autowired
    private StockRequestService stockRequestsService;
    
    @Autowired
    private StockRequestRepository stockRequestsRepository;
    
    @Autowired
    private AssetsCurrentValuesService acvService;
    
    @Autowired
    private AssetsMarginalValuesService amvService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private TypeOfAssetsService typesService;
    
    @Autowired
    private SchemeService schemeService;
    
    @Autowired
    private StatesRepository statesRepo;
    
    @Test
    public void generateRequestTest() {
        TypeOfAssets type1 = new TypeOfAssets("Car Assets", "GRT 1", "t gn");
        TypeOfAssets type1_p = typesService.findByName("GRT 1");
        if(type1_p == null) {
            typesService.save(type1);
        } else {
            type1 = type1_p;
        }
        Assert.assertNotNull(type1.getId());
        
        TypeOfAssets type2 = new TypeOfAssets("Car Assets", "GRT 2", "t gn");
        TypeOfAssets type2_p = typesService.findByName("GRT 2");
        if(type2_p == null) {
            typesService.save(type2);
        } else {
            type2 = type2_p;
        }
        Assert.assertNotNull(type2.getId());
        
        TypeOfAssets type3 = new TypeOfAssets("Car Assets", "GRT 3", "t gn");
        TypeOfAssets type3_p = typesService.findByName("GRT 3");
        if(type3_p == null) {
            typesService.save(type3);
        } else {
            type3 = type3_p;
        }
        Assert.assertNotNull(type3.getId());
        
        List<Scheme> schemes = schemeService.findAll();
        if(schemes.isEmpty()) {
            Assert.fail("There are no schemes in database");
        }
        
        for(Scheme scheme : schemes) {
            AssetsMarginalValues amv = new AssetsMarginalValues(type1, 130, 20, 25, scheme);
            AssetsCurrentValues acv = new AssetsCurrentValues(type1, scheme, 21);
            
            amvService.save(amv);
            acvService.save(acv);
        }
        
        for(Scheme scheme : schemes) {
            AssetsMarginalValues amv = new AssetsMarginalValues(type2, 130, 20, 25, scheme);
            AssetsCurrentValues acv = new AssetsCurrentValues(type2, scheme, 21);
            
            amvService.save(amv);
            acvService.save(acv);
        }
        
        for(Scheme scheme : schemes) {
            AssetsMarginalValues amv = new AssetsMarginalValues(type3, 130, 20, 25, scheme);
            AssetsCurrentValues acv = new AssetsCurrentValues(type3, scheme, 50);
            
            amvService.save(amv);
            acvService.save(acv);
        }
        
        Integer requestId = stockRequestsService.generateRequestForScheme(schemes.get(0), false);
        Assert.assertNotNull("Request was not generated", requestId);
        
        StockRequest request = stockRequestsService.findById(requestId);
        Assert.assertNotNull(request);
        
        //check if request has type1 and type2
        
        boolean hasType1 = false;
        boolean hasType2 = false;
        boolean hasType3 = false;
        
        for(RequestProductsTypesList t : request.getRequestedProductTypes()) {
            if(t.getProductType().getId() == type1.getId()) {
                hasType1 = true;
            }
            if(t.getProductType().getId() == type2.getId()) {
                hasType2 = true;
            }
            if(t.getProductType().getId() == type3.getId()) {
                hasType3 = true;
            }
        }
        
        Assert.assertTrue("Request did not include type1", hasType1);
        Assert.assertTrue("Request did not include type2", hasType2);
        Assert.assertFalse("Request should not include type3", hasType3);
        
        
        try{
            stockRequestsRepository.deleteById(requestId);
            
            acvService.deleteByProductTypeId(type1.getId());
            acvService.deleteByProductTypeId(type2.getId());
            acvService.deleteByProductTypeId(type3.getId());
            
            amvService.deleteByProductTypeId(type1.getId());
            amvService.deleteByProductTypeId(type2.getId());
            amvService.deleteByProductTypeId(type3.getId());
            
            typesService.deleteById(type1.getId());
            typesService.deleteById(type2.getId());
            typesService.deleteById(type3.getId());
        } catch(Exception e) {
            
        }
        
    }
    
    @Test
    public void findRequestsInDayBeforeScopeTest() {
        StockRequest sr = new StockRequest(null, new ArrayList<>(), new Scheme("Cork"), statesRepo.findById(StockRequestService.NEW_REQUEST).get(), true, "");
        sr.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
        
        List<StockRequest> saved = stockRequestsService.save(sr);
        
        
        Assert.assertNotEquals(0, stockRequestsService.findInRangeOfDateToNow(Timestamp.valueOf(LocalDateTime.now().minusDays(1))).size());
        Assert.assertEquals(0, stockRequestsService.findInRangeOfDateToNow(Timestamp.valueOf(LocalDateTime.now())).size());
        
        for(var request : saved) {
            stockRequestsService.delete(request.getId());
        }
    }
}
