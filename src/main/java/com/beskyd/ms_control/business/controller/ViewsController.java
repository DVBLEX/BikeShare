package com.beskyd.ms_control.business.controller;

import java.security.Principal;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserService;

@Controller
@RequestMapping("/")
public class ViewsController {
    
    private final UserService userService;
    
    @Inject
    public ViewsController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/views/assets-edit")
    public String getAssetsEditPage() {
        return "assets/assets-edit";
    }
    
    @GetMapping("/views/stock-requests")
    public String getStockRequestsPage() {
        return "distribution/stock-requests";
    }
    
    @GetMapping("/views/edit-tables-data")
    public String getEditTablesDataPage() {
        return "system/edit-tables-data";
    }
    
    @GetMapping("/views/stock-balance")
    public String getStockBalancePage() {
        return "scheme-stock-control/stock-balance";
    }
    
    @GetMapping("/views/purchase-orders")
    public String getPurchaseOrdersPage() {
        return "distribution/purchase-orders";
    }
    
    @GetMapping("/views/distribution")
    public String getDistributionPage() {
        return "distribution/distribution";
    }
    
    @GetMapping("/views/assets-report")
    public String getAssetsReportPage() {
        return "scheme-stock-control/assets-report";
    }
    
    @GetMapping("/views/repair-reports")
    public String getRepairReportsPage() {
        return "repair-reports/repair-reports";
    }
    
    @GetMapping("/views/repair-history")
    public String getRepairHistoryPage() {
        return "repair-reports/repair-history";
    }
    
    @GetMapping("/views/create-repair-report")
    public String getCreateRepairReportPage() {
        return "repair-reports/create-repair-report";
    }
    
    @GetMapping("/views/routine-review")
    public String getRoutineReviewPage() {
        return "repair-reports/routine-review";
    }
    
    @GetMapping("/views/suppliers")
    public String getSuppliersPage() {
        return "distribution/suppliers";
    }
    
    @GetMapping("/views/logs")
    public String getLogsPage() {
        return "logs/logs";
    }

    @GetMapping("/views/reports")
    public String getReportsPage() {
        return "reports/reports";
    }
    
    @GetMapping("/views/scheme-stock-control")
    public String getSchemeStockControlPage() {
        return "scheme-stock-control/scheme-stock-control";
    }
    
    @GetMapping({"/views/home", "/"})
    public String getHome(Principal principal) {
        
        if(principal == null) {
            return "redirect:/login";
        }
        
        User user = userService.findOne(principal.getName());
        if(user.hasRole(User.ROLE_ADMIN)) {
            return "redirect:/views/users";
        } else if(user.hasRole(User.ROLE_FULFILLMENT_OPERATOR) || user.hasRole(User.ROLE_PURCHASE_MANAGER)) {
            return "redirect:/views/assets-edit";
        } else if(user.hasRole(User.ROLE_SCHEME_LEADER)){
            return "redirect:/views/repair-reports";
        } else if(user.hasRole(User.ROLE_OPERATOR)){
            return "redirect:/views/repair-reports";
        } else {
            return "redirect:/login";
        }
    }
    
    //Who will have permissions is still under a question
    @GetMapping("/views/users")
    public String getUsersListPage() {
        return "user-administration/user-list";
    }
    
    @GetMapping("/views/users/password-recovery/{recoveryToken}")
    public String getPasswordRecoveryPage(@PathVariable("recoveryToken") String recoveryToken) {
        if (recoveryToken.equals("change-password")) {
            return "user-administration/password-recovery-form";
        }
        return userService.findUserByRecoveryToken(recoveryToken)
                .map(x -> "user-administration/password-recovery-form")
                .orElse("");
    }
    
    @GetMapping("/views/dev-scope/assets-values")
    public String getAssetsValuesPage() {
        return "../dev-scope/assets-values";
    }
    
}
