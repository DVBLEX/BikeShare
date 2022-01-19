package com.beskyd.ms_control.business.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beskyd.ms_control.business.audit.OperationsLoggingService;
import com.beskyd.ms_control.business.general.MailService;
import com.beskyd.ms_control.business.general.SchemeResponseItem;
import com.beskyd.ms_control.business.general.SystemParametersService;
import com.beskyd.ms_control.business.requests.DeleteUserRequest;
import com.beskyd.ms_control.business.requests.RecoverPasswordRequest;
import com.beskyd.ms_control.business.requests.UserCredentialsRequest;
import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserAlreadyExistsException;
import com.beskyd.ms_control.business.usermanagement.UserDoesNotExistException;
import com.beskyd.ms_control.business.usermanagement.UserService;
import com.beskyd.ms_control.business.usermanagement.UserServiceImpl;

@RestController
@RequestMapping("/msc-api/user")
public class AuthorisationController {
    
    private final UserService userService;
    private final OperationsLoggingService loggingService;
    private final MailService mailService;
    private final SystemParametersService systemParamsService;
    
    @Inject
    public AuthorisationController(UserService userService, PasswordEncoder passwordEncoder, OperationsLoggingService loggingService, MailService mailService, SystemParametersService systemParamsService) {
        this.userService = userService;
        this.loggingService = loggingService;
        this.mailService = mailService;
        this.systemParamsService = systemParamsService;
    }
    
    @GetMapping("/all")
    public List<User> findAllUsers(){
        return userService.findAll();
    }
    
    @GetMapping("/{schemeName}")
    public List<User> findUsersByScheme(@PathVariable("schemeName") String schemeName){
        return userService.findByScheme(schemeName);
    }
    
    @PostMapping("")
    public void saveUser(@RequestBody User savableUser, Principal principal) {
        User oldUser = userService.findOne(savableUser.getUserEmail());
        
        if(oldUser == null) {
            throw new UserDoesNotExistException(savableUser.getUserEmail());
        }
        
        userService.save(savableUser);
        
        loggingService.pushLog(1106, principal.getName(), savableUser, oldUser, savableUser.getUserEmail(), "email");
    }
    
    @PostMapping("/add")
    public ResponseEntity<User> insertUser(@RequestBody User savableUser, Principal principal) {
        try{
            userService.insert(savableUser);
            
            User admin = userService.findOne(principal.getName());
            
            loggingService.pushLog(1104, admin.getUserEmail(), savableUser, null, savableUser.getUserEmail(), "email");
            
            mailService.sendSimpleMessage(savableUser.getUserEmail(), "New User Setup", "Hello " + savableUser.getFirstName() + ",\r\n" + 
                "\r\n" + 
                "We are sending this e-mail because a new '" + UserServiceImpl.collectAllRoleNames(savableUser) + "' user account was created for you on Maintenance and Stock Control.\r\n" + 
                "\r\n" + 
                "Please refer the link below to set up your password\r\n" + 
                systemParamsService.findParameterByName("domain_link") + "/msc/views/users/password-recovery/" + savableUser.getRecoveryToken() +  
                "\r\n" + 
                "Regards,\r\n" + 
                admin.getFirstName());
            
            return ResponseEntity.ok(savableUser);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/delete")
    public void deleteUser(@RequestBody DeleteUserRequest deleteRequest, Principal principal) {
        User oldUser = userService.findOne(deleteRequest.getUserEmail());
        
        if(oldUser == null) {
            throw new UserDoesNotExistException(deleteRequest.getUserEmail());
        }
        
        userService.deleteUser(deleteRequest.getUserEmail());
        
        loggingService.pushLog(1105, principal.getName(), null, oldUser, oldUser.getUserEmail(), "email");
    }
    
    @PutMapping("/drop-password")
    public User dropPassword(@RequestBody UserCredentialsRequest request, Principal principal) {
        User user = userService.findOne(request.getUserEmail());
        
        if(user == null || ((principal == null || principal.getName().equals(user.getUserEmail())) && !user.getActive())) {
            throw new UserDoesNotExistException(request.getUserEmail());
        }
        
        loggingService.pushLog(1107, user.getUserEmail(), null, user, user.getUserEmail(), "email");
        
        userService.dropPassword(user);
        
        mailService.sendSimpleMessage(user.getUserEmail(), "Password recovery link", "Hello " + user.getFirstName() + 
            "!\n\n Please, proceed by the link below to recover your password.\n\n"
            + systemParamsService.findParameterByName("domain_link") + "/msc/views/users/password-recovery/" + user.getRecoveryToken());
        
        return user;
    }
    
    @PutMapping("/recover-password")
    public void recoverPassword(@Valid @RequestBody RecoverPasswordRequest request) {
        Optional<User> optUser = userService.findUserByRecoveryToken(request.getToken());
        if(optUser.isEmpty()) {
            return;
        }
        
        userService.recoverPassword(optUser.get(), request.getNewPassword());
        
        loggingService.pushLog(1107, optUser.get().getUserEmail(), "Password dropped");
    }
    
    @PutMapping("/check-permission/{pageName}")
    public Boolean checkPermission(@PathVariable("pageName") String pageName, @RequestBody UserCredentialsRequest request) {
        User user = userService.findOne(request.getUserEmail());
        
        if(user == null) {
            throw new UserDoesNotExistException(request.getUserEmail());
        }
        
        return userService.hasPermission(user, pageName);
    }
    
    @GetMapping("/permitted-pages")
    public Set<String> getPermittedPagesByUser(Principal principal){
        return userService.getPermittedPagesByUser(userService.findOne(principal.getName()));
    }
    
    @GetMapping("/user-roles")
    public Integer[] getUserRoles(Principal principal) {
        User user = userService.findOne(principal.getName());
        
        Integer[] roles = new Integer[5];
        String[] rolesStr = user.getUserRole().split(",");
        
        for (int i = 0; i < rolesStr.length; i++) {
            roles[i] = Integer.valueOf(rolesStr[i]);
        }
        
        return roles;
    }
    
    @GetMapping("/user-scheme")
    public SchemeResponseItem getSchemeOfUser(Principal principal) {
        User user = userService.findOne(principal.getName());
        
        return new SchemeResponseItem(user.getCity());
    }
    
    @GetMapping("/username")
    public String getUsername(Principal principal) {
        return principal.getName();
    }

}
