package com.beskyd.ms_control.security;

import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException
    {
        User user = (User) authentication.getPrincipal();
        String authorities = user
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        
        if (authorities.equals("ROLE_FIRSTLOGINNER")) {
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(new JSONObject().put("passwordRecoveryToken", user.getRecoveryToken()).toString());
            return;
        } else {
            response.addHeader("X-Granted-Authorities", authorities); 
        }
        
        request.getSession(false).setMaxInactiveInterval(10000000);
        //response.sendRedirect(request.getContextPath() + "/views/home");
    }
}
