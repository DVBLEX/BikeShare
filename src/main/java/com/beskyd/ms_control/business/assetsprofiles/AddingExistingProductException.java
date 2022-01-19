package com.beskyd.ms_control.business.assetsprofiles;

public class AddingExistingProductException extends Exception{

    public AddingExistingProductException(Product product) {
        super("You are trying to add a new product with existing parameters " + product.getType().getTypeName() + " " + product.getProductId().getProductName() + " " + product.getProductId().getSupplier().getName());
    }
}
