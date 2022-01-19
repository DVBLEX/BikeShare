package com.beskyd.ms_control.user;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserRepository;
import com.beskyd.ms_control.business.usermanagement.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
@Rollback
public class UserFunctionalityTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Test
    public void testPermissions() {
        User tempUser = new User();
        tempUser.setActive(true);
        tempUser.setCity("Dublin");
        tempUser.setFirstName("James");
        tempUser.setLastName("Bond");
        tempUser.setUserEmail("test@temp.com");
        tempUser.setPasswordHash(passwordEncoder.encode("qwerty"));
        tempUser.setUserRole("0,0,1,1,0");
        tempUser.setState(User.STATE_FIRST_LOGIN);
        
        userRepository.save(tempUser);
        
        assertTrue(userService.hasPermission(tempUser, "assets-edit"));
        assertFalse(userService.hasPermission(tempUser, "user-list"));
        
        tempUser.setUserRole("1");
        
        userRepository.save(tempUser);
        
        assertTrue(userService.hasPermission(tempUser, "purchase-orders"));
        assertTrue(userService.hasPermission(tempUser, "assets-edit"));
        assertTrue(userService.hasPermission(tempUser, "user-list"));
    }
}
