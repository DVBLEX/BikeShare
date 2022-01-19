package com.beskyd.ms_control.business.purchaseorders;

import com.beskyd.ms_control.business.general.StatesResponse;
import com.beskyd.ms_control.business.suppliers.SupplierResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PurchaseOrderResponse {

    private static final String SEPARATOR = "}^{";

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    @Getter
    @Setter
    public static class OrderCreds{
        private String invoice;

        private String notes;
    }
    
    private Integer id;

    private SupplierResponse supplier;

    private StatesResponse state;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp stateChangeDate;

    @EqualsAndHashCode.Exclude
    private List<OrderCreds> creds;

    @EqualsAndHashCode.Exclude
    private String comment;
    
    @JsonIgnoreProperties(value = {"purchaseOrder"}, allowSetters = true)
    private List<PurchaseOrderProductsResponse> orderedProducts;

    public PurchaseOrderResponse(Integer id, SupplierResponse supplier, StatesResponse state, Timestamp stateChangeDate, String invoice, String notes, List<PurchaseOrderProductsResponse> orderedProducts, String comment) throws PurchaseOrderException {
        this.id = id;
        this.supplier = supplier;
        this.state = state;
        this.stateChangeDate = stateChangeDate;
        this.orderedProducts = orderedProducts;
        this.comment = comment;
        initCreds(invoice, notes);
    }

    public PurchaseOrderResponse(PurchaseOrder original, boolean ignoreOrderedProducts) throws PurchaseOrderException {
        this.id = original.getId();
        this.supplier = new SupplierResponse(original.getSupplier());
        this.state = new StatesResponse(original.getState());
        this.stateChangeDate = original.getStateChangeDate();
        this.comment = original.getComment();

        initCreds(original.getInvoice(), original.getNotes());
        
        if(!ignoreOrderedProducts) {
            this.orderedProducts = PurchaseOrderProductsResponse.createListFrom(original.getOrderedProducts());
        }
    }
    
    private void initCreds(String invoice, String notes) throws PurchaseOrderException {
        this.creds = new ArrayList<>();
        
        if(invoice == null) {
            creds.add(new OrderCreds("", ""));
            return;
        }
        
        if(notes == null) {
            notes = "";  
        }
        
        List<String> invoices = List.of(invoice.split(Pattern.quote(SEPARATOR)));
        
        String[] splitNotes = notes.split(Pattern.quote(SEPARATOR));
        List<String> notesList = new ArrayList<>();
        
        for (int i = 0; i < invoices.size(); i++) {
            if(splitNotes.length > i) {
                notesList.add(splitNotes[i]);
            } else {
                notesList.add("");
            }
        }
        
        if(invoices.size() != notesList.size()) {
            throw new PurchaseOrderException("number of invoices and notes should match");
        }
        
        
        for (int i = 0; i < invoices.size(); i++) {
            creds.add(new OrderCreds(invoices.get(i), notesList.get(i)));
        }
        if(creds.isEmpty()) {
            creds.add(new OrderCreds("", ""));
        }
    }
    
    public static List<PurchaseOrderResponse> createListFrom(List<PurchaseOrder> originals) throws PurchaseOrderException{
        List<PurchaseOrderResponse> list = new ArrayList<>();
        
        for(PurchaseOrder or : originals) {
            list.add(new PurchaseOrderResponse(or, false));
        }

        return list;
    }
    
    private String invoiceToString() {
        StringBuilder invoice = new StringBuilder();

        if(creds != null) {
            for(OrderCreds cred : creds) {
                invoice.append(cred.getInvoice()).append(SEPARATOR);
            }
        }
        
        return invoice.toString();
    }

    private String notesToString() {
        StringBuilder notes = new StringBuilder();
        
        if(creds != null) {
            for(OrderCreds cred : creds) {
                notes.append(cred.getNotes()).append(SEPARATOR);
            }
        }
                
        return notes.toString();
    }

    /**
     * this will set a list of {@link PurchaseOrderProducts} as {@code null}
     * @return
     */
    public PurchaseOrder toOriginal() {
        return new PurchaseOrder(id, supplier.toOriginal(), state != null ? state.toOriginal() : null, stateChangeDate, invoiceToString(), notesToString(), null, comment);
    }
    
    public PurchaseOrder toOriginalWithOrderedProducts() {
        PurchaseOrder order = new PurchaseOrder(id, supplier.toOriginal(), state != null ? state.toOriginal() : null, stateChangeDate, invoiceToString(), notesToString(), PurchaseOrderProductsResponse.toOriginals(orderedProducts), comment);
       
        for(PurchaseOrderProducts p : order.getOrderedProducts())
           p.setPurchaseOrder(order);
           
        return order;
    }
}
