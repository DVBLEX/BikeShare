package com.beskyd.ms_control.business.controller.exceptionhandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.beskyd.ms_control.business.audit.OperationsLoggingService;
import com.beskyd.ms_control.business.general.MailService;

@ControllerAdvice
public class ControllerExceptionsHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionsHandler.class);
    
    @Autowired
    private OperationsLoggingService loggingService;
    
    @Autowired
    private MailService mailService;
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseBody> handleAllExceptions(HttpServletRequest request, Principal principal, Exception ex){
        LOGGER.info("Exception Occured:: URL={}", request.getRequestURL());
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        
        String trace = "";
        if(ex.getMessage() != null && ex.getMessage().length() > 0) {
            trace += ex.getMessage() + " ";
        }
        trace += sw.toString();
        
        String name = "";
        if(principal != null) {
            name = principal.getName();
        }
        
        loggingService.pushLog(1800, name, trace);
        
        LOGGER.error(ex.getMessage(), ex);
        
        mailService.sendSimpleMessage("andrew@telclic.net", "System error", trace);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponseBody(ex.getMessage()));
    }
    
    
    
}
