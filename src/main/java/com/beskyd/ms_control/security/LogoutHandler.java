package com.beskyd.ms_control.security;

import com.beskyd.ms_control.business.audit.OperationsLoggingService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutHandler implements LogoutSuccessHandler {

    @Inject
    OperationsLoggingService loggingService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(authentication != null) {
            loggingService.pushLog(1101, authentication.getName(), " has successful logged out");
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
