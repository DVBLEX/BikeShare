package com.beskyd.ms_control.business.controller;

import com.beskyd.ms_control.business.assetsprofiles.Product;
import com.beskyd.ms_control.business.assetsprofiles.ProductService;
import com.beskyd.ms_control.business.audit.OperationsLoggingService;
import com.beskyd.ms_control.business.distributions.Distribution;
import com.beskyd.ms_control.business.distributions.DistributionAssets;
import com.beskyd.ms_control.business.distributions.DistributionResponse;
import com.beskyd.ms_control.business.distributions.DistributionService;
import com.beskyd.ms_control.business.general.*;
import com.beskyd.ms_control.business.purchaseorders.*;
import com.beskyd.ms_control.business.schemestocksontrol.centraldepot.CentralDepot;
import com.beskyd.ms_control.business.schemestocksontrol.centraldepot.CentralDepotManagerService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsTransferQueueResponse;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsTransferQueueService;
import com.beskyd.ms_control.business.stockrequests.StockRequestResponse;
import com.beskyd.ms_control.business.stockrequests.StockRequestService;
import com.beskyd.ms_control.business.suppliers.Supplier;
import com.beskyd.ms_control.business.suppliers.SupplierService;
import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserService;
import com.itextpdf.text.DocumentException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/msc-api/distribution")
public class DistributionController {
    private final StockRequestService stockRequestService;
    private final SupplierService supplierService;
    private final PurchaseOrderProductsService purchaseOrderProductsService;
    private final UserService userService;
    private final PurchaseOrderService purchaseOrderService;
    private final SplitPurchaseOrderQueueService splitOrderQueueService;
    private final StatesRepository statesRepo;
    private final OperationsLoggingService logService;
    private final DistributionService distributionService;
    private final AssetsCurrentValuesService acvService;
    private final AssetsTransferQueueService atqService;
    private final CentralDepotManagerService centralDepotService;
    private final ProductService productService;
    private final MailService mailService;
    private final EmailBuilderService emailBuilderService;
    private final PdfBuilderService pdfBuilderService;
    
    @Inject
    public DistributionController(SupplierService supplierService, StockRequestService stockRequestService, PurchaseOrderProductsService purchaseOrderProductsService, UserService userService,
        PurchaseOrderService purchaseOrderService, SplitPurchaseOrderQueueService splitOrderQueueService, StatesRepository statesRepo, OperationsLoggingService logService,
        DistributionService distributionService, AssetsCurrentValuesService acvService, AssetsTransferQueueService atqService, CentralDepotManagerService centralDepotService,
        ProductService productService, MailService mailService, SystemParametersService systemParamsService, EmailBuilderService emailBuilderService, PdfBuilderService pdfBuilderService) {
        this.supplierService = supplierService;
        this.stockRequestService = stockRequestService;
        this.purchaseOrderProductsService = purchaseOrderProductsService;
        this.userService = userService;
        this.purchaseOrderService = purchaseOrderService;
        this.splitOrderQueueService = splitOrderQueueService;
        this.statesRepo = statesRepo;
        this.logService = logService;
        this.distributionService = distributionService;
        this.acvService = acvService;
        this.atqService = atqService;
        this.centralDepotService = centralDepotService;
        this.productService = productService;
        this.mailService = mailService;
        this.pdfBuilderService = pdfBuilderService;
        this.emailBuilderService = emailBuilderService;
    }
    
    
    @GetMapping("/purchase-orders")
    public List<PurchaseOrderResponse> getAllPurchaseOrders() throws PurchaseOrderException{
        return PurchaseOrderResponse.createListFrom(purchaseOrderService.findAll());
    }
    
    @PutMapping("/purchase-orders/{orderId}/change-state/{stateId}")
    public PurchaseOrderResponse changeOrderState(@PathVariable("orderId") Integer orderId, @PathVariable("stateId") Integer stateId) throws PurchaseOrderException {
        PurchaseOrder order = purchaseOrderService.findById(orderId);
        States state = statesRepo.findById(stateId).orElseThrow();
        order.setState(state);
        order.setStateChangeDate(Timestamp.valueOf(LocalDateTime.now()));
        purchaseOrderService.save(order);
        return new PurchaseOrderResponse(order, true);
    }
    
    @PostMapping("/purchase-orders")
    public PurchaseOrderResponse savePurchaseOrder(@RequestBody PurchaseOrderResponse savableOrder, Principal principal) throws PurchaseOrderException {
        PurchaseOrder saved = purchaseOrderService.saveNewOrder(savableOrder.toOriginalWithOrderedProducts());
        if(savableOrder.getId() == null) {
            logService.pushLog(1600, principal.getName(), saved.toJSONObject());
        }
        return new PurchaseOrderResponse(saved, false);
    }
    
