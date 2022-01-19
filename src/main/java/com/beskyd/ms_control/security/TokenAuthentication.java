package com.beskyd.ms_control.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.beskyd.ms_control.business.usermanagement.User;

public class TokenAuthentication implements Authentication {
    private final String token;
    private final User user;

    public TokenAuthentication(final User user) {
      this.token = user.getToken();
      this.user = user;

    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<Role> getAuthorities() { 
      
      Set<Role> authorities = new HashSet<>();
      
      for(var role : Role.getAllRoles()) {
          if(user.hasRole(role.roleId)) {
              authorities.add(role);
          }
      }
      
      return authorities;
    }

    
    

    public static class Role implements GrantedAuthority{

        private final String roleName;
        
        private final String roleNamePretty;
        
        private final int roleId;
        
        public Role(String roleName, String roleNamePretty, int roleId) {
            this.roleName = roleName;
            this.roleId = roleId;
            this.roleNamePretty = roleNamePretty;
        }

        @Override
        public String getAuthority() {
            return "ROLE_" + roleName;
        }

        public static final Set<Role> getAllRoles(){
            var allRoles = new HashSet<Role>();
            
            allRoles.add(new Role(User.ROLE_ADMIN, User.ROLE_PRETTY_ADMIN, 0));
            allRoles.add(new Role(User.ROLE_OPERATOR, User.ROLE_PRETTY_OPERATOR, 1));
            allRoles.add(new Role(User.ROLE_SCHEME_LEADER, User.ROLE_PRETTY_SCHEME_LEADER, 2));
            allRoles.add(new Role(User.ROLE_PURCHASE_MANAGER, User.ROLE_PRETTY_PURCHASE_MANAGER, 3));
            allRoles.add(new Role(User.ROLE_FULFILLMENT_OPERATOR, User.ROLE_PRETTY_FULFILLMENT_OPERATOR, 4));
            
            return allRoles;
        }
        
        public String getRoleName() {
            return roleName;
        }
        
        public String getRoleNamePretty() {
            return roleNamePretty;
        }
        
        public int getRoleId() {
            return roleId;
        }

        @Override
        public String toString() {
            return this.getAuthority();
        }
    }
    
    @Override
    public Object getCredentials() {
      return token;
    }

    @Override
    public Object getDetails() {
      return getUser();
    }

    @Override
    public Object getPrincipal() {
      return getUser();
    }

    @Override
    public boolean isAuthenticated() {
      return true;
    }

    @Override
    public String getName() {
      return user.getUserEmail();
    }

    @Override
    public void setAuthenticated(boolean b)  {
    }
  }
