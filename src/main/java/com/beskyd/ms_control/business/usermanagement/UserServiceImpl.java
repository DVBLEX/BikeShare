package com.beskyd.ms_control.business.usermanagement;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beskyd.ms_control.security.TokenAuthentication;
import com.beskyd.ms_control.security.TokenAuthentication.Role;

@Service
public class UserServiceImpl implements UserService{
    
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private Map<String, List<String>> pagePermissions = new HashMap<>();
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Inject
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        
        constructPagesPermissionsMap();
    }
    
    public List<User> findAll(){
        return userRepository.findAll();
    }
    
    @Override
    public List<User> findByScheme(String schemeName) {
        return userRepository.findByCity(schemeName);
    }
    
    @Override
    public List<User> findUsersByRoles(String... roles){
        List<User> allUsers = userRepository.findAll();
        
        List<User> usersWithRole = new ArrayList<>();
        
        for (User user : allUsers) {
            for (int i = 0; i < roles.length; i++) {
                if(user.hasRole(roles[i])) {
                    usersWithRole.add(user);
                    
                    break;
                }
            }
        }
        
        return usersWithRole;
        
    }
    
    /**
     * find user in data base
     * @param userEmail
     * @return {@link User}, if present, or null
     */
    public User findOne(String userEmail) {
        return userRepository.findById(userEmail).orElse(null);
    }
    
    /**
     * Should not be used to save new user, because this user can end up with wrong state or password
     * @param savableUser {@link User} to save
     * @return
     */
    public User save(User savableUser) {
        return userRepository.save(savableUser);
    }
    
    /**
     * Adds new user. Sets it's state to 3 (need password change) and generates recovery token
     * @param insertableUser - {@link User} to add
     * @return inserted user
     * @throws UserAlreadyExistsException 
     */
    public User insert(User insertableUser) throws UserAlreadyExistsException {
        insertableUser.setPasswordHash(passwordEncoder.encode("1111".subSequence(0, 4)));
        insertableUser.setState(User.STATE_FIRST_LOGIN);
        insertableUser.setPassChangeDate(Timestamp.valueOf(LocalDateTime.now()));
        insertableUser.generateRecoveryToken();
        insertableUser.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
        
        if(userRepository.findById(insertableUser.getUserEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        
        save(insertableUser);
        
        return insertableUser;
    }
    
    /**
     * Sets {@link User}'s token to null and saves him
     * @param token authentication token
     */
    public void logOut(String token) {
        User user = findUserByToken_Checked(token);
        user.setToken(null);
        save(user);
    }
    
    /**
     * 
     * @param length - length of the generated password
     * @return generated password
     */
    public String generatePassword(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }
    
    @Transactional
    public void deleteUser(String userEmail) {
        userRepository.deleteById(userEmail);
    }
    
    /**
     * Generates new password with length 20 for user, sets its State to  STATE_NEED_PASSWORD_CHANGE 
     * and generates a recovery token
     * @param user - {@link User}, for which password is dropped
     * @return saved user
     */
    public User dropPassword(User user) {
        user.setPasswordHash(passwordEncoder.encode(generatePassword(20)));
        user.setState(User.STATE_NEED_PASSWORD_CHANGE);
        user.generateRecoveryToken();
        user.setPassChangeDate(Timestamp.valueOf(LocalDateTime.now()));
        
        return save(user);
    }
    
    public Optional<User> findUserByRecoveryToken(String recoveryToken) {
        return userRepository.findByRecoveryToken(recoveryToken);
    }
    
    public User findUserByToken(String token) {
        return userRepository.findByToken(token).orElse(null);
    }
    
    /**
     * Find user by {@code token}. If user was not found, throws {@link UserDoesNotExistException}
     * @param token security token
     * @return
     * @throws UserDoesNotExistException
     */
    public User findUserByToken_Checked(String token) {
        User user = findUserByToken(token);
        
        if(user == null) {
            throw new UserDoesNotExistException();
        }
        
        return user;
    }
    
    /**
     * Sets newPassword as new user password, changes user's state to STATE_ACTIVE_PASSWORD
     * and sets recovery token as null
     * @param user - {@link User}, for which password is recovered
     * @param newPassword - new password for user
     */
    public void recoverPassword(User user, String newPassword) {
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setState(User.STATE_ACTIVE_PASSWORD);
        user.setRecoveryToken(null);
        user.setPassChangeDate(Timestamp.valueOf(LocalDateTime.now()));
        
        userRepository.save(user);
    }
    
    /**
     * If user with this userEmail is present, returns empty string.
     * If user logins the first time, returns generated password recovery token
     * Also, saves generated token to user in data base
     * @param userEmail
     * @param rawPassword
     * @return
     */
    public String signIn(String userEmail, String rawPassword) {
        var optionalUser = userRepository.findById(userEmail);
        if(optionalUser.isEmpty()) {
            return null;
        }
        if(optionalUser.get().getState().equals(User.STATE_FIRST_LOGIN)) {
            optionalUser.get().setRecoveryToken(UUID.randomUUID().toString().replace("-", ""));
            save(optionalUser.get());
            return optionalUser.get().getRecoveryToken();
        }
        if(passwordEncoder.matches(rawPassword, optionalUser.get().getPasswordHash())) {
            userRepository.updateUserLogInDatetime(Timestamp.valueOf(LocalDateTime.now()), userEmail);
           
            return "";
        }
        
        return null;
    }    
    
    /**
     * Does {@code user} have permission for page
     * @param user
     * @param pageName
     * @return
     */
    public Boolean hasPermission(User user, String pageName) {
        TokenAuthentication tokenAuth = new TokenAuthentication(user);
        Collection<Role> authorities = tokenAuth.getAuthorities();
        
        for (Role role : authorities) {
            List<String> permittedPages = pagePermissions.get(role.getRoleName());
            
            if(role.getRoleName().equalsIgnoreCase(User.ROLE_ADMIN)) {
                return true;
            }
            
            for(String pn : permittedPages) {
                if(pn.equalsIgnoreCase(pageName)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public Set<String> getPermittedPagesByUser(User user){
        TokenAuthentication tokenAuth = new TokenAuthentication(user);
        Collection<Role> authorities = tokenAuth.getAuthorities();
        
        Set<String> permittedPages = new HashSet<>();
        
        for (Role role : authorities) {
            if(role.getRoleName().equalsIgnoreCase(User.ROLE_ADMIN)) {
                permittedPages.add("all");
                
                continue;
            }

            permittedPages.addAll(pagePermissions.get(role.getAuthority()));
        }
        
        return permittedPages;
    }
    
    /**
     * Returns roles on {@code String} like 'Admin, Fulfillment Operator, Scheme Leader'
     * @param user
     * @return
     */
    public static String collectAllRoleNames(User user) {
        if(user.getUserRole() == null || user.getUserRole().length() == 0) {
            return "";
        }
        TokenAuthentication tokenAuth = new TokenAuthentication(user);
        Collection<Role> authorities = tokenAuth.getAuthorities();
        
        StringBuilder roles = new StringBuilder();
        
        for (Role role : authorities) {
           roles.append(role.getRoleNamePretty()).append(", ");
        }
        roles.delete(roles.length() - 2, roles.length());
        
        return roles.toString();
    }
    
    public Map<String, List<String>> getPagePermissionsMap(){
        return pagePermissions;
    }
    
    private void constructPagesPermissionsMap() {
        
        final String rolePrefix = "ROLE_";
        
        List<String> operatorPages = new ArrayList<>();
        operatorPages.add("assets-report");
        operatorPages.add("create-repair-report");
        operatorPages.add("repair-reports");
        operatorPages.add("repair-history");
        operatorPages.add("routine-review");
                
        pagePermissions.put(User.ROLE_OPERATOR, operatorPages); //Role index = 1 (0,1,0,0,0)
        pagePermissions.put(rolePrefix + User.ROLE_OPERATOR, operatorPages);
        
        List<String> schemeLeaderPages = new ArrayList<>();
        schemeLeaderPages.add("assets-report");
        schemeLeaderPages.add("stock-requests");
        schemeLeaderPages.add("stock-balance");
        schemeLeaderPages.add("distribution");
        schemeLeaderPages.add("repair-reports");
        schemeLeaderPages.add("repair-history");
        
        pagePermissions.put(User.ROLE_SCHEME_LEADER, schemeLeaderPages); //Role index = 2 (0,0,1,0,0)
        pagePermissions.put(rolePrefix + User.ROLE_SCHEME_LEADER, schemeLeaderPages);
        
        List<String> purchaseManagerPages = new ArrayList<>();
        purchaseManagerPages.add("assets-edit");
        purchaseManagerPages.add("scheme-stock-control");
        purchaseManagerPages.add("stock-requests");
        purchaseManagerPages.add("purchase-orders");
        purchaseManagerPages.add("suppliers");
        purchaseManagerPages.add("distribution");
        
        pagePermissions.put(User.ROLE_PURCHASE_MANAGER, purchaseManagerPages); //Role index = 3 (0,0,0,1,0)
        pagePermissions.put(rolePrefix + User.ROLE_PURCHASE_MANAGER, purchaseManagerPages);
        
        List<String> fulfillmentOperatorPages = new ArrayList<>();
        fulfillmentOperatorPages.add("assets-edit");
        fulfillmentOperatorPages.add("scheme-stock-control");
        fulfillmentOperatorPages.add("stock-requests");
        fulfillmentOperatorPages.add("purchase-orders");
        fulfillmentOperatorPages.add("suppliers");
        fulfillmentOperatorPages.add("distribution");
        
        pagePermissions.put(User.ROLE_FULFILLMENT_OPERATOR, fulfillmentOperatorPages); //Role index = 4 (0,0,0,0,1)
        pagePermissions.put(rolePrefix + User.ROLE_FULFILLMENT_OPERATOR, fulfillmentOperatorPages);
    }

    @Override
    public UserDetails loadUserByUsername(String username)  {
        User mscUser = findOne(username);
        if(mscUser == null || !mscUser.isEnabled()) {
            throw new UsernameNotFoundException("user with email " + username + "does not exist");
        }

        if (!mscUser.isAccountNonLocked()) {
            String token = UUID.randomUUID().toString().replace("-", "");
            mscUser.setRecoveryToken(token);
            userRepository.setRecoveryToken(username, token);
            SecurityContextHolder.getContext().setAuthentication(new TokenAuthentication(mscUser));
        } else {
            userRepository.updateUserLogInDatetime(Timestamp.valueOf(LocalDateTime.now()), mscUser.getUserEmail());
        }
        return mscUser;
    }
    
}