    @DeleteMapping("/purchase-orders/{id}")
    public void deleteRequest(@PathVariable("id") Integer id) {
        purchaseOrderService.delete(id);
    }
    
    @PostMapping("/add-product-to-order")
    public void addProductToOrder(@RequestBody PurchaseOrderProductsResponse productForOrder) throws ProductAlreadyExistsInOrder {
        purchaseOrderProductsService.addProductToOrder(productForOrder.toOriginal());
    }
    
    @PostMapping("/ordered-product")
    public void saveProductToOrder(@RequestBody PurchaseOrderProductsResponse productToSave, Principal principal) {
        PurchaseOrderProducts oldProd = purchaseOrderProductsService.findByOrderAndProduct(productToSave.getProduct().getId(), productToSave.getPurchaseOrder().getId()); 
        purchaseOrderProductsService.save(productToSave.toOriginal());
        
        logService.pushLog(1602, principal.getName(), new JSONObject().put("purchaseOrderId", productToSave.getPurchaseOrder().getId())
                                                                      .put("productName", productToSave.getProduct().getType().getTypeName() + " " + productToSave.getProduct().getProductId().getProductName())
                                                                      .put("oldAmount", oldProd.getAmount())
                                                                      .put("newAmount", productToSave.getAmount()));
    }
    
    @DeleteMapping("/ordered-pruduct/{productId}/{orderId}")
    public void removeProductFromOrder(@PathVariable("productId") @NotNull Integer productId, @PathVariable("orderId") @NotNull Integer orderId, Principal principal) {
        purchaseOrderProductsService.removeByProductIdAndOrderId(productId, orderId);
        Product prod = productService.findById(productId);
        
        logService.pushLog(1603, principal.getName(), new JSONObject().put("purchaseOrderId", orderId).put("productName", prod.getType().getTypeName() + " " + prod.getProductId().getProductName()));
    }
    
    @GetMapping("/split-purchase-order-queue")
    public List<SplitPurchaseOrderQueueResponse> getAllSplitPurchaseOrdersInQueue(){
        return SplitPurchaseOrderQueueResponse.createListFrom(splitOrderQueueService.findAll());
    }
    
    @PostMapping("/split-purchase-order-queue")
    public Integer saveSplitPurchaseOrderQueueRecord(@RequestBody SplitPurchaseOrderQueueResponse savable, Principal principal) {
        logService.pushLog(1604, principal.getName(), new JSONObject().put("oldOrderId", savable.getOldOrderId())
                                                                      .put("productName", savable.getProduct().getType().getTypeName() + " " + savable.getProduct().getProductId().getProductName())
                                                                      .put("quantity", savable.getQuantity()));
        return splitOrderQueueService.save(savable.toOriginal());
    }
    
    @DeleteMapping("/split-purchase-order-queue/{id}")
    public SplitPurchaseOrderQueueResponse deleteOneRecordFromSplitPurchaseOrderQueue(@PathVariable("id") Integer id, Principal principal) {
        SplitPurchaseOrderQueueResponse resp = new SplitPurchaseOrderQueueResponse(splitOrderQueueService.findById(id));
        splitOrderQueueService.delete(id);
        
        logService.pushLog(1605, principal.getName(), new JSONObject().put("oldOrderId", resp.getOldOrderId()));
          
        return resp;
    }
    
    @DeleteMapping("/split-purchase-order-queue/all")
    public void clearSplitPurchaseOrderQueue(Principal principal) {
        User user = userService.findOne(principal.getName());
        if(!user.getUserRole().equals("0,0,0,0,1")) {//is user not only a fulfillment operator
            splitOrderQueueService.deleteAll();
        } else {
            splitOrderQueueService.deleteAllFulfillment();
        }
        
        
        
        logService.pushLog(1606, user.getUserEmail(), "Records cleared");
    }
    
    @PutMapping("/split-purchase-order-queue-to-orders")
    public List<PurchaseOrderResponse> convertSplitPurchaseOrderIntoRequests(Principal principal) throws PurchaseOrderException{
        List<SplitPurchaseOrderQueue> queue;
        
        User user = userService.findOne(principal.getName());
        if(!user.getUserRole().equals("0,0,0,0,1")) {//is user not only a fulfillment operator
            queue = splitOrderQueueService.findAll();
        } else {
            queue = splitOrderQueueService.findFulfillmentQueue();
        }
        
        List<PurchaseOrder> orders = purchaseOrderService.generatePurchaseOrdersFromSplitQueue(queue);
        
        JSONObject logBody = new JSONObject();
        
        for (int i = 0; i < orders.size(); i++) {
            purchaseOrderService.save(orders.get(i));
            logBody.put((i + 1) + "_newOrderId", orders.get(i));
        }
        splitOrderQueueService.deleteAll();
        
        logService.pushLog(1607, user.getUserEmail(), logBody);
        
        return PurchaseOrderResponse.createListFrom(orders);
    }

