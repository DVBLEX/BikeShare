package com.beskyd.ms_control.business.reports;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String reportName) {
        super("Could not find a report by name " + reportName);
    }
}
