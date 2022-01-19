package com.beskyd.ms_control.business.repairreports.entity;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(ReportUsedAssets.ComplexId.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportUsedAssets implements JsonAware{

    @Id
    @Column(name = "report_operator_id")
    private Integer repairOperatorId;
    
    @Id
    @Column(name = "product_type_id")
    private Integer productTypeId;
    
    @MapsId("repairOperatorId")
    @ManyToOne
    @JoinColumn(name = "report_operator_id")
    @JsonBackReference
    private RepairReportsOperators operator;
    
    @MapsId("productTypeId")
    @ManyToOne
    @JoinColumn(name = "product_type_id")
    private TypeOfAssets productType;

    private int amount;

    public ReportUsedAssets(RepairReportsOperators operator, TypeOfAssets productType, int amount) {
        this.repairOperatorId = operator.getId();
        this.productTypeId = productType.getId();
        this.operator = operator;
        this.productType = productType;
        this.amount = amount;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class ComplexId implements Serializable{
        
        @Column(name = "report_operator_id")
        private Integer repairOperatorId;
        
        @Column(name = "product_type_id")
        private Integer productTypeId;

        @Override
        public int hashCode() {
            return Objects.hash(productTypeId, repairOperatorId);
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
            return Objects.equals(productTypeId, other.productTypeId) && Objects.equals(repairOperatorId, other.repairOperatorId);
        }

        @Override
        public String toString() {
            return "ComplexId [repairOperatorId=" + repairOperatorId + ", productTypeId=" + productTypeId + "]";
        }
    }

    /**
     * Also sets {@code repairOperatorId}
     * @param operator
     */
    public void setOperator(RepairReportsOperators operator) {
        this.operator = operator;
        if(operator != null) {
            repairOperatorId = operator.getId();
        }
    }

    /**
     * Also sets {@code productTypeId}
     * @param productType
     */
    public void setProductType(TypeOfAssets productType) {
        this.productType = productType;
        if(productType != null) {
            this.productTypeId = productType.getId();
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(amount, operator, productType, productTypeId, repairOperatorId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReportUsedAssets other = (ReportUsedAssets) obj;
        return amount == other.amount && Objects.equals(operator, other.operator) && Objects.equals(productType, other.productType) && Objects.equals(productTypeId,
                other.productTypeId) && Objects.equals(repairOperatorId, other.repairOperatorId);
    }

    @Override
    public String toString() {
        return "ReportUsedAssets [repairOperatorId=" + repairOperatorId + ", productTypeId=" + productTypeId + ", operator=" + operator + ", productType=" + productType
                + ", amount=" + amount + "]";
    }

    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("repairOperatorId", repairOperatorId);
        jo.put("productType", productType.toJSONObject());
        jo.put("amount", amount);
        return jo;
    }
}
