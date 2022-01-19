package com.beskyd.ms_control.business.usermanagement;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends UserDetailsService{

    List<User> findAll();

    User findOne(String userEmail);
    
    List<User> findByScheme(String schemeName);
    
    /**
     * Try to use constants from User class for roles names as {@code User.ROLE_ADMIN} etc.
     * @param roles
     * @return
     */
    List<User> findUsersByRoles(String... roles);

    User save(User savableUser);

    User insert(User insertableUser) throws UserAlreadyExistsException;

    void logOut(String token);

    String generatePassword(int length);

    @Transactional
    void deleteUser(String userEmail);

    User dropPassword(User user);

    Optional<User> findUserByRecoveryToken(String recoveryToken);

    User findUserByToken(String token);

    User findUserByToken_Checked(String token);

    void recoverPassword(User user, String newPassword);

    String signIn(String userEmail, String rawPassword);

    Boolean hasPermission(User user, String pageName);

    Set<String> getPermittedPagesByUser(User user);

    Map<String, List<String>> getPagePermissionsMap();
    
        
}
