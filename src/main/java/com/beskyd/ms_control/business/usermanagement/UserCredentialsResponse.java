package com.beskyd.ms_control.business.usermanagement;

import java.util.Objects;

public class UserCredentialsResponse {

    private String firstName;
    
    private String lastName;
    
    private String email;

    public UserCredentialsResponse(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserCredentialsResponse() {
    }

    
    public String getFirstName() {
        return firstName;
    }

    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    
    public String getLastName() {
        return lastName;
    }

    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    
    public String getEmail() {
        return email;
    }

    
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, firstName, lastName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserCredentialsResponse other = (UserCredentialsResponse) obj;
        return Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName);
    }

    @Override
    public String toString() {
        return "UserCredentialsResponse [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
    }
    
    
}
