package com.beskyd.ms_control.repairreporttests;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.repairreports.RepairReportsService;
import com.beskyd.ms_control.business.repairreports.entity.*;
import com.beskyd.ms_control.business.repairreports.repo.BikeStationsRepository;
import com.beskyd.ms_control.business.repairreports.repo.BikesRepository;
import com.beskyd.ms_control.business.repairreports.repo.RepairReasonsRepository;
import com.beskyd.ms_control.business.repairreports.repo.ReportReasonsRepository;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class RepairReportsTests {

    @Autowired
    private RepairReportsService repairReportsService;
    
    @Autowired
    private BikeStationsRepository bikeStationsRepository;
    
    @Autowired
    private BikesRepository bikesRepository;
    
    @Autowired
    private ReportReasonsRepository reportReasonsRepository;
    
    @Autowired
    private RepairReasonsRepository repairReasonsRepository;
    
    @Autowired
    private StatesRepository statesRepository;
    
    @Test
    public void saveTest() {
        BikeStations location = bikeStationsRepository.findById(2).get();
        Bikes bike = bikesRepository.findById(2).get();
        ReportReasons reportReason = reportReasonsRepository.findById(1).get();
        RepairReasons repairReason = repairReasonsRepository.findById(3).get();
        
        RepairReports report = RepairReports.builder()
                .location(location)
                .bike(bike)
                .reportReason(reportReason)
                .repairReason(Set.of(repairReason))
                .build();

        repairReportsService.adjustAndSave(report);
        
        Assert.assertNotNull(report.getId());
        Assert.assertEquals(RepairReportsService.STATE_NEW, report.getState().getId());
        Assert.assertNotEquals(0, repairReportsService.findByScheme("Cork").size());
    }

    @Test
    public void adjustStateAndIdTest() {
        RepairReports newReport = new RepairReports();

        Assert.assertEquals(RepairReportsService.STATE_NEW, repairReportsService.adjustStateAndId(newReport).getState().getId());

        RepairReports pendingReport = new RepairReports();
        pendingReport.setId(-2);
        pendingReport = repairReportsService.adjustStateAndId(pendingReport);
        Assert.assertNull(pendingReport.getId());
        Assert.assertEquals(RepairReportsService.STATE_PENDING, pendingReport.getState().getId());

        RepairReports pendingToNew = new RepairReports();
        pendingToNew.setId(1);
        pendingToNew.setState(pendingReport.getState());
        Assert.assertEquals(RepairReportsService.STATE_NEW, repairReportsService.adjustStateAndId(pendingToNew).getState().getId());

        pendingToNew.setOperators(Set.of(new RepairReportsOperators()));
        Assert.assertEquals(RepairReportsService.STATE_IN_PROGRESS, repairReportsService.adjustStateAndId(pendingToNew).getState().getId());

    }
}
