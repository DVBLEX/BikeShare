package com.beskyd.ms_control.business.stockrequests;

import com.beskyd.ms_control.business.audit.OperationsLoggingService;
import com.beskyd.ms_control.business.distributions.Distribution;
import com.beskyd.ms_control.business.distributions.DistributionResponse;
import com.beskyd.ms_control.business.distributions.DistributionService;
import com.beskyd.ms_control.business.general.TimestampResponse;
import com.beskyd.ms_control.business.purchaseorders.PurchaseOrder;
import com.beskyd.ms_control.business.purchaseorders.PurchaseOrderService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/msc-api/distribution/stock-requests")
public class StockRequestsController {

    private final StockRequestService stockRequestService;
    private final DistributionService distributionService;
    private final PurchaseOrderService purchaseOrderService;
    private final OperationsLoggingService logService;


    public StockRequestsController(StockRequestService stockRequestService, DistributionService distributionService, PurchaseOrderService purchaseOrderService,
                                   OperationsLoggingService logService) {
        this.stockRequestService = stockRequestService;
        this.distributionService = distributionService;
        this.purchaseOrderService = purchaseOrderService;
        this.logService = logService;
    }


    @GetMapping()
    public List<StockRequestResponse> findAllStockRequests() {
        return StockRequestResponse.createListFrom(stockRequestService.findAll());
    }


    @GetMapping("/scheme-sorted")
    public Map<String, List<StockRequestResponse>> findAllStockRequestsSortedByScheme() {
        List<StockRequestResponse> list = StockRequestResponse.createListFrom(stockRequestService.findAll());
        List<Distribution> distributions = distributionService.findForStockRequests();

        if (!distributions.isEmpty()) {
            for (StockRequestResponse sResp : list) {
                for (Distribution dist : distributions) {
                    if (dist.getStockRequest().getId().equals(sResp.getId())) {
                        sResp.setDistribution(new DistributionResponse(dist, false));
                    }
                }
            }
        }

        return list.stream().collect(Collectors.groupingBy(e -> e.getScheme().getName()));
    }

    @PostMapping()
    public List<StockRequestResponse> saveStockRequest(@RequestBody StockRequestResponse request, Principal principal) {
        List<StockRequest> saved = stockRequestService.save(request.toOriginal());

        if (Boolean.TRUE.equals(request.getManual())) {
            for (var sr : saved) {
                logService.pushLog(1504, principal.getName(), sr.toJSONObject());
            }
        }

        return StockRequestResponse.createListFrom(saved);
    }

    @PostMapping("/merge-requests")
    public TimestampResponse mergeRequests(@RequestBody List<StockRequestResponse> requests, Principal principal) {
        List<StockRequest> originalRequests = new ArrayList<>();
        for (StockRequestResponse rr : requests) {
            originalRequests.add(stockRequestService.findById(rr.getId()));
        }


        List<PurchaseOrder> orders = purchaseOrderService.generatePurchaseOrders(originalRequests);

        Timestamp stateChangeDate = null;
        for (StockRequest r : originalRequests) {
            stateChangeDate = stockRequestService.changeStateAndSave(r, 2);
        }

        JSONObject dataObject = new JSONObject();

        JSONArray newOrdersIds = new JSONArray();
        for (PurchaseOrder order : orders) {
            purchaseOrderService.save(order);
            newOrdersIds.put(order.getId());
        }
        dataObject.put("OrdersIds", newOrdersIds);

        if (stockRequestService.findByStateId(1).size() == originalRequests.size()) {
            JSONArray ids = new JSONArray();
            for (StockRequest r : originalRequests) {
                ids.put(r.getId());
            }
            dataObject.put("stockRequestsIds", ids);

            logService.pushLog(1500, principal.getName(), dataObject);
        } else {
            logService.pushLog(1501, principal.getName(),
                    dataObject.put("stockRequestId",
                            originalRequests.isEmpty() ? null : originalRequests.get(0).getId().toString()));//this is not very elegant, but if we are not merging all requests, we can merge only one, so here we are just doing get(0)
        }

        return new TimestampResponse(stateChangeDate);
    }

}
