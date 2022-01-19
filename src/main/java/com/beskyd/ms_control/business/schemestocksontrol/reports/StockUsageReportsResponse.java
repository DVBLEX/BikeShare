package com.beskyd.ms_control.business.schemestocksontrol.reports;

import com.beskyd.ms_control.business.general.SchemeResponseItem;
import com.beskyd.ms_control.business.general.StatesResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class StockUsageReportsResponse {

    private Integer id;
    
    private SchemeResponseItem scheme;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp creationDate;
    
    private StatesResponse state;

    @EqualsAndHashCode.Exclude
    private String notes;

    @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties({"report"})
    @ToString.Exclude
    private Set<StockUsageReportAssetsResponse> assets;


    public StockUsageReportsResponse(StockUsageReports original, boolean ignoreAssets) {
        this.id = original.getId();
        this.scheme = new SchemeResponseItem(original.getScheme());
        this.creationDate = original.getCreationDate();
        this.state = new StatesResponse(original.getState());
        this.notes = original.getNotes();
        if(!ignoreAssets) {
          this.assets = StockUsageReportAssetsResponse.createSetFrom(original.getAssets());
        }
    }
    
    public StockUsageReportsResponse(StockUsageReports original) {
        this(original, false);
    }
    
    public static List<StockUsageReportsResponse> createListFrom(List<StockUsageReports> originals){
        List<StockUsageReportsResponse> list = new ArrayList<>();
        
        for(StockUsageReports report : originals) {
            list.add(new StockUsageReportsResponse(report, false));
        }
        
        return list;
    }
    
    public StockUsageReports toOriginal() {
        return new StockUsageReports(id, scheme.toOriginal(), creationDate, state.toOriginal(), notes, StockUsageReportAssetsResponse.toOriginals(assets));
    }
}
