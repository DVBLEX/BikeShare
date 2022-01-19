package com.beskyd.ms_control.business.schemestocksontrol.reports;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.*;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
@IdClass(StockUsageReportAssets.ComplexId.class)
@ToString
@EqualsAndHashCode
public class StockUsageReportAssets implements JsonAware{

    @Id
    @Column(name = "report_id")
    @ToString.Exclude
    private Integer reportId;
    
    @Id
    @Column(name = "type_of_asset_id")
    @ToString.Exclude
    private Integer typeOfAssetId;
    
    @MapsId
    @ManyToOne
    @JoinColumn(name = "report_id")
    @EqualsAndHashCode.Exclude
    private StockUsageReports report;
    
    @MapsId
    @ManyToOne
    @JoinColumn(name = "type_of_asset_id")
    @EqualsAndHashCode.Exclude
    private TypeOfAssets typeOfAssets;

    private Integer amount;

    
    public StockUsageReportAssets(StockUsageReports report, TypeOfAssets typeOfAssets, Integer amount) {
        this.report = report;
        if(report != null) {
            reportId = report.getId();
        }
        this.typeOfAssets = typeOfAssets;
        if(typeOfAssets != null) {
            this.typeOfAssetId = typeOfAssets.getId();
        }
        this.amount = amount;
    }
    
    public static class ComplexId implements Serializable{
        @Column(name = "report_id")
        private Integer reportId;
        
        @Column(name = "type_of_asset_id")
        private Integer typeOfAssetId;

        public ComplexId() {
        }

        public ComplexId(Integer reportId, Integer typeOfAssetId) {
            this.reportId = reportId;
            this.typeOfAssetId = typeOfAssetId;
        }

        public Integer getReportId() {
            return reportId;
        }

        
        public void setReportId(Integer reportId) {
            this.reportId = reportId;
        }

        
        public Integer getTypeOfAssetId() {
            return typeOfAssetId;
        }

        
        public void setTypeOfAssetId(Integer typeOfAssetId) {
            this.typeOfAssetId = typeOfAssetId;
        }


        @Override
        public int hashCode() {
            return Objects.hash(reportId, typeOfAssetId);
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ComplexId other = (ComplexId) obj;
            return Objects.equals(reportId, other.reportId) && Objects.equals(typeOfAssetId, other.typeOfAssetId);
        }


        @Override
        public String toString() {
            return "ComplexId [reportId=" + reportId + ", typeOfAssetId=" + typeOfAssetId + "]";
        }
        
    }

    /**
     * Also sets {@code reportId}
     * @param report
     */
    public void setReport(StockUsageReports report) {
        if(report != null) {
            reportId = report.getId();
        }
        this.report = report;
    }

    /**
     * Also sets {@code typeOfAssetId}
     * @param typeOfAssets
     */
    public void setTypeOfAssets(TypeOfAssets typeOfAssets) {
        if(typeOfAssets != null) {
            typeOfAssetId = typeOfAssets.getId();
        }
        this.typeOfAssets = typeOfAssets;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("reportId", reportId);
        jo.put("typeOfAssetName", typeOfAssets.getTypeName());
        jo.put("amount", amount);
        return jo;
    }
}
