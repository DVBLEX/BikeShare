package com.beskyd.ms_control.business.general;

import java.sql.Timestamp;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TimestampResponse {

    @JsonFormat(pattern="dd/MM/YYYY HH:mm")
    private Timestamp datetime;

    public TimestampResponse(Timestamp datetime) {
        this.datetime = datetime;
    }

    public TimestampResponse() {
    }

    
    public Timestamp getDatetime() {
        return datetime;
    }

    
    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(datetime);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TimestampResponse other = (TimestampResponse) obj;
        return Objects.equals(datetime, other.datetime);
    }

    @Override
    public String toString() {
        return "TimestampResponse [datetime=" + datetime + "]";
    }
    
    
}
