package com.beskyd.ms_control.business.schemestocksontrol.reports;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsResponse;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class StockUsageReportAssetsResponse {
    
    private StockUsageReportsResponse report;
    
    private TypeOfAssetsResponse typeOfAssets;

    private Integer amount;


    public StockUsageReportAssetsResponse(StockUsageReportAssets original) {
        if(original.getReport() != null) {
            this.report = new StockUsageReportsResponse(original.getReport(), true);
        }
        this.typeOfAssets = new TypeOfAssetsResponse(original.getTypeOfAssets(), true);
        this.amount = original.getAmount();
    }
    
    public static Set<StockUsageReportAssetsResponse> createSetFrom(Set<StockUsageReportAssets> originals){
        Set<StockUsageReportAssetsResponse> set = new HashSet<>();
        
        for(StockUsageReportAssets or : originals) {
            set.add(new StockUsageReportAssetsResponse(or));
        }
        
        return set;
    }
    
    public StockUsageReportAssets toOriginal() {
        return new StockUsageReportAssets(report != null ? report.toOriginal() : null, typeOfAssets.toOriginal(), amount);
    }
    
    public static Set<StockUsageReportAssets> toOriginals(Set<StockUsageReportAssetsResponse> responses){
        Set<StockUsageReportAssets> set = new HashSet<>();
        
        for(StockUsageReportAssetsResponse r : responses) {
            set.add(r.toOriginal());
        }
        
        return set;
    }
}
