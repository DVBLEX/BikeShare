package com.beskyd.ms_control.business.schemestocksontrol.reports;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;

@Service
public class StockUsageReportsService {
    
    public static final Integer STATE_USED = 31;
    public static final Integer STATE_PLACED = 32;
    
    private StockUsageReportsRepository reportsRepo;
    private StockUsageReportAssetsRepository reportAssetsRepository;
    private AssetsCurrentValuesService acvService;
    
    @Inject
    public StockUsageReportsService(StockUsageReportsRepository reportsRepo, StockUsageReportAssetsRepository reportAssetsRepository, AssetsCurrentValuesService acvService) {
        this.reportsRepo = reportsRepo;
        this.reportAssetsRepository = reportAssetsRepository;
        this.acvService = acvService;
    }
    
    /**
     * Finds all {@link StockUsageReports} 
     * @return
     */
    public List<StockUsageReports> findAll(){
        return reportsRepo.findAll(Sort.by("id").descending());
    }
    
    /**
     * FInds all {@link StockUsageReports} by {@link Scheme}'s name
     * @param name
     * @return
     */
    public List<StockUsageReports> findBySchemeName(String name){
        return reportsRepo.findByScheme_Name(name, Sort.by("id").descending());
    }
    
    /**
     * Finds {@link StockUsageReports} by id
     * @param id of report
     * @return object of {@link StockUsageReports}, if found, otherwise - {@code null}
     */
    public StockUsageReports findById(int id) {
        return reportsRepo.findById(id).orElse(null);
    }
    
    /**
     * Saves {@link StockUsageReports}. 
     * If {@code id} is {@code null}, sets {@code creationDate}
     * Updates {@link AssetsCurrentValues} for request's scheme
     * If one of the placed assets isn't in scheme's stock, then we create and save new {@link AssetsCurrentValues} for it
     * @param savable
     * @return
     */
    public StockUsageReports saveReport(StockUsageReports savable) {
        if(savable.getId() == null) {
            savable.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        
        if(savable.getState().getId() == STATE_USED) {
            for(StockUsageReportAssets asset : savable.getAssets()) {
                AssetsCurrentValues acv = acvService.findByComplexId(asset.getTypeOfAssets().getId(), savable.getScheme().getName());
                acv.setQuantity(acv.getQuantity() - asset.getAmount());
                acvService.save(acv);
            }
        } else if(savable.getState().getId() == STATE_PLACED) {
            for(StockUsageReportAssets asset : savable.getAssets()) {
                AssetsCurrentValues acv = acvService.findByComplexId(asset.getTypeOfAssets().getId(), savable.getScheme().getName());
                
                if(acv == null) {
                    acv = new AssetsCurrentValues(asset.getTypeOfAssets(), savable.getScheme(), asset.getAmount());
                } else {
                    acv.setQuantity(acv.getQuantity() + asset.getAmount());
                }
                
                acvService.save(acv);
            }
        }
        
        reportsRepo.save(savable);
        
        for(StockUsageReportAssets asset : savable.getAssets()) {
            asset.setReport(savable);
        }
        
        reportAssetsRepository.saveAll(savable.getAssets());
        
        return savable;
    }
    
    public void deleteReport(int id) {
        reportsRepo.deleteById(id);
    }
}
