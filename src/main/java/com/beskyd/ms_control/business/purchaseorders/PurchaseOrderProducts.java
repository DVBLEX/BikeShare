package com.beskyd.ms_control.business.purchaseorders;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.*;
import org.json.JSONObject;

import com.beskyd.ms_control.business.assetsprofiles.Product;
import com.beskyd.ms_control.config.addLogic.JsonAware;

@Entity
@IdClass(PurchaseOrderProducts.ComplexId.class)
@Table(name = "purchase_orders_products")
@NoArgsConstructor
public class PurchaseOrderProducts implements JsonAware {

    @Id
    @Column(name = "id_purchase_order")
    @Getter @Setter private Integer idPurchaseOrder;

    @Id
    @Column(name = "id_product")
    @Getter @Setter private Integer idProduct;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "id_purchase_order")
    @Getter private PurchaseOrder purchaseOrder;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "id_product")
    @Getter private Product product;

    @Getter @Setter private Integer amount;

    @Getter @Setter private Integer confirmed;


    public PurchaseOrderProducts(PurchaseOrder purchaseOrder, Product product, Integer amount, Integer confirmed) {
        this.purchaseOrder = purchaseOrder;
        if (purchaseOrder != null) {
            this.idPurchaseOrder = purchaseOrder.getId();
        }
        this.product = product;
        this.idProduct = product.getId();
        this.amount = amount;
        this.confirmed = confirmed;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class ComplexId implements Serializable {

        @Column(name = "id_purchase_order")
        private Integer idPurchaseOrder;

        @Column(name = "id_product")
        private Integer idProduct;
    }

    /**
     * Also sets {@code idPurchaseOrder}
     *
     * @param purchaseOrder
     */
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
        setIdPurchaseOrder(purchaseOrder.getId());
    }

    /**
     * Also sets {@code idProduct}
     *
     * @param product
     */
    public void setProduct(Product product) {
        this.product = product;
        setIdProduct(product.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduct, idPurchaseOrder);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PurchaseOrderProducts other = (PurchaseOrderProducts) obj;
        return Objects.equals(idProduct, other.idProduct) && Objects.equals(idPurchaseOrder, other.idPurchaseOrder);
    }

    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("idProduct", idProduct);
        jo.put("product", product.getType().getTypeName() + " " + product.getProductId().getProductName());
        jo.put("idPurchaseOrder", idPurchaseOrder);
        jo.put("amount", amount);
        jo.put("confirmed", confirmed);
        return jo;
    }
}
