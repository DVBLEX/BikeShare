package com.beskyd.ms_control.business.repairreports.response;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsResponse;
import com.beskyd.ms_control.business.repairreports.entity.RepairReportsOperators;
import com.beskyd.ms_control.business.repairreports.entity.ReportUsedAssets;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ReportUsedAssetsResponse {

    private Integer repairOperatorId;
    
    private TypeOfAssetsResponse productType;

    private int amount;

    public ReportUsedAssetsResponse(ReportUsedAssets original) {
        this.repairOperatorId = original.getOperator().getId();
        this.productType = new TypeOfAssetsResponse(original.getProductType(), true);
        this.amount = original.getAmount();
    }

    public static Set<ReportUsedAssetsResponse> createSetFrom(Set<ReportUsedAssets> originals){
        Set<ReportUsedAssetsResponse> set = new HashSet<>();
        
        for(ReportUsedAssets or : originals) {
            set.add(new ReportUsedAssetsResponse(or));
        }
        
        return set;
    }
    
    public ReportUsedAssets toOriginal(RepairReportsOperators operator) {
        return new ReportUsedAssets(operator, productType.toOriginal(), amount);
    }
    
    public static Set<ReportUsedAssets> toOriginals(Set<ReportUsedAssetsResponse> responses, RepairReportsOperators operator){
        Set<ReportUsedAssets> originals = new HashSet<>();
        
        for(ReportUsedAssetsResponse response : responses) {
            originals.add(response.toOriginal(operator));
        }
        
        return originals;
    }

}
