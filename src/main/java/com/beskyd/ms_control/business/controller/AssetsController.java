package com.beskyd.ms_control.business.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beskyd.ms_control.business.assetsprofiles.AddingExistingProductException;
import com.beskyd.ms_control.business.assetsprofiles.Product;
import com.beskyd.ms_control.business.assetsprofiles.ProductResponse;
import com.beskyd.ms_control.business.assetsprofiles.ProductService;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsResponse;
import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssetsService;
import com.beskyd.ms_control.business.audit.OperationsLoggingService;
import com.beskyd.ms_control.business.schemestocksontrol.reports.StockUsageReports;
import com.beskyd.ms_control.business.schemestocksontrol.reports.StockUsageReportsResponse;
import com.beskyd.ms_control.business.schemestocksontrol.reports.StockUsageReportsService;
import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.SystemParametersService;
import com.beskyd.ms_control.business.requests.ACVReduceRequest;
import com.beskyd.ms_control.business.requests.ACVRequest;
import com.beskyd.ms_control.business.requests.AMVRequest;
import com.beskyd.ms_control.business.requests.ProdTypeIdAndSchemeNameRequest;
import com.beskyd.ms_control.business.requests.SaveProductRequest;
import com.beskyd.ms_control.business.requests.TransferQueueUpdateQuantityRequest;
import com.beskyd.ms_control.business.requests.TransferRequest;
import com.beskyd.ms_control.business.schemestocksontrol.centraldepot.CentralDepotManagerService;
import com.beskyd.ms_control.business.schemestocksontrol.centraldepot.CentralDepotResponse;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesResponse;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValuesResponse;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValuesService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsTransferQueue;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsTransferQueueResponse;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsTransferQueueService;
import com.beskyd.ms_control.business.schemestocksontrol.values.IdenticalTransferException;
import com.beskyd.ms_control.business.schemestocksontrol.values.RequestTransferResponse;
import com.beskyd.ms_control.business.schemestocksontrol.values.TransferToTheSameSchemeException;
import com.beskyd.ms_control.business.suppliers.Supplier;
import com.beskyd.ms_control.business.suppliers.SupplierResponse;
import com.beskyd.ms_control.business.suppliers.SupplierService;
import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserService;
import com.beskyd.ms_control.helper.QRGenerationException;
import com.beskyd.ms_control.helper.QRGenerator;
import com.beskyd.ms_control.helper.QRGeneratorImpl;

@CrossOrigin(origins = "http://localhost:4200", exposedHeaders = "Access-Control-Allow-Origin")
@RestController
@RequestMapping("/msc-api/assets")
public class AssetsController {
    public static final Logger LOGGER = LoggerFactory.getLogger(AssetsController.class);
    private final ProductService productService;
    private final SupplierService supplierService;
    private final TypeOfAssetsService typeOfAssetsService;
    private final AssetsMarginalValuesService amvService;
    private final AssetsCurrentValuesService acvService;
    private final AssetsTransferQueueService transferQueueService;
    private final UserService userService;
    private final OperationsLoggingService logService;
    private final CentralDepotManagerService centralDepotService;
    private final StockUsageReportsService stockUsageReportsService;
    private final SystemParametersService systemParamsService;
    
    @Inject
    public AssetsController(ProductService productService, SupplierService supplierService, TypeOfAssetsService typeOfAssetsService, AssetsMarginalValuesService amvService,
        AssetsCurrentValuesService acvService, AssetsTransferQueueService transferQueueService, UserService userService, OperationsLoggingService logService, CentralDepotManagerService centralDepotService,
        StockUsageReportsService stockUsageReportsService, SystemParametersService systemParamsService) {
        this.productService = productService;
        this.supplierService = supplierService;
        this.typeOfAssetsService = typeOfAssetsService;
        this.amvService = amvService;
        this.acvService = acvService;
        this.transferQueueService = transferQueueService;
        this.userService = userService;
        this.logService = logService;
        this.centralDepotService = centralDepotService;
        this.stockUsageReportsService = stockUsageReportsService;
        this.systemParamsService = systemParamsService;
    }
    
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> findAllProducts(){
        return ResponseEntity.ok().body(ProductResponse.createListFrom(productService.findAll()));
    }
    
