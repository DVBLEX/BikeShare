package com.beskyd.ms_control.business.purchaseorders;


public class ProductAlreadyExistsInOrder extends Exception{

    public ProductAlreadyExistsInOrder(Integer productId, Integer orderId) {
        super("Product with id " + productId + " already is in purchase order No " + orderId);
    }
    
}
