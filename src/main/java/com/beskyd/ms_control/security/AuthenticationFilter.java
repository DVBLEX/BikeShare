package com.beskyd.ms_control.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.beskyd.ms_control.business.requests.UserCredentialsRequest;
import com.beskyd.ms_control.business.usermanagement.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private UserService userService;
    
    
    public AuthenticationFilter() {
    }
    
    public AuthenticationFilter(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            UserCredentialsRequest creds = objectMapper.readValue(IOUtils.toString(request.getReader()), UserCredentialsRequest.class);
            
            if(userService.signIn(creds.getUserEmail(), creds.getRawPassword()) == null) {
                response.sendError(511, "wrong creds");
                return null;
            }

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(creds.getUserEmail(), creds.getRawPassword());
            
            setDetails(request, token);
            
            return this.getAuthenticationManager().authenticate(token);
        } catch(IOException e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }
}
