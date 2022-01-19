package com.beskyd.ms_control.business.usermanagement;

import java.io.Serializable;

public class SignInResponse implements Serializable{
    
    private boolean authorisationSuccess;
    private boolean needPasswordChange;
    private String token;
    
    public boolean getNeedPasswordChange() {
        return needPasswordChange;
    }

    
    public void setNeedPasswordChange(boolean needPasswordChange) {
        this.needPasswordChange = needPasswordChange;
    }

    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }


    public boolean getAuthorisationSuccess() {
        return authorisationSuccess;
    }

    
    public void setAuthorisationSuccess(boolean authorisationSuccess) {
        this.authorisationSuccess = authorisationSuccess;
    }


    @Override
    public String toString() {
        return "SingInResponse [authorisationSuccess=" + authorisationSuccess + ", token=" + token + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (authorisationSuccess ? 1231 : 1237);
        result = prime * result + (needPasswordChange ? 1231 : 1237);
        result = prime * result + ((token == null) ? 0 : token.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SignInResponse other = (SignInResponse) obj;
        if (authorisationSuccess != other.authorisationSuccess)
            return false;
        if (needPasswordChange != other.needPasswordChange)
            return false;
        if (token == null) {
            if (other.token != null)
                return false;
        } else if (!token.equals(other.token))
            return false;
        return true;
    }
    
    
}
