package com.beskyd.ms_control.business.repairreports.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CoordinatesRequest {

    private double latitude;
    
    private double longitude;


    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CoordinatesRequest other = (CoordinatesRequest) obj;
        return Double.doubleToLongBits(latitude) == Double.doubleToLongBits(other.latitude) && Double.doubleToLongBits(longitude) == Double.doubleToLongBits(other.longitude);
    }

    @Override
    public String toString() {
        return "CoordinatesRequest [latitude=" + latitude + ", longitude=" + longitude + "]";
    }
    
    
}
