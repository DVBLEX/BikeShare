package com.beskyd.ms_control.business.usermanagement;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.beskyd.ms_control.config.addLogic.JsonAware;
import com.beskyd.ms_control.security.TokenAuthentication;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User implements JsonAware, UserDetails{
    
    public static final Byte STATE_NEED_PASSWORD_CHANGE=1;
    public static final Byte STATE_ACTIVE_PASSWORD=2;
    public static final Byte STATE_FIRST_LOGIN=3;
    
    
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_OPERATOR = "OPERATOR";
    public static final String ROLE_SCHEME_LEADER = "SCHEME_LEADER";
    public static final String ROLE_PURCHASE_MANAGER = "PURCHASE_MANAGER";
    public static final String ROLE_FULFILLMENT_OPERATOR = "FULFILLMENT_OPERATOR";
    
    public static final String ROLE_PRETTY_ADMIN = "Admin";
    public static final String ROLE_PRETTY_OPERATOR = "Operator";
    public static final String ROLE_PRETTY_SCHEME_LEADER = "Scheme Leader";
    public static final String ROLE_PRETTY_PURCHASE_MANAGER = "Purchase Manager";
    public static final String ROLE_PRETTY_FULFILLMENT_OPERATOR = "Fulfillment Operator";
    
    @Id
    @NotNull
    private String userEmail;
    
    @NotNull
    private String firstName;
    
    @NotNull
    private String lastName;
    
    private String passwordHash;
    
    @NotNull
    private String userRole;
    
    private String city;
    
    private Timestamp passChangeDate;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp lastLogInTime;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    private Timestamp creationDate;
    
    private Boolean active;
   
    private Byte state;
    
    private String recoveryToken;
    
    @Column(name = "auth_token")
    private String token;
    
    public void generateRecoveryToken() {
        recoveryToken = UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * Roles - ADMIN, OPERATOR, SCHEME_LEADER, PURCHASE_MANAGER, FULFILLMENT_OPERATOR
     * Roles names can be found in User class as static final fields
     * @param roleName
     * @return
     */
    public boolean hasRole(String roleName) {
        if(roleName.equalsIgnoreCase(ROLE_ADMIN)) {
            return hasRole(0);
        } else if(roleName.equalsIgnoreCase(ROLE_OPERATOR)) {
            return hasRole(1);
        } else if(roleName.equalsIgnoreCase(ROLE_SCHEME_LEADER)) {
            return hasRole(2);
        } else if(roleName.equalsIgnoreCase(ROLE_PURCHASE_MANAGER)) {
            return hasRole(3);
        } else if(roleName.equalsIgnoreCase(ROLE_FULFILLMENT_OPERATOR)) {
            return hasRole(4);
        }
        
        return false;
    }
    
    public boolean hasRole(int roleId) {
        return userRole.length() > roleId * 2 && userRole.charAt(roleId * 2) == '1';
    }
    
    @Override
    public String toString() {
        return "User [userEmail=" + userEmail + ", firstName=" + firstName + ", lastName=" + lastName + ", passwordHash=" + passwordHash + ", userRole=" + userRole + ", city="
            + city + ", passChangeDate=" + passChangeDate + ", lastLogInTime=" + lastLogInTime + ", creationDate=" + creationDate + ", active=" + active + ", state=" + state
            + ", recoveryToken=" + recoveryToken + ", token=" + token + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, firstName, lastName, passwordHash, recoveryToken, state, userEmail, userRole);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        return Objects.equals(city, other.city) && Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName) && Objects.equals(passwordHash,
            other.passwordHash) && Objects.equals(recoveryToken, other.recoveryToken) && Objects.equals(state, other.state) && Objects.equals(userEmail, other.userEmail)
            && Objects.equals(userRole, other.userRole);
    }

    
    public Timestamp getLastLogInTime() {
        return lastLogInTime;
    }

    
    public void setLastLogInTime(Timestamp lastLogInTime) {
        this.lastLogInTime = lastLogInTime;
    }

    public String getToken() {
        return token;
    }

    
    public void setToken(String token) {
        this.token = token;
    }

    public String getRecoveryToken() {
        return recoveryToken;
    }
    
    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

    public Byte getState() {
        return state;
    }
    
    public void setState(Byte state) {
        this.state = state;
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
    
    public String getUserEmail() {
        return userEmail;
    }

    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    
    public String getPasswordHash() {
        return passwordHash;
    }

    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    
    public String getUserRole() {
        return userRole;
    }

    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    
    public String getCity() {
        return city;
    }

    
    public void setCity(String city) {
        this.city = city;
    }

    
    public Timestamp getPassChangeDate() {
        return passChangeDate;
    }

    
    public void setPassChangeDate(Timestamp passChangeDate) {
        this.passChangeDate = passChangeDate;
    }

    
    public Boolean getActive() {
        return active;
    }

    
    public void setActive(Boolean active) {
        this.active = active;
    }

    
    public Timestamp getCreationDate() {
        return creationDate;
    }

    
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toJSON() {
        return toJSONObject().toString();
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        jo.put("userEmail", userEmail);
        jo.put("firstName", firstName);
        jo.put("lastName", lastName);
        jo.put("passwordHash", passwordHash);
        jo.put("userRole", UserServiceImpl.collectAllRoleNames(this));
        jo.put("city", city);
        jo.put("passChangeDate", passChangeDate);
        jo.put("lastLogInTime", lastLogInTime);
        jo.put("creationDate", creationDate);
        jo.put("active", active);
        jo.put("state", state);
        return jo;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.state.equals(STATE_FIRST_LOGIN)) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_FIRSTLOGINNER"));
        }
        Set<TokenAuthentication.Role> authorities = new HashSet<>();

        for (TokenAuthentication.Role role : TokenAuthentication.Role.getAllRoles()) {
            if (this.hasRole(role.getRoleId())) {
                authorities.add(role);
            }
        }

        return authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.userEmail;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.active;
    }
}
