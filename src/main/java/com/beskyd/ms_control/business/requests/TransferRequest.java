package com.beskyd.ms_control.business.requests;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.general.Scheme;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.json.JSONObject;

@EqualsAndHashCode
@ToString
@Getter
@Setter
public class TransferRequest implements ParentRequest {

    /** ObjectMapper that provides JSON without indentations */
    protected static com.fasterxml.jackson.databind.ObjectMapper compact = new ObjectMapper();
    /** Logging object */
    protected static org.slf4j.Logger logger = null;
    /** ObjectMapper that provides JSON with indentations */
    protected static com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private TypeOfAssets productType;
    
    private Scheme transferFrom;
    
    private Scheme transferTo;
    
    private Integer transferAmount;
    
    private Boolean fromStockRequest;

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("productType", productType.toJSONObject());
        jo.put("transferFrom", transferFrom.toJSONObject());
        jo.put("transferTo", transferTo.toJSONObject());
        jo.put("transferAmount", transferAmount);
        jo.put("fromStockRequest", fromStockRequest);
        return jo;
    }
}
