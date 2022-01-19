package com.beskyd.ms_control.business.reports.assets;

import com.beskyd.ms_control.business.purchaseorders.PurchaseOrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AssetsReportRepository extends JpaRepository<PurchaseOrderProducts, PurchaseOrderProducts.ComplexId> {
    @Query(value = "select toa.groupName AS name, sum (pop.confirmed) AS acquired, SUM(pop.amount) AS ordered " +
            " from PurchaseOrderProducts pop" +
            " inner join Product p ON p.id = pop.idProduct" +
            " inner join PurchaseOrder po ON po.id = pop.idPurchaseOrder" +
            " inner join TypeOfAssets toa on toa.id = p.type.id" +
            " where po.stateChangeDate between :start_date and :end_date group by toa.id")
    List<AssetsReport> findByRange(@Param("start_date") Timestamp startDate,
                                   @Param("end_date") Timestamp endDate);
}
