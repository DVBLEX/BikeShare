package com.beskyd.ms_control.business.general;

import com.beskyd.ms_control.business.distributions.Distribution;
import com.beskyd.ms_control.business.distributions.DistributionAssets;
import com.beskyd.ms_control.business.purchaseorders.PurchaseOrderResponse;
import com.beskyd.ms_control.business.usermanagement.User;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailBuilderService {

    private final String COMMENT_MARK = "<!--COMMENT-->";
    private final String EMPTY_STRING = "";
    private final SystemParametersService systemParamsService;

    public EmailBuilderService(SystemParametersService systemParamsService) {
        this.systemParamsService = systemParamsService;
    }

    public String constructPurchaseOrderHTML(User user, PurchaseOrderResponse order) {
        StringBuilder emailHtml = new StringBuilder("<h2 style=\"text-align: center;\"><strong>Purchase order: </strong>&nbsp;" + order.getId() + "</h2>\r\n" +
                "<h4>Generated on:&nbsp;" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(order.getStateChangeDate()) + "</h4>\r\n" +
                (order.getSupplier().getName() != null && order.getSupplier().getName().length() > 0 ?  "<p style='font-weight: bold'>" + order.getSupplier().getName() + "</p>\r\n" : "") +
                (order.getSupplier().getPhone() != null && order.getSupplier().getPhone().length() > 0 ?  "<p style='font-weight: bold'>" + order.getSupplier().getPhone() + "</p>\r\n" : "") +
                (order.getSupplier().getWebsite() != null && order.getSupplier().getWebsite().length() > 0 ?  "<p style='font-weight: bold'>" + order.getSupplier().getWebsite() + "</p>\r\n" : "") +
                (order.getSupplier().getContact() != null && order.getSupplier().getContact().length() > 0 ?  "<p style='font-weight: bold'>" + order.getSupplier().getContact() + "</p>\r\n" : "") +
                "<h5>&nbsp;</h5>\r\n" +
                "<h5>&nbsp;</h5>\r\n" +
                "<p>Greetings!</p>\r\n" +
                "<p>Please find below a list of items we would like to order.</p>\r\n" +
                "<p>&nbsp;</p>\r\n");

        emailHtml.append("<p></p>")
                .append("<table style=\"font-size: 16px; font-family: Arial, Helvetica, sans-serif; border-collapse: collapse;\">")
                .append("<tr><th style=\"width: 100px; height: 40px; border: 1px solid #ddd; padding: 8px; background-color: #dbdbdb;\">No</th><th style=\"width: 300px; height: 40px; border: 1px solid #ddd; padding: 8px; background-color: #dbdbdb;\">Name</th><th style=\"min-width: 200px; height: 40px; border: 1px solid #ddd; padding: 8px; background-color: #dbdbdb;\">Amount</th></tr>");

        for(int i = 0; i < order.getOrderedProducts().size(); i++) {
            emailHtml.append("<tr><td style=\"width: 100px; border: 1px solid #ddd; padding: 8px;\">" + (i + 1) + "</td><td style=\"width: 300px; border: 1px solid #ddd; padding: 8px;\">").append(order.getOrderedProducts().get(i).getProduct().getType().getGroupName())
                    .append(" ").append((order.getOrderedProducts().get(i).getProduct().getProductId().getProductName() != null ? order.getOrderedProducts().get(i).getProduct().getProductId().getProductName() : "")).append("</td><td style=\"width: 200px; border: 1px solid #ddd; padding: 8px;\">").append(order.getOrderedProducts().get(i).getAmount()).append("</td></tr>");
        }
        emailHtml.append("</table>");
        emailHtml.append(COMMENT_MARK); //real comment will be inserted here

        emailHtml.append("<p>&nbsp;</p>\r\n" +
                "<p>&nbsp;</p>\r\n" +
                "<hr />\r\n" +
                "<p>Regards,</p>\r\n" +
                "<p>" + user.getFirstName() + " " + user.getLastName() + "</p>\r\n" +
                "<p>" + user.getUserEmail() +"</p>\r\n" +
                "<p>&nbsp;</p>\r\n" +
                "<hr />\r\n"+
                "<p>" + systemParamsService.findParameterByName("pdf_text") + "</p>");

        return emailHtml.toString();
    }

    public String constructPurchaseOrderHTMLWithComment(User user, PurchaseOrderResponse order) {
        String emailHtml = constructPurchaseOrderHTML(user, order);
        int index = emailHtml.indexOf(COMMENT_MARK) + COMMENT_MARK.length();
        StringBuffer resultString = new StringBuffer(emailHtml);
        String substring;
        if (order.getComment() == null || EMPTY_STRING.equals(order.getComment())) {
            substring = "<p><b>Comment: </b></p>"; //there is no comment to show. Or we can hide 'comment' word itself if it will be necessary
        } else {
            substring = "<p><b>Comment: </b><i>" + order.getComment() + "</i></p>";
        }
        resultString.insert(index, substring);
        return resultString.toString();
    }

    public String constructDistributionOrderHTML(Distribution dist) {
        StringBuilder emailHtml = new StringBuilder("<h2 style=\"text-align: center;\"><strong>Distribution order: </strong>&nbsp;" + dist.getId() + " for " + dist.getSchemeTo().getName() + "</h2>\r\n" +
                "<h4>Generated on:&nbsp;" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dist.getStateChangeDate()) + "</h4>\r\n" +
                "<h4>&nbsp;</h4>\r\n" +
                "<h4>&nbsp;</h4>\r\n" +
                "<p>&nbsp;</p>\r\n");

        emailHtml.append("<p></p>")
                .append("<table style=\"font-size: 16px; font-family: Arial, Helvetica, sans-serif; border-collapse: collapse;\">")
                .append("<tr><th style=\"width: 100px; height: 40px; border: 1px solid #ddd; padding: 8px; background-color: #dbdbdb;\">No</th><th style=\"width: 300px; height: 40px; border: 1px solid #ddd; padding: 8px; background-color: #dbdbdb;\">Name</th><th style=\"min-width: 200px; height: 40px; border: 1px solid #ddd; padding: 8px; background-color: #dbdbdb;\">Amount</th></tr>");

        List<DistributionAssets> assets = new ArrayList<>(dist.getAssets());

        for(int i = 0; i < assets.size(); i++) {
            emailHtml
                    .append("<tr><td style=\"width: 100px; border: 1px solid #ddd; padding: 8px;\">" + (i + 1) + "</td><td style=\"width: 300px; border: 1px solid #ddd; padding: 8px;\">")
                    .append(assets.get(i).getTypeOfAssets().getGroupName())
                    .append(" ")
                    .append(assets.get(i).getTypeOfAssets().getTypeName())
                    .append("</td><td style=\"width: 200px; border: 1px solid #ddd; padding: 8px;\">")
                    .append(assets.get(i).getQuantity()).append("</td></tr>");
        }


        emailHtml.append("</table>");

        emailHtml.append("<p>&nbsp;</p>");
        if(dist.getNotes() != null) {
            emailHtml.append("<p><div>").append(dist.getNotes()).append("</div></p>");
        }

        return emailHtml.toString();
    }

}
