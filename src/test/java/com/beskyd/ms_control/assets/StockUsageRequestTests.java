package com.beskyd.ms_control.assets;

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
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.schemestocksontrol.reports.StockUsageReportAssets;
import com.beskyd.ms_control.business.schemestocksontrol.reports.StockUsageReports;
import com.beskyd.ms_control.business.schemestocksontrol.reports.StockUsageReportsService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class StockUsageRequestTests {
    
    @Autowired
    private StockUsageReportsService reportsService;
    
    @Autowired
    private TypeOfAssetsService typeOfAssetsService;
    
    @Autowired
    private AssetsCurrentValuesService acvService;
    
    @Autowired
    private StatesRepository statesRepo;
    
    @Test
    public void placeTest() {
        Scheme galwayScheme = new Scheme("Galway");
        StockUsageReports report = new StockUsageReports(null, galwayScheme, null, statesRepo.findById(StockUsageReportsService.STATE_PLACED).get(), "--");
        
        AssetsCurrentValues acv2 = acvService.findByComplexId(2, galwayScheme.getName());
        AssetsCurrentValues acv7 = acvService.findByComplexId(7, galwayScheme.getName());
        
        int amount2 = 30;
        int amount7 = 55;
        
        report.getAssets().add(new StockUsageReportAssets(null, typeOfAssetsService.findById(2), amount2));
        report.getAssets().add(new StockUsageReportAssets(null, typeOfAssetsService.findById(7), amount7));
        
        reportsService.saveReport(report);
        Assert.assertNotNull(report.getId());
        
        StockUsageReports placedReport = reportsService.findById(report.getId());
        Assert.assertNotNull(placedReport);
        
        Assert.assertEquals(amount2, (int) placedReport.getAssets().stream().filter(rt -> rt.getTypeOfAssets().getId() == 2).findFirst().get().getAmount());
        Assert.assertEquals(amount7, (int) placedReport.getAssets().stream().filter(rt -> rt.getTypeOfAssets().getId() == 7).findFirst().get().getAmount());
        
        Assert.assertEquals(acv2.getQuantity() + amount2, (int) acvService.findByComplexId(2, galwayScheme.getName()).getQuantity());
        Assert.assertEquals(acv7.getQuantity() + amount7, (int) acvService.findByComplexId(7, galwayScheme.getName()).getQuantity());
        
        Assert.assertEquals("--", placedReport.getNotes());
        
        reportsService.deleteReport(placedReport.getId());
    }
    
    @Test
    public void useTest() {
        Scheme galwayScheme = new Scheme("Galway");
        StockUsageReports report = new StockUsageReports(null, galwayScheme, null, statesRepo.findById(StockUsageReportsService.STATE_USED).get(), "--");
        
        AssetsCurrentValues acv2 = acvService.findByComplexId(2, galwayScheme.getName());
        AssetsCurrentValues acv7 = acvService.findByComplexId(7, galwayScheme.getName());
        
        int amount2 = 20;
        int amount7 = 35;
        
        report.getAssets().add(new StockUsageReportAssets(null, typeOfAssetsService.findById(2), amount2));
        report.getAssets().add(new StockUsageReportAssets(null, typeOfAssetsService.findById(7), amount7));
        
        reportsService.saveReport(report);
        Assert.assertNotNull(report.getId());
        
        StockUsageReports usedReport = reportsService.findById(report.getId());
        Assert.assertNotNull(usedReport);
        
        Assert.assertEquals(amount2, (int) usedReport.getAssets().stream().filter(rt -> rt.getTypeOfAssets().getId() == 2).findFirst().get().getAmount());
        Assert.assertEquals(amount7, (int) usedReport.getAssets().stream().filter(rt -> rt.getTypeOfAssets().getId() == 7).findFirst().get().getAmount());
        
        Assert.assertEquals(acv2.getQuantity() - amount2, (int) acvService.findByComplexId(2, galwayScheme.getName()).getQuantity());
        Assert.assertEquals(acv7.getQuantity() - amount7, (int) acvService.findByComplexId(7, galwayScheme.getName()).getQuantity());
        
        Assert.assertEquals("--", usedReport.getNotes());
        
        reportsService.deleteReport(usedReport.getId());
    }
}