    @PutMapping("/send-order/{supplierId}")
    public void sendEmailOrderToSupplier(@PathVariable("supplierId") Integer supplierId, @RequestBody PurchaseOrderResponse order, Principal principal)
            throws DocumentException, IOException {
        User user = userService.findOne(principal.getName());
        
        Supplier supplier = supplierService.findById(supplierId);
        
        for(PurchaseOrderProductsResponse p : order.getOrderedProducts()) {
            p.setPurchaseOrder(order);
            p.setConfirmed(p.getAmount());
        }
        purchaseOrderProductsService.saveAll(PurchaseOrderProductsResponse.toOriginals(order.getOrderedProducts()));
        
        logService.pushLog(1601,principal.getName(), new JSONObject().put("supplierName", supplier.getName()).put("purchaseOrderId", order.getId()));
        
        String emailHtml = emailBuilderService.constructPurchaseOrderHTMLWithComment(user, order);

        ByteArrayOutputStream out = pdfBuilderService.parse(emailHtml);
        mailService.sendMessageWithAttachment(supplier.getEmail(), "Purchase order: No." + order.getId(), emailHtml, "Order " + order.getId() + ".pdf", out, "application/pdf");
    }
    
    @GetMapping(value = "/get-order-pdf/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void getPurchaseOrderPDF(@PathVariable("orderId") Integer orderId, HttpServletResponse response, Principal principal) throws DocumentException, PurchaseOrderException, IOException {
        User user = userService.findOne(principal.getName());
        PurchaseOrder order = purchaseOrderService.findById(orderId);
        String emailHtml = emailBuilderService.constructPurchaseOrderHTML(user, new PurchaseOrderResponse(order, false));
        pdfBuilderService.writePdf(emailHtml, response, principal);
    }

    @GetMapping(value = "/get-distribution-pdf/{distributionId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public void getDistributionOrderPDF(@PathVariable("distributionId") Integer distributionId, HttpServletResponse response, Principal principal) throws IOException, DocumentException {
        Distribution dist = distributionService.findById(distributionId);
        String emailHtml = emailBuilderService.constructDistributionOrderHTML(dist);
        pdfBuilderService.writePdf(emailHtml, response, principal);
    }
    
    @PutMapping("/replenish")
    public PurchaseOrderResponse closePurchaseOrder(@RequestBody PurchaseOrderResponse savableOrder, Principal principal) throws PurchaseOrderException {
        PurchaseOrder previous = purchaseOrderService.findById(savableOrder.getId());
        PurchaseOrder original = savableOrder.toOriginalWithOrderedProducts();

        
        purchaseOrderService.setOrderAsFulfilled(original);
        
        for(PurchaseOrderProducts pop : original.getOrderedProducts()) {
                       
            
            if(pop.getConfirmed() < pop.getAmount() && original.getState().getId() != PurchaseOrderService.PARTIALLY_FULFILLED_ORDER) {
                //newPO.getOrderedProducts().add(new PurchaseOrderProducts(newPO, pop.getProduct(), pop.getAmount() - pop.getConfirmed(), null));
                
                purchaseOrderService.setOrderAsPartiallyFulfilled(original);
            }
            
            CentralDepot cdType = centralDepotService.findByProductTypeId(pop.getProduct().getType().getId());
            if(cdType == null) {
                cdType = centralDepotService.save(new CentralDepot(pop.getProduct().getType(), 0));
                cdType.setAmount(pop.getAmount());
            }
            
            PurchaseOrderProducts prevProd = previous.getOrderedProducts().stream().filter(p -> p.getIdProduct().equals(pop.getIdProduct())).findFirst().orElseThrow();
            
            cdType.setAmount(cdType.getAmount() + (pop.getConfirmed() - prevProd.getConfirmed()));
            centralDepotService.save(cdType);
        }
        
        purchaseOrderService.save(original);
        
        return new PurchaseOrderResponse(original, false);
        
//        if(newPO.getOrderedProducts().size() > 0) { //for now we have left the idea if split new order
//            purchaseOrderService.save(newPO);
//            
//            logService.pushLog(1608, principal.getName(), new JSONObject().put("closedOrderId", savableOrder.getId()).put("newOrderId", newPO.getId()));
//            
//            return new PurchaseOrderResponse(newPO, false);
//        } else {
//            logService.pushLog(1608, principal.getName(), new JSONObject().put("closedOrderId", savableOrder.getId()));
//            
//            return null;
//        }
    }
    