    @GetMapping("/products/count")
    public Long getProductsCount(@RequestParam("filter") String nameFilter) {
        return  productService.getCount(nameFilter);
    }
    
    @GetMapping("/products/by-pages-and-name/{pageNum}/{pageSize}")
    public List<ProductResponse> findAllProductsByPages(@RequestParam("filter") String nameFilter, @PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize){
        return ProductResponse.createListFrom(productService.findByPagesAndName(pageNum - 1, pageSize, nameFilter));
    }


    @GetMapping("/types-of-assets")
    public List<TypeOfAssetsResponse> findAllTypesOfAssets(){
        return TypeOfAssetsResponse.createListFrom(typeOfAssetsService.findAll());
    }
    
    @GetMapping("/types-of-assets/{id}")
    public TypeOfAssetsResponse getTypeOfAssetsById(@PathVariable("id") Integer id) {
        TypeOfAssets type = typeOfAssetsService.findById(id);
        if(type != null) {
            return new TypeOfAssetsResponse(type, false);
        } else {
            return null;
        }
    }
    
    @GetMapping("/suppliers")
    public List<SupplierResponse> findAllSuppliers(){
        return SupplierResponse.createListFrom(supplierService.findAll());
    }
    
    @PostMapping("/suppliers")
    public Integer saveSupplier(@RequestBody SupplierResponse supplierResp, Principal principal) {
        Supplier supplier = supplierResp.toOriginal();
        Supplier oldSupplier = supplierService.findById(supplier.getId());
        
        if(oldSupplier == null) {
            logService.pushLog(1400, principal.getName(), supplier, null, supplier.getName(), "name");
        } else {
            logService.pushLog(1401, principal.getName(), supplier, oldSupplier, supplier.getName(), "name");
        }
        
        return supplierService.save(supplier).getId();
    }
    
    @DeleteMapping("/suppliers/{id}")
    public void deleteSupplier(@PathVariable("id") Integer id, Principal principal) {
        Supplier oldSupplier = supplierService.findById(id);
        if(oldSupplier != null) {
            logService.pushLog(1402, principal.getName(), null, oldSupplier, oldSupplier.getName(), "name");
        }
        
        supplierService.delete(id);
    }
    
    /**
     * @see TypeOfAssetsService#getAllTypeGroups()
     */
    @GetMapping("/groups-of-types")
    public List<String> getAllGroupsOfTypes(){
        return typeOfAssetsService.getAllTypeGroups();
    }
    
    @GetMapping("/products/by-suppliers")
    public Map<String, List<ProductResponse>> getProductsSortedBySuppliers(){
        List<ProductResponse> products = ProductResponse.createListFrom(productService.findAll());
        return products.stream().collect(Collectors.groupingBy(p -> p.getProductId().getSupplier().getName()));
    }
    
    /**
     * Saves new {@link Product}.
     * Saves also (before product save) {@link TypeOfAssets}, if its id is {@code null}. 
     * Don't forget to update {@code type} on client side from response body!
     * @throws AddingExistingProductException 
     * 
     * @see ProductService#save(Product, Product)
     */
    @PostMapping("/product")
    public ProductResponse saveProduct(@RequestBody SaveProductRequest saveProductRequest, Principal principal) throws AddingExistingProductException {
        final Product savableProduct = saveProductRequest.getSavableProduct();
        TypeOfAssets type = typeOfAssetsService.findByName(savableProduct.getType().getGroupName());
        if(type != null) {
            type.setGroupName(savableProduct.getType().getGroupName());
            type.setTypeName(savableProduct.getType().getTypeName());
            typeOfAssetsService.save(type);
            savableProduct.setType(type);
        } else {
            typeOfAssetsService.save(savableProduct.getType());
        }

       if(saveProductRequest.getOldProduct() == null) {
           logService.pushLog(1200, principal.getName(), savableProduct, null, savableProduct.getType().getTypeName() + " " + savableProduct.getProductId().getProductName(), "name");
        } else {
            logService.pushLog(1202, principal.getName(), savableProduct, saveProductRequest.getOldProduct(), savableProduct.getType().getTypeName() + " " + savableProduct.getProductId().getProductName(), "name");
        }
        
        return new ProductResponse(productService.save(savableProduct));
    }
    
