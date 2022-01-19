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
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.distributions.Distribution;
import com.beskyd.ms_control.business.distributions.DistributionAssets;
import com.beskyd.ms_control.business.distributions.DistributionService;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsTransferQueue;
import com.beskyd.ms_control.business.stockrequests.RequestProductsTypesList;
import com.beskyd.ms_control.business.stockrequests.StockRequest;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class DistributionTests {

    @Autowired
    private DistributionService distributionService;
    
    @Autowired
    private TypeOfAssetsService typeOfAssetsService;
    
    @Autowired
    private StatesRepository statesRepo;
    
    @Test
    public void generateFromStockRequestTest() {
        
        List<RequestProductsTypesList> requestedProductTypes = new ArrayList<>();
        
        StockRequest stockRequest = new StockRequest(null, requestedProductTypes, new Scheme("Cork"), statesRepo.findById(1).get(), false, "");
        
        requestedProductTypes.add(new RequestProductsTypesList(stockRequest, typeOfAssetsService.findById(1), 50));//in central depot there are only 40
        requestedProductTypes.add(new RequestProductsTypesList(stockRequest, typeOfAssetsService.findById(2), 100));
        requestedProductTypes.add(new RequestProductsTypesList(stockRequest, typeOfAssetsService.findById(3), 20));
        
        Distribution distribution = distributionService.generateFromStockRequest(stockRequest);
        
        boolean hasType1 = false;
        boolean hasType2 = false;
        boolean hasType3 = false;
        for(var da : distribution.getAssets()) {
            if(da.getTypeOfAssets().getId() == 1) {
                Assert.assertEquals(40, da.getQuantity());
                hasType1 = true;
            }
            if(da.getTypeOfAssets().getId() == 2) {
                Assert.assertEquals(100, da.getQuantity());
                hasType2 = true;
            }
            if(da.getTypeOfAssets().getId() == 3) {
                Assert.assertEquals(20, da.getQuantity());
                hasType3 = true;
            }

        }
        Assert.assertTrue(hasType1);
        Assert.assertTrue(hasType2);
        Assert.assertTrue(hasType3);
    }
    
    @Test
    public void generateFromAssetsTransferQueueTest() {
        List<AssetsTransferQueue> transferQueue = new ArrayList<>();
        
        TypeOfAssets type1 = typeOfAssetsService.findById(1);
        transferQueue.add(new AssetsTransferQueue(type1, new Scheme("Limerick"), new Scheme("Cork"), 30));
        transferQueue.add(new AssetsTransferQueue(type1, new Scheme("Limerick"), new Scheme("Cork"), 50));//2 times the same type of assets;
        transferQueue.add(new AssetsTransferQueue(typeOfAssetsService.findById(2), new Scheme("Dublin"), new Scheme("Galway"), 90));
        transferQueue.add(new AssetsTransferQueue(typeOfAssetsService.findById(3), new Scheme("Dublin"), new Scheme("Limerick"),75));
        transferQueue.add(new AssetsTransferQueue(typeOfAssetsService.findById(3), new Scheme("Galway"), new Scheme("Cork"), 80));
        transferQueue.add(new AssetsTransferQueue(typeOfAssetsService.findById(4), new Scheme("Limerick"), new Scheme("Cork"), 85));//the same route as type1
        transferQueue.add(new AssetsTransferQueue(typeOfAssetsService.findById(5), new Scheme("Dublin"), new Scheme("Galway"), 78));//the same route as type2
        transferQueue.add(new AssetsTransferQueue(typeOfAssetsService.findById(7), new Scheme("Dublin"), new Scheme("Galway"), 54));//the same route as type2
        
        List<Distribution> distributions = distributionService.generateFromTransferQueue(transferQueue);
        
        Assert.assertEquals(4, distributions.size());
        
        Assert.assertEquals(1, distributions.stream()
                                            .filter(d -> d.getSchemeFrom().getName().equals("Limerick") && d.getSchemeTo().getName().equals("Cork"))
                                            .count());
        Assert.assertEquals(1, distributions.stream()
                                            .filter(d -> d.getSchemeFrom().getName().equals("Dublin") && d.getSchemeTo().getName().equals("Galway"))
                                            .count());
        Assert.assertEquals(1, distributions.stream()
                                            .filter(d -> d.getSchemeFrom().getName().equals("Dublin") && d.getSchemeTo().getName().equals("Limerick"))
                                            .count());
        Assert.assertEquals(1, distributions.stream()
                                            .filter(d -> d.getSchemeFrom().getName().equals("Galway") && d.getSchemeTo().getName().equals("Cork"))
                                            .count());
        
        for(Distribution dist : distributions) {
            if(dist.getSchemeFrom().getName().equals("Limerick") && dist.getSchemeTo().getName().equals("Cork")) {
                Assert.assertEquals(2, dist.getAssets().size());
                
                for(DistributionAssets asset : dist.getAssets()) {
                    if(asset.getTypeOfAssets().getId() == 1) {
                        Assert.assertEquals(80, asset.getQuantity());
                    } else if(asset.getTypeOfAssets().getId() == 4) {
                        Assert.assertEquals(85, asset.getQuantity());
                    }
                }
            }
            
            if(dist.getSchemeFrom().getName().equals("Dublin") && dist.getSchemeTo().getName().equals("Galway")) {
                Assert.assertEquals(3, dist.getAssets().size());
                
                for(DistributionAssets asset : dist.getAssets()) {
                    if(asset.getTypeOfAssets().getId() == 2) {
                        Assert.assertEquals(90, asset.getQuantity());
                    }
                    if(asset.getTypeOfAssets().getId() == 5) {
                        Assert.assertEquals(78, asset.getQuantity());
                    }
                    if(asset.getTypeOfAssets().getId() == 7) {
                        Assert.assertEquals(54, asset.getQuantity());
                    }
                }
            }
            
            if(dist.getSchemeFrom().getName().equals("Dublin") && dist.getSchemeTo().getName().equals("Limerick")) {
                Assert.assertEquals(1, dist.getAssets().size());
                
                for(DistributionAssets asset : dist.getAssets()) {
                    if(asset.getTypeOfAssets().getId() == 3) {
                        Assert.assertEquals(75, asset.getQuantity());
                    }
                }
            }
            
            if(dist.getSchemeFrom().getName().equals("Galway") && dist.getSchemeTo().getName().equals("Cork")) {
                Assert.assertEquals(1, dist.getAssets().size());
                
                for(DistributionAssets asset : dist.getAssets()) {
                    if(asset.getTypeOfAssets().getId() == 3) {
                        Assert.assertEquals(80, asset.getQuantity());
                    }
                }
            }
        }
        
    }
}
