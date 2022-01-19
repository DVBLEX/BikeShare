package com.beskyd.ms_control.business.purchaseorders;

import com.beskyd.ms_control.business.general.States;
import com.beskyd.ms_control.business.suppliers.Supplier;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PurchaseOrder implements JsonAware{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_supplier")
    private Supplier supplier;
    
    @ManyToOne
    @JoinColumn(name = "id_state")
    private States state;

    @EqualsAndHashCode.Exclude
    private String invoice;

    @EqualsAndHashCode.Exclude
    private String notes;

    @EqualsAndHashCode.Exclude
    private Timestamp stateChangeDate;

    @OneToMany(mappedBy = "purchaseOrder", fetch = FetchType.EAGER)
    private List<PurchaseOrderProducts> orderedProducts;

    private String comment;

    public PurchaseOrder(Integer id, Supplier supplier, States state, Timestamp stateChangeDate, String invoice, String notes, List<PurchaseOrderProducts> orderedProducts, String comment) {
        this.id = id;
        this.supplier = supplier;
        this.state = state;
        this.stateChangeDate = stateChangeDate;
        this.orderedProducts = orderedProducts;
        this.invoice = invoice;
        this.notes = notes;
        this.comment = comment;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("purchaseOrderId", id);
        jo.put("state", state.getName());
        jo.put("stateChangeDate", stateChangeDate);
        jo.put("invoice", invoice);
        jo.put("notes", notes);
        jo.put("comment", comment);
        return jo;
    }
}
