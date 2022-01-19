package com.beskyd.ms_control.distribution;

import java.util.ArrayList;
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
import com.beskyd.ms_control.business.assetsprofiles.ProductService;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.SchemeService;
import com.beskyd.ms_control.business.general.States;
import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.purchaseorders.PurchaseOrder;
import com.beskyd.ms_control.business.purchaseorders.PurchaseOrderProducts;
import com.beskyd.ms_control.business.purchaseorders.PurchaseOrderService;
import com.beskyd.ms_control.business.purchaseorders.SplitPurchaseOrderQueue;
import com.beskyd.ms_control.business.stockrequests.RequestProductsTypesList;
import com.beskyd.ms_control.business.stockrequests.StockRequest;
import com.beskyd.ms_control.business.stockrequests.StockRequestService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class PurchaseOrdersTests {
    
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    
    @Autowired
    private StockRequestService stockRequestsSerivce;    
    
    @Autowired
    private StatesRepository statesRepo;
    
    @Autowired
    private TypeOfAssetsService typesService;
    
    @Autowired
    private SchemeService schemeService;
    
    @Autowired
    private ProductService productService;
    
    @Test
    public void generateOrderTest() {
        States stateNewSR = statesRepo.getOne(1);
        
        States stateMergedSR = statesRepo.getOne(2);
        
        TypeOfAssets pt1 = typesService.findById(1);
        Assert.assertNotNull(pt1);
        TypeOfAssets pt2 = typesService.findById(2);
        Assert.assertNotNull(pt2);
        TypeOfAssets pt3 = typesService.findById(3);
        Assert.assertNotNull(pt3);
        TypeOfAssets pt4 = typesService.findById(4);
        Assert.assertNotNull(pt4);
        TypeOfAssets pt5 = typesService.findById(5);
        Assert.assertNotNull(pt5);
        TypeOfAssets pt7 = typesService.findById(7);
        Assert.assertNotNull(pt7);
        TypeOfAssets pt8 = typesService.findById(8);
        Assert.assertNotNull(pt8);
        TypeOfAssets pt9 = typesService.findById(9);
        Assert.assertNotNull(pt9);
        TypeOfAssets pt10 = typesService.findById(10);
        Assert.assertNotNull(pt10);
        TypeOfAssets pt11 = typesService.findById(11);
        Assert.assertNotNull(pt11);
        TypeOfAssets pt12 = typesService.findById(12);
        Assert.assertNotNull(pt12);
        TypeOfAssets pt13 = typesService.findById(13);
        Assert.assertNotNull(pt13);
        
        Scheme scheme1 = schemeService.findAll().get(0);
        Scheme scheme2 = schemeService.findAll().get(1);
        
        StockRequest sr1 = new StockRequest(null, new ArrayList<>(), scheme1, stateNewSR, false, "");
        sr1.getRequestedProductTypes().add(new RequestProductsTypesList(sr1, pt1, 30));
        sr1.getRequestedProductTypes().add(new RequestProductsTypesList(sr1, pt2, 30));
        sr1.getRequestedProductTypes().add(new RequestProductsTypesList(sr1, pt3, 30));
        sr1.getRequestedProductTypes().add(new RequestProductsTypesList(sr1, pt4, 30));
        sr1.getRequestedProductTypes().add(new RequestProductsTypesList(sr1, pt5, 30));
        sr1.getRequestedProductTypes().add(new RequestProductsTypesList(sr1, pt7, 30));
        
        StockRequest sr2 = new StockRequest(null, new ArrayList<>(), scheme2, stateNewSR, false, "");
        sr2.getRequestedProductTypes().add(new RequestProductsTypesList(sr2, pt7, 20));
        sr2.getRequestedProductTypes().add(new RequestProductsTypesList(sr2, pt8, 20));
        sr2.getRequestedProductTypes().add(new RequestProductsTypesList(sr2, pt1, 20));
        sr2.getRequestedProductTypes().add(new RequestProductsTypesList(sr2, pt2, 20));
        
        StockRequest sr3 = new StockRequest(null, new ArrayList<>(), scheme1, stateMergedSR, false, "");
        sr3.getRequestedProductTypes().add(new RequestProductsTypesList(sr3, pt9, 40));
        
        List<PurchaseOrder> orders = purchaseOrderService.generatePurchaseOrders(List.of(sr1, sr2, sr3));
        Assert.assertEquals(3, orders.size());//We should get 3 orders, because we have 3 suppliers for products
        
        PurchaseOrder firstOrder = orders.get(0);
        PurchaseOrder secondOrder = orders.get(1);
        PurchaseOrder thirdOrder = orders.get(2);
        
        if(firstOrder.getSupplier().getId() == 2) {
            PurchaseOrder temp = firstOrder;
            firstOrder = secondOrder;
            secondOrder = temp;
        }
        
        Assert.assertTrue(firstOrder.getOrderedProducts().size() > secondOrder.getOrderedProducts().size());
        
        //Here below we are checking, if products is in the right orders
        for(PurchaseOrderProducts pp : firstOrder.getOrderedProducts()) {
            if(pp.getProduct().getType().getId().equals(pt1.getId()) && firstOrder.getId() == null) {
                Assert.assertEquals(50, (int) pp.getAmount());
            }
            if(pp.getProduct().getType().getId().equals(pt7.getId())) {
                Assert.fail();
            }
            if(pp.getProduct().getType().getId().equals(pt8.getId())) {
                Assert.fail();
            }
            
            Assert.assertEquals(firstOrder.getSupplier().getId(), pp.getProduct().getProductId().getSupplier().getId());
        }
        
        boolean containsPt8 = false;
        for(PurchaseOrderProducts pp : secondOrder.getOrderedProducts()) {
            if(pp.getProduct().getType().getId().equals(pt8.getId())) {
                containsPt8 = true;
                if(secondOrder.getId() == null) {
                    Assert.assertEquals(20, (int) pp.getAmount());
                }
            }
            
            if(pp.getProduct().getType().getId().equals(pt7.getId())) {
                Assert.fail();
            }
            
            Assert.assertEquals(secondOrder.getSupplier().getId(), pp.getProduct().getProductId().getSupplier().getId());
        }
        
        Assert.assertTrue(containsPt8);
        
        boolean containsPt7 = false;
        for(PurchaseOrderProducts pp : thirdOrder.getOrderedProducts()) {
            if(pp.getProduct().getType().getId().equals(pt7.getId()) && thirdOrder.getId() == null) {
                containsPt7 = true;
                Assert.assertEquals(50, (int) pp.getAmount());
            }
            
            Assert.assertEquals(thirdOrder.getSupplier().getId(), pp.getProduct().getProductId().getSupplier().getId());
        }
        
        Assert.assertTrue(containsPt7);  
    }
    
    @Test
    public void generateOrdersFromQueueTest() {
        
        SplitPurchaseOrderQueue record1_10 = new SplitPurchaseOrderQueue(null, productService.findById(1), 10, null);
        SplitPurchaseOrderQueue record1_30 = new SplitPurchaseOrderQueue(null, productService.findById(1), 35, null);
        SplitPurchaseOrderQueue record2 = new SplitPurchaseOrderQueue(null, productService.findById(2), 25, null);
        SplitPurchaseOrderQueue record7 = new SplitPurchaseOrderQueue(null, productService.findById(7), 20, null);
        SplitPurchaseOrderQueue record8 = new SplitPurchaseOrderQueue(null, productService.findById(8), 40, null);
        SplitPurchaseOrderQueue record14 = new SplitPurchaseOrderQueue(null, productService.findById(14), 70, null);
        
        List<SplitPurchaseOrderQueue> queue = List.of(record1_10, record1_30, record2, record7, record8, record14);
        
        List<PurchaseOrder> orders = purchaseOrderService.generatePurchaseOrdersFromSplitQueue(queue);
        
        Assert.assertEquals(2, orders.size());
        
        boolean hasNo1Product = false;
        for(PurchaseOrderProducts pp : orders.get(0).getOrderedProducts()) {
            if(pp.getProduct().getId() == 8 || pp.getProduct().getId() == 14) {
                Assert.fail();
            }
            if(pp.getProduct().getId() == 1) {
                if(hasNo1Product) {
                    Assert.fail("The same product is ordered the second time");
                }
                hasNo1Product = true;
                
                if( orders.get(0).getId() == null) {
                    Assert.assertEquals(45, pp.getAmount().intValue());
                }
            }
        }
        
        boolean hasNo8Product = false;
        boolean hasNo14Product = false;
        for(PurchaseOrderProducts pp : orders.get(1).getOrderedProducts()) {
            if(pp.getProduct().getId() == 1 || pp.getProduct().getId() == 2 || pp.getProduct().getId() == 7) {
                Assert.fail();
            }
            if(pp.getProduct().getId() == 8) {
                hasNo8Product = true;
                if(orders.get(1).getId() == null) {
                    Assert.assertEquals(40, pp.getAmount().intValue());
                }
            } else
            if(pp.getProduct().getId() == 14) {
                hasNo14Product = true;
            }
        }
        
        Assert.assertTrue(hasNo8Product);
        Assert.assertTrue(hasNo14Product);
    }

}
