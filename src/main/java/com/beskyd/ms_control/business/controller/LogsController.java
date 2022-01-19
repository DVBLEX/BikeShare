package com.beskyd.ms_control.business.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beskyd.ms_control.business.audit.OperationsLogResponse;
import com.beskyd.ms_control.business.audit.OperationsLoggingService;
import com.beskyd.ms_control.business.requests.LogsFilterRequest;

@RestController
@RequestMapping("/msc-api/logs")
public class LogsController {

    private final OperationsLoggingService loggingService;

    @Inject
    public LogsController(OperationsLoggingService loggingService) {
        this.loggingService = loggingService;
    }
    
    @GetMapping("")
    public List<OperationsLogResponse> getAllLogs(){
        return OperationsLogResponse.createListFrom(loggingService.findAllLogs());
    }
    
    @GetMapping("/{page}/{pageSize}")
    public List<OperationsLogResponse> getLogsByPages(@PathVariable("page") int page, @PathVariable("pageSize") int pageSize){
        return OperationsLogResponse.createListFrom(loggingService.findByPages(page, pageSize));
    }
    
    @PutMapping("")
    public List<OperationsLogResponse> getLogsByFilters(@RequestBody LogsFilterRequest request){
        return OperationsLogResponse.createListFrom(loggingService.findLogsByFilters(request.getUserEmails(), request.getGroups(), request.getStartDateMilis(), request.getEndDateMilis(), request.getOrderNumber()));
    }
    
    @GetMapping("/action-groups")
    public List<String> getAllActionGroups(){
        return loggingService.getSystemOperationsGroupList();
    }
}