    @GetMapping("/all")
    public List<DistributionResponse> getAllDistributions(){
        return DistributionResponse.createListFrom(distributionService.findAll());
    }
    
    @PostMapping("/create/from-stock-request")
    public DistributionResponse createDistributionFromStockRequest(@RequestBody StockRequestResponse stockRequest, Principal principal) {
        DistributionResponse resp = new DistributionResponse(distributionService.createFromStockRequest(stockRequest.toOriginal(), stockRequest.getDistributionNotes()), false);
        
        logService.pushLog(1503, principal.getName(), new JSONObject().put("stockRequestId", stockRequest.getId()).put("distributionId", resp.getId()));
        return resp;
    }
    
    @PostMapping("/create/from-transfer-queue")
    public List<DistributionResponse> createDistributionsFromTransferQueue(@RequestBody List<AssetsTransferQueueResponse> transferQueue, Principal principal){
        List<DistributionResponse> distList = DistributionResponse.createListFrom(distributionService.createFromTransferQueue(AssetsTransferQueueResponse.toOriginals(transferQueue)));
        
        String schemeName = transferQueue.get(0).getTransferToScheme().getName();
        atqService.deleteByTransferToScheme(schemeName);
        
        logService.pushLog(1305, principal.getName(), schemeName);
        return distList;
    }//TODO така ситуація. В черзі висить товар з певною кількістю. В той час, поки він висів, кількість на складі звідки везти зменшилася і при створенні дистрибуції виникне або помилка, або буде від'ємне значення кількості
    //TODO можливе вирішення, щоб уникнути ще додаткових перевірок і сплітів, це оновляти значення звідки везти не при створенні дистрибуції, а при закритті і закривати її можна буде тільки тоді, коли буде вистачати товарів на складі
    
    @PutMapping("/{distId}/change-state/{stateId}")
    public StatesResponse changeDistributionState(@PathVariable("distId") Integer distId, @PathVariable("stateId") Integer stateId, Principal principal) {
        Distribution dist = distributionService.findById(distId);
        dist.setState(statesRepo.findById(stateId).orElseThrow());
        dist.setStateChangeDate(Timestamp.valueOf(LocalDateTime.now()));
        
        distributionService.save(dist);
        
        if(stateId.equals(DistributionService.SHIPPED_DISTRIBUTION)) {
            logService.pushLog(1700, principal.getName(), new JSONObject().put("distributionId", distId));
        }
        
        StatesResponse sr = new StatesResponse(dist.getState());
        sr.setStateChangeDate(dist.getStateChangeDate());
        
        return sr;
    }
    
    @PutMapping("/{distId}/close-distribution")
    public StatesResponse changeDistributionState(@PathVariable("distId") Integer distId, Principal principal) {
        Distribution dist = distributionService.findById(distId);
        States state = statesRepo.findById(DistributionService.CLOSED_DISTRIBUTION).orElseThrow();
        dist.setState(state);
        dist.setStateChangeDate(Timestamp.valueOf(LocalDateTime.now()));
        distributionService.save(dist);
        if(dist.getStockRequest() != null) {
            stockRequestService.changeStateAndSave(dist.getStockRequest(), StockRequestService.FULFILLED_REQUEST);
        }
        for(DistributionAssets da : dist.getAssets()) {
            AssetsCurrentValues acv =  acvService.findByComplexId(da.getTypeOfAssets().getId(), da.getDistribution().getSchemeTo().getName());
            if(acv != null) {
                acv.setQuantity(acv.getQuantity() + da.getQuantity());
            } else {
                acv = new AssetsCurrentValues(da.getTypeOfAssets(), da.getDistribution().getSchemeTo(), da.getQuantity());
            }
            acvService.save(acv);
        }
        
        StatesResponse sr = new StatesResponse(state);
        sr.setStateChangeDate(dist.getStateChangeDate());
        
        logService.pushLog(1701, principal.getName(), new JSONObject().put("distributionId", distId));
        
        return sr;
    }
    
    @GetMapping("/central-depot-scheme")
    public SchemeResponseItem getCentralDepotScheme() {
        return new SchemeResponseItem("Cork");
    }
    
}
