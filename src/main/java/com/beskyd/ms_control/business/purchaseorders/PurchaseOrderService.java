package com.beskyd.ms_control.business.purchaseorders;

import com.beskyd.ms_control.business.assetsprofiles.Product;
import com.beskyd.ms_control.business.assetsprofiles.ProductService;
import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.stockrequests.RequestProductsTypesList;
import com.beskyd.ms_control.business.stockrequests.StockRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {
    
    public static final int NEW_ORDER = 11;
    public static final int SENT_ORDER = 12;
    public static final int PARTIALLY_FULFILLED_ORDER = 13;
    public static final int FULFILLED_ORDER = 14;
    
    private final PurchaseOrderRepository repo;
    private final ProductService productsService;
    private final PurchaseOrderProductsService orderProductService;
    private final StatesRepository statesRepo;
    
    @Inject
    public PurchaseOrderService(PurchaseOrderRepository repo, ProductService productsService,
        PurchaseOrderProductsService orderProductService, StatesRepository statesRepo) {
        this.repo = repo;
        this.productsService = productsService;
        this.orderProductService = orderProductService;
        this.statesRepo = statesRepo;
    }
    
    public List<PurchaseOrder> findAll() {
        return repo.findAll(Sort.by("state.id").and(Sort.by("id").ascending()).ascending());
    }
    
    /**
     * Find {@link PurchaseOrder} by id
     * @param id
     * @return object, if present, null - if not
     */
    public PurchaseOrder findById(Integer id) {
        return repo.findById(id).orElse(null);
    }
    
    /**
     * Saves {@link PurchaseOrder}. If id is null, sets state with id 11 (state 'New') and {@code stateChangeDate}.
     * Also sets savable purchase order to every ordered product and saves list of {@link PurchaseOrderProducts} of this purchase order.
     * If savable order is new, then for every orders product we set confirmed amount from ordered amount.
     * @param savable
     * @return id of saved {@code PurchaseOrder}
     */
    public Integer save(PurchaseOrder savable) {
        boolean idWasZero = false;
        
        if(savable.getId() == null) {
            idWasZero = true;
                
            savable.setState(statesRepo.findById(NEW_ORDER).orElseThrow());
            savable.setStateChangeDate(Timestamp.valueOf(LocalDateTime.now()));   
        } else {
            for(PurchaseOrderProducts prod : savable.getOrderedProducts()) {
                prod.setPurchaseOrder(savable);
            }
            
            orderProductService.saveAll(savable.getOrderedProducts());
        }
        
        if(savable.getInvoice() == null) {
            savable.setInvoice("");
        }
        if(savable.getNotes() == null) {
            savable.setNotes("");
        }
        
        savable = repo.save(savable);
        
        if(savable.getOrderedProducts() != null && idWasZero) {
            for(PurchaseOrderProducts prod : savable.getOrderedProducts()) {
                prod.setPurchaseOrder(savable);
            }
            
            orderProductService.saveAll(savable.getOrderedProducts());
        }
        
        return savable.getId();
           
    }
    
    public void delete(Integer id) {
        repo.deleteById(id);
    }
    
    public void setOrderAsPartiallyFulfilled(PurchaseOrder order) {
        order.setState(statesRepo.findById(PARTIALLY_FULFILLED_ORDER).orElseThrow());
        order.setStateChangeDate(Timestamp.valueOf(LocalDateTime.now()));
    }
    
    public void setOrderAsFulfilled(PurchaseOrder order) {
        order.setState(statesRepo.findById(FULFILLED_ORDER).orElseThrow());
        order.setStateChangeDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    private void processOnlyNewRequests(List<StockRequest> requests,
                                                      Map<Integer, Integer> amountsByProdTypeId,
                                                      Map<Integer, List<Product>> productsBySupplierId,
                                                      List<Product> allProducts) {

        for(StockRequest request : requests) {
            //Process request only of it's New
            if(request.getState().getId() == 1) {
                for(RequestProductsTypesList requestedProdType : request.getRequestedProductTypes()) {

                    Integer typeId = requestedProdType.getProductType().getId();

                    //set requested amounts by type of assets id
                    if(amountsByProdTypeId.containsKey(typeId)) {
                        amountsByProdTypeId.put(typeId, amountsByProdTypeId.get(typeId) + requestedProdType.getOrderValue());
                    } else {
                        amountsByProdTypeId.put(typeId, requestedProdType.getOrderValue());
                        List<Product> productsOfType = productsService.findByTypeId(typeId);
                        allProducts.addAll(productsOfType);

                        //lets sort products by supplier id
                        for(Product p : productsOfType) {
                            Integer supplierId = p.getProductId().getSupplier().getId();

                            if(!productsBySupplierId.containsKey(supplierId)) {
                                productsBySupplierId.put(supplierId, new ArrayList<>());
                            }

                            productsBySupplierId.get(supplierId).add(p);
                        }
                    }

                }
            }
        }
    }

    //generate orders for each supplier, whose products we have in requests
    //and we'll will do it, until map with products is not empty
    private void generateOrders(Map<Integer, List<Product>> productsBySupplierId,
                                List<Product> productsPushedToOrder,
                                Map<Integer, Integer> amountsByProdTypeId,
                                List<PurchaseOrder> orders) {
        while (!productsBySupplierId.isEmpty()) {
            List<Product> theBiggestProductsListForOneSupplier = null;
            Integer supplierId = null;

            //lets find supplier with the biggest list of requested products
            for (Map.Entry<Integer, List<Product>> etr : productsBySupplierId.entrySet()) {
                if (theBiggestProductsListForOneSupplier == null || theBiggestProductsListForOneSupplier.size() < etr.getValue().size()) {
                    theBiggestProductsListForOneSupplier = etr.getValue();
                    supplierId = etr.getKey();
                }
            }

            if (theBiggestProductsListForOneSupplier.isEmpty()) {
                break;
            }

            //remove products for the supplier, for which we are creating a purchase order
            productsBySupplierId.remove(supplierId);

            for (Product orderedProduct : productsPushedToOrder) {

                Iterator<Product> iter = theBiggestProductsListForOneSupplier.iterator();

                //we need to remove products, types of which were ordered previously
                while (iter.hasNext()) if (iter.next().getType().getId().equals(orderedProduct.getType().getId())) {
                    iter.remove();
                }
            }

            //need to do this check, after removing elements
            if (theBiggestProductsListForOneSupplier.isEmpty()) {
                continue;
            }

            List<PurchaseOrder> ordersBySupplier = repo.findBySupplier_IdAndState_Id(supplierId, NEW_ORDER);
            PurchaseOrder order = null;

            if (!ordersBySupplier.isEmpty()) {
                order = ordersBySupplier.get(ordersBySupplier.size() - 1);
                if (order.getState().getId() != NEW_ORDER) {
                    order = null;
                }
            }

            if (order == null) {
                order = new PurchaseOrder(null, theBiggestProductsListForOneSupplier.get(0).getProductId().getSupplier(),
                        statesRepo.getOne(NEW_ORDER), Timestamp.valueOf(LocalDateTime.now()), "", "", new ArrayList<>(), "");
            }

            //now we are creating a list of products for purchase order
            for (Product productToOrder : theBiggestProductsListForOneSupplier) {

                boolean productPresent = false;

                //we need to check, if this product isn't already in the order
                for (PurchaseOrderProducts orderProduct : order.getOrderedProducts()) {
                    if (orderProduct.getProduct().getId().equals(productToOrder.getId())) {
                        orderProduct.setAmount(orderProduct.getAmount() + amountsByProdTypeId.get(productToOrder.getType().getId()));

                        productPresent = true;

                        break;
                    }
                }

                if (productPresent) {
                    continue;
                }

                order.getOrderedProducts().add(new PurchaseOrderProducts(order, productToOrder, amountsByProdTypeId.get(productToOrder.getType().getId()), 0));
            }

            orders.add(order);

            //Add ordered product to the collection of ordered products
            productsPushedToOrder.addAll(theBiggestProductsListForOneSupplier);
        }
    }

//    private void searchForCheaperAlternatives(List<PurchaseOrder> orders) {
//        for (int j = 0; j < orders.size(); j++) {
//            PurchaseOrder order = orders.get(j);
//
//            if (order.getId() != null) {//we can search cheaper alternatives for products from the new order
//                continue;
//            }
//
//            for (int i = 0; i < order.getOrderedProducts().size(); i++) {
//                PurchaseOrderProducts orderedProduct = order.getOrderedProducts().get(i);
//
//                if (orderedProduct.getProduct().getType().getProducts().size() > 1) {
//                    Product cheapestAlternateProduct = orderedProduct.getProduct();
//
//                    //searching for the cheapest product
//                    for (Product alternateProduct : orderedProduct.getProduct().getType().getProducts()) {
//                        if (!alternateProduct.getId().equals(orderedProduct.getProduct().getId())
//                                //checking, if a product of the same type is cheaper
//                                && alternateProduct.getProductId().getPrice().floatValue() < cheapestAlternateProduct.getProductId().getPrice().floatValue()) {
//
//                            cheapestAlternateProduct = alternateProduct;
//                        }
//                    }
//
//                    if (cheapestAlternateProduct != orderedProduct.getProduct()) {
//                        order.getOrderedProducts().remove(i--);
//
//                        PurchaseOrderProducts newOrderedProduct = new PurchaseOrderProducts(null, cheapestAlternateProduct, amountsByProdTypeId.get(cheapestAlternateProduct.getType().getId()), 0);
//
////                        if(newOrderedProduct.getProduct().getMinOrder() > newOrderedProduct.getAmount()) {
////                            newOrderedProduct.setAmount(newOrderedProduct.getProduct().getMinOrder());
////                        }
//
//                        int supplierId = cheapestAlternateProduct.getProductId().getSupplier().getId();
//                        //searching for an order with a suitable supplier
//                        Optional<PurchaseOrder> alternateOrderOpt = orders.stream().filter(o -> o.getSupplier().getId() == supplierId)
//                                .findFirst();//find first, because there can be only one order for each supplier
//
//                        if (alternateOrderOpt.isPresent()) {
//                            //adding alternate product to an order with suitable supplier
//                            newOrderedProduct.setPurchaseOrder(alternateOrderOpt.get());
//                            alternateOrderOpt.get().getOrderedProducts().add(newOrderedProduct);
//
//                        } else {
//                            List<PurchaseOrder> newOrders = repo.findBySupplier_IdAndState_Id(cheapestAlternateProduct.getProductId().getSupplier().getId(), NEW_ORDER);
//                            PurchaseOrder newOrder = null;
//                            if (newOrders.isEmpty()) {
//                                newOrder = new PurchaseOrder(null, cheapestAlternateProduct.getProductId().getSupplier(),
//                                        statesRepo.getOne(NEW_ORDER), Timestamp.valueOf(LocalDateTime.now()), "", "", new ArrayList<>(), "");
//                            } else {
//                                newOrder = newOrders.get(0);
//                            }
//                            newOrderedProduct.setPurchaseOrder(newOrder);
//                            newOrder.getOrderedProducts().add(newOrderedProduct);
//
//                            orders.add(newOrder);
//                        }
//                    }
//                }
//            }
//        }
//    }
    
    /**
     * Here we gather all products from requested product types. Aggregate amounts of that types, that needed to be ordered.
     * And then, over gathered products, we create {@link PurchaseOrder}
     * @param requests
     * @return
     */
    public List<PurchaseOrder> generatePurchaseOrders(List<StockRequest> requests) {
        List<PurchaseOrder> orders = new ArrayList<>();
        Map<Integer, List<Product>> productsBySupplierId = new HashMap<>();
        List<Product> allProducts = new ArrayList<>();
        //in this map we will record amounts of types of assets to request
        // parameters are Integer/*type of assets id*/ and Integer /*amount of products to request*/
        Map<Integer, Integer> amountsByProdTypeId = new HashMap<>();
        //We'll need this below, to check, what products already have been ordered, to not order them the second time
        List<Product> productsPushedToOrder = new ArrayList<>();

        processOnlyNewRequests(requests, amountsByProdTypeId, productsBySupplierId, allProducts);

        generateOrders(productsBySupplierId, productsPushedToOrder, amountsByProdTypeId, orders);

        //here we are searching for cheaper alternatives
//        searchForCheaperAlternatives(orders);

        removeOrdersWithEmptyPurchaseList(orders);
        return orders;
    }

    private void removeOrdersWithEmptyPurchaseList(List<PurchaseOrder> orders) {
        for (PurchaseOrder order: orders) {
            for (PurchaseOrderProducts orderProducts : order.getOrderedProducts()) {
                if (orderProducts.getAmount() == null || orderProducts.getAmount() <= 0) {
                    order.getOrderedProducts().remove(orderProducts);
                }
            }

            if (order.getId() == null && order.getOrderedProducts().isEmpty()) {
                orders.remove(order);
            }
        }
    }


    /**
     * Creates purchase orders or update existing, based on split purchase order queue.
     * Products, that occur multiple times, are merged (theirs amounts are being summed)
     * @param queue - list of {@link SplitPurchaseOrderQueue}
     * @return list of {@link PurchaseOrder}'s, generated from queue
     */
    @Transactional
    public List<PurchaseOrder> generatePurchaseOrdersFromSplitQueue(List<SplitPurchaseOrderQueue> queue){        
        Map<Integer, List<SplitPurchaseOrderQueue>> queueBySuppliersId = queue.stream().collect(Collectors.groupingBy(r -> r.getProduct().getProductId().getSupplier().getId()));
        
        List<PurchaseOrder> orders = new ArrayList<>();
        
        for(var entry : queueBySuppliersId.entrySet()) {
            List<SplitPurchaseOrderQueue> queueOfSupplier = entry.getValue();
            
            List<PurchaseOrder> ordersBySupplier = repo.findBySupplier_IdAndState_Id(entry.getKey(), NEW_ORDER);
            PurchaseOrder order = null;
            if(!ordersBySupplier.isEmpty()) {
                order = ordersBySupplier.get(ordersBySupplier.size() - 1);
                if(order.getState().getId() != NEW_ORDER) {
                    order = null;
                }
            } 
            
            //in case we didn't find a suitable order
            if(order == null){
                order = new PurchaseOrder(null, queueOfSupplier.get(0).getProduct().getProductId().getSupplier(), statesRepo.getOne(11), Timestamp.valueOf(LocalDateTime.now()), "", "", new ArrayList<>(), "");
            }
            
            
            for(SplitPurchaseOrderQueue record : queueOfSupplier) {
                
                boolean productPresent = false;
                
                //we need to check, if this product isn't already in the order
                for(PurchaseOrderProducts orderProduct : order.getOrderedProducts()) {
                    if(orderProduct.getProduct().getId().equals(record.getProduct().getId())) {
                        orderProduct.setAmount(orderProduct.getAmount() + record.getQuantity());
                        
                        productPresent = true;
                        
                        break;
                    }
                }
                
                if(productPresent) {
                    continue;
                }
                
                order.getOrderedProducts().add(new PurchaseOrderProducts(order, record.getProduct(), record.getQuantity(), 0));
            }
            
            orders.add(order);
        }
        
        return orders;
    }
    
    public PurchaseOrder saveNewOrder(PurchaseOrder order) {
        List<PurchaseOrder> ordersBySupplier = repo.findBySupplier_IdAndState_Id(order.getSupplier().getId(), NEW_ORDER);
        if(!ordersBySupplier.isEmpty()) {
            PurchaseOrder existingOrder = ordersBySupplier.get(0);
            for(PurchaseOrderProducts op : order.getOrderedProducts()) {
                boolean hasProduct = false;
                for(PurchaseOrderProducts existingOP : existingOrder.getOrderedProducts()) {
                    if(op.getProduct().getId().equals(existingOP.getProduct().getId())) {
                        existingOP.setAmount(existingOP.getAmount() + op.getAmount());
                        hasProduct = true;
                        break;
                    }
                }
                
                if(!hasProduct) {
                    existingOrder.getOrderedProducts().add(op);
                }
                
            }
            save(existingOrder);
            
            return existingOrder;
        } else {
            //here we are searching for duplicates of products in this order
            for(PurchaseOrderProducts op : order.getOrderedProducts()) {
                for (int i = 0; i < order.getOrderedProducts().size(); i++) {
                    if(order.getOrderedProducts().get(i).getProduct().getId().equals(op.getProduct().getId()) 
                        && order.getOrderedProducts().get(i).getProduct() != op.getProduct()) {
                        op.setAmount(op.getAmount() + order.getOrderedProducts().get(i).getAmount());
                        order.getOrderedProducts().remove(i);
                        
                        break;
                    }
                }
            }
            save(order);
            
            return order;
        }
    }

}