    @DeleteMapping("/product/{id}")
    public void deleteProduct(@PathVariable("id") Integer id, Principal principal) {
        Product oldProduct = productService.findById(id);
        logService.pushLog(1201, principal.getName(), null, oldProduct, oldProduct.getType().getTypeName() + " " + oldProduct.getProductId().getProductName(), "name");
        
        productService.deleteById(id);
    }
    
    @GetMapping("/assets-margin-values")
    public List<AssetsMarginalValuesResponse> getAllAssetsMarginalValues(){
        return AssetsMarginalValuesResponse.createListFrom(amvService.findAll());
    }
    
    @PutMapping("/assets-margin-values/get-by-complex-id")
    public AssetsMarginalValuesResponse getAssetsMarginalValuesByComplexId(@RequestBody ProdTypeIdAndSchemeNameRequest request) {
        AssetsMarginalValues amv = amvService.findByComplexId(request.getProductTypeId(), request.getSchemeName());
        
        if(amv != null) {
            return new AssetsMarginalValuesResponse();
        }
        
        return null;
    }
    
    @GetMapping("/assets-current-values")
    public List<AssetsCurrentValuesResponse> getAllAssetsCurrentValues(){
        return AssetsCurrentValuesResponse.createListFrom(acvService.findAll());
    }
    
    @GetMapping("/assets-current-values/by-scheme/{schemeName}")
    public List<AssetsCurrentValuesResponse> getAssetsCurrentValuesByScheme(@PathVariable("schemeName") String schemeName){
        return AssetsCurrentValuesResponse.createListFrom(acvService.findByScheme(new Scheme(schemeName)));
    }
    
    @GetMapping("/assets-current-values/{productTypeId}")
    public List<AssetsCurrentValuesResponse> getAssetsCurrentValuesByProductTypeId(@PathVariable("productTypeId") Integer productTypeId){
        return AssetsCurrentValuesResponse.createListFrom(acvService.findByProductTypeId(productTypeId));
    }
        
    @PutMapping("/assets-current-values/reduce-values")
    public void recudeAssetsCurrentValues(@RequestBody List<ACVReduceRequest> reductRequests) {
        for(ACVReduceRequest rr : reductRequests) {
            acvService.reduceQuantity(rr.getProductTypeId(), rr.getSchemeName(), rr.getMinusQuantity());
        }
    }
    
    @GetMapping("/assets-transfer-queue")
    public List<AssetsTransferQueueResponse> getAssetsTransferQueue(){
        return AssetsTransferQueueResponse.createFromList(transferQueueService.findAll());
    }
    
    @PostMapping("/assets-margin-values")
    public void saveAssetsMarginValues(@RequestBody AMVRequest amvSavable, Principal principal) {
        AssetsMarginalValues amvOld = amvService.findByComplexId(amvSavable.getProductType(), amvSavable.getScheme());
        
        logService.pushLog(1300, principal.getName(), amvSavable.toAssetsMarginalValues(), amvOld, amvSavable.toAssetsMarginalValues().getProductType().getTypeName() + " from " + amvSavable.toAssetsMarginalValues().getScheme().getName());
              
        amvService.save(amvSavable.toAssetsMarginalValues());
    }
    
    @PostMapping("/assets-current-values")
    public void saveAssetsCurrentValues(@RequestBody ACVRequest acvSavable) {
        acvService.save(acvSavable.toAssetsCurrentValues());
    }
    
