package com.beskyd.ms_control.business.general;

import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValues;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;
import com.beskyd.ms_control.business.stockrequests.StockRequest;
import com.beskyd.ms_control.business.stockrequests.StockRequestService;
import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SummaryInfoService {

    private final StockRequestService stockRequestService;
    private final AssetsCurrentValuesService acvService;
    private final MailService mailService;
    private final UserService userService;
    
    @Value("${send.summary}")
    private Boolean sendSummary;
    
    public SummaryInfoService(StockRequestService stockRequestService, AssetsCurrentValuesService acvService, MailService mailService, UserService userService) {
        this.stockRequestService = stockRequestService;
        this.acvService = acvService;
        this.mailService = mailService;
        this.userService = userService;
    }
    
    public String generateSummaryHtml() {
        final String tdWithBorderTag = "<td style=\"border: solid 1px grey\">";
        final String tdEndTag = "</td>";
        
        StringBuilder html = new StringBuilder("<strong>Yesterday Stock Requests</strong><br>"
            + "<table style=\"border-collapse: collapse; width: 100%;\">\r\n" +
            "  <tr>\r\n" + 
            "    <th style=\"border: solid 1px grey\">Scheme</th>\r\n" + 
            "    <th style=\"border: solid 1px grey\">Request Number</th>\r\n" + 
            "    <th style=\"border: solid 1px grey\">Requested Items</th>\r\n" + 
            "  </tr>");
        
//        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
//        time = Timestamp.from(time.toInstant().minus(1, ChronoUnit.DAYS));
        
        List<StockRequest> createdRequests = stockRequestService.findInRangeOfDateToNow(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        
        Map<String, List<StockRequest>> requestsByScheme = createdRequests.stream().collect(Collectors.groupingBy(sr -> sr.getScheme().getName()));

        requestsByScheme.forEach((scheme, list) -> {
            for (int i = 0; i < list.size(); i++) {
                html.append("<tr>");
                if (i == 0) {
                    html.append("<td style=\"border: solid 1px grey\" rowspan=\"").append(list.size()).append("\">").append(scheme).append(tdEndTag);
                }
                html.append(tdWithBorderTag);
                html.append(list.get(i).getId());
                html.append(tdEndTag);
                html.append(tdWithBorderTag);
                StringBuilder items = new StringBuilder();
                list.get(i).getRequestedProductTypes().stream().forEach(pt -> items.append(pt.getProductType().getGroupName())
                        .append(" ")
                        .append(pt.getProductType().getTypeName())
                        .append(": ")
                        .append(pt.getOrderValue())
                        .append("<br>"));
                html.append(items);
                html.append(tdEndTag);
                html.append("</tr>");
            }
        });


        html.append("</table>");
        
        html.append("<br><br><br>");
        
        List<AssetsCurrentValues> lackingStocks = acvService.findLackingAssets();
        
        Map<String, List<AssetsCurrentValues>> stocksByScheme = lackingStocks.stream().collect(Collectors.groupingBy(s -> s.getScheme().getName()));

        html.append("<strong>Running Out Stocks</strong><br>"
            + "<table style=\"border-collapse: collapse; width: 100%;\">\r\n" +
            "  <tr>\r\n" + 
            "    <th style=\"border: solid 1px grey\">Scheme</th>\r\n" + 
            "    <th style=\"border: solid 1px grey\">Asset Type</th>\r\n" + 
            "    <th style=\"border: solid 1px grey\">Current Level</th>\r\n" + 
            "  </tr>");
        
        
        
        stocksByScheme.forEach((scheme, stocks) -> {
            Map<String, List<AssetsCurrentValues>> stocksByGroup = stocks.stream().collect(Collectors.groupingBy(s -> s.getProductType().getAssetGroup()));
        
            StringBuilder schemeCell = new StringBuilder();
            schemeCell.append("<td style=\"border: solid 1px grey\" rowspan=\"").append(stocksByGroup.keySet().size()).append("\">").append(scheme).append(tdEndTag);
            
            
            stocksByGroup.forEach((group, sts) -> {
                html.append("<tr>");
                html.append(schemeCell);
                schemeCell.setLength(0);
                
                html.append(tdWithBorderTag).append(group).append(tdEndTag);
                
                html.append(tdWithBorderTag);
                
                sts.stream().forEach(s -> html.append(s.getProductType().getTypeName()).append(": ").append(s.getQuantity()).append("<br>"));
                
                html.append(tdEndTag);
                
                html.append("</tr>");
            });
        });
        
        
        
        return html.toString();
    }
    
    @Scheduled(cron = "0 0 4 * * ? ")//To execute every day on 04:00
//    @Scheduled(fixedDelay = 600000)//delay - 60 min, just for a development stage
    public void sendSummaryToManager() {
        if(Boolean.FALSE.equals(sendSummary)) {
            return;
        }
        
        List<User> managers = userService.findUsersByRoles(User.ROLE_PURCHASE_MANAGER);
        
        String emailHtml = generateSummaryHtml();       
        
        Timestamp date = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
        
        for(User manager : managers) {
            mailService.sendHtmlMessage(manager.getUserEmail(), "Daily Summary for " + new SimpleDateFormat("dd/MM/yyyy").format(date), emailHtml);
        }
    }
}
