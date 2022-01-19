package com.beskyd.ms_control.business.stockrequests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RequestProductsTypesListRepository extends JpaRepository<RequestProductsTypesList, RequestProductsTypesList.ComplexId> {
    @Query("select rpt from RequestProductsTypesList rpt" +
            " inner join StockRequest sr on sr.id = rpt.idRequest" +
            " where sr.creationDate between :start_date and :end_date")
    List<RequestProductsTypesList> findByRange(@Param("start_date") Timestamp startDate,
                                               @Param("end_date") Timestamp endDate);
}