    @PostMapping("/request-transfer")
    public ResponseEntity<RequestTransferResponse> requestTransfer(@RequestBody TransferRequest request, Principal principal) {
        AssetsTransferQueue atq = new AssetsTransferQueue(request.getProductType(), request.getTransferFrom(), request.getTransferTo(), request.getTransferAmount());
        
        RequestTransferResponse response = new RequestTransferResponse();
        try {
            transferQueueService.save(atq);
            
            if(request.getFromStockRequest() == null || !request.getFromStockRequest()) {
                logService.pushLog(1301, principal.getName(), atq, null, atq.getId().toString());
            } else {
                logService.pushLog(1502, principal.getName(), atq, null, atq.getId().toString());
            }
            
            response.setSaved(atq);
            
            return ResponseEntity.ok(response);
        } catch (TransferToTheSameSchemeException|IdenticalTransferException e) {
            response.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/update-transfer-quantity")
    public void updateTransferQuantity(@RequestBody TransferQueueUpdateQuantityRequest request, Principal principal) throws TransferToTheSameSchemeException, IdenticalTransferException {
        AssetsTransferQueue atq = transferQueueService.findById(request.getId());
        AssetsTransferQueue oldAtq = new AssetsTransferQueue(atq);
        atq.setQuantity(request.getQuantity());
        
        logService.pushLog(1302, principal.getName(), atq, oldAtq, atq.getId().toString());
        
        transferQueueService.save(atq);
    }
    
    @DeleteMapping("/transfer-request/{id}")
    public void deleteTransferRequestById(@PathVariable("id") Integer id, Principal principal) {
        AssetsTransferQueue oldAtq = transferQueueService.findById(id);
        
        logService.pushLog(1303, principal.getName(), null, oldAtq, oldAtq.getId().toString());
        
        transferQueueService.deleteById(id);
    }
    
    @DeleteMapping("/transfer-request/by-transfer-to-scheme/{name}")
    public void deleteTransferRequestByTransferToScheme(@PathVariable("name") String name, Principal principal) {
        
        User user = userService.findOne(principal.getName());
        
        logService.pushLog(1304, user.getUserEmail(), name);
        
        if(!user.getUserRole().equals("0,0,0,0,1")) {//is user not only a fulfillment operator
            transferQueueService.deleteByTransferToScheme(name);
        } else {
            transferQueueService.deleteFulfillmentByTransferToScheme(name);
        }
    }
    
    @GetMapping("/central-depot/product-type-id-sorted")
    public Map<Integer, CentralDepotResponse> getAllCentralDepotRecords(Principal principal){
        List<CentralDepotResponse> list = CentralDepotResponse.createListFrom(centralDepotService.findAll(userService.findOne(principal.getName())));
        return list.stream().collect(Collectors.toMap(CentralDepotResponse::returnProductTypeId, r -> r));
        
    }

    @GetMapping("/stock-usage-reports")
    public List<StockUsageReportsResponse> getAllStockUsageReports(){
        return StockUsageReportsResponse.createListFrom(stockUsageReportsService.findAll());
    }
    
    @GetMapping("/stock-usage-reports/{schemeName}")
    public List<StockUsageReportsResponse> getAllStockUsageReportsByScheme(@PathVariable("schemeName") String schemeName){
        return StockUsageReportsResponse.createListFrom(stockUsageReportsService.findBySchemeName(schemeName));
    }
    
    @PostMapping("/stock-usage-reports")
    public StockUsageReportsResponse saveStockUsageResponse(@RequestBody StockUsageReportsResponse resp, Principal principal) {
        StockUsageReports saved = stockUsageReportsService.saveReport(resp.toOriginal());

        if(saved.getState().getId() == StockUsageReportsService.STATE_USED.intValue()) {
            logService.pushLog(1900, principal.getName(), saved.toJSONObject());
        } else {
            logService.pushLog(1901, principal.getName(), saved.toJSONObject());
        }
        
        return new StockUsageReportsResponse(saved);
    }
    
    @GetMapping("/low-stock-percentage")
    public Double getLowStockPercentage() {
        return Double.valueOf(systemParamsService.findParameterByName("low_stock_percentage")) / 100;
    }
    
	@GetMapping(value = "/qr-pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
	public void getAssetQrPDF(@PathVariable("id") Integer id, HttpServletResponse response,
			Principal principal) throws IOException {

		Product product = productService.findById(id);
		if (product != null) {
			String typeName = product.getType().getTypeName();
			try {
				QRGenerator qrGenerator = new QRGeneratorImpl(typeName, String.valueOf(id), product.getId().toString());
				byte[] pdfBytes = qrGenerator.getPdfBytes();
				response.setHeader("Content-Disposition", "inline");
				response.setHeader("Content-Type", MediaType.APPLICATION_PDF_VALUE);
				response.setContentLength(pdfBytes.length);
				FileCopyUtils.copy(pdfBytes, response.getOutputStream());
			} catch (QRGenerationException e) {
				LOGGER.warn(e.getMessage());
			}
		}
	}
}
