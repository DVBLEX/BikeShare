package com.beskyd.ms_control.distribution;

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
import com.beskyd.ms_control.business.assetsprofiles.Product;
import com.beskyd.ms_control.business.assetsprofiles.ProductService;
import com.beskyd.ms_control.business.purchaseorders.SplitPurchaseOrderQueue;
import com.beskyd.ms_control.business.purchaseorders.SplitPurchaseOrderQueueService;
import com.beskyd.ms_control.business.suppliers.SupplierService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class SplitPurchaseOrderQueueTests {

    @Autowired
    private SplitPurchaseOrderQueueService splitPurchaseOrderQueueService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private SupplierService supplierService;
    
    @Test
    public void saveDeleteTest() {
        Product product = productService.findById(1);
        if(product == null) {
            Assert.fail("There is no product with id 1 in the database. Can't proceed with the test");
        }
        
        SplitPurchaseOrderQueue element = new SplitPurchaseOrderQueue(null, product, 30, null);
        
        splitPurchaseOrderQueueService.save(element);
        
        if(element.getId() == null) {
            Assert.fail("Element wasn't saved");
        }
            
        Assert.assertNotNull("Element wasn't saved", splitPurchaseOrderQueueService.findById(element.getId()));
        
        splitPurchaseOrderQueueService.delete(element.getId());
        
        Assert.assertNull("Element wasn't deleted", splitPurchaseOrderQueueService.findById(element.getId()));
    }
}
