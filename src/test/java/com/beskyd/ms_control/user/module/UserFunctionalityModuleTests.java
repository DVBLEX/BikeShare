package com.beskyd.ms_control.user.module;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.beskyd.ms_control.business.usermanagement.UserRepository;
import com.beskyd.ms_control.business.usermanagement.UserService;
import com.beskyd.ms_control.business.usermanagement.UserServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class UserFunctionalityModuleTests {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Test
    public void testGeneratePassword() {
        UserService userService = new UserServiceImpl(userRepository, passwordEncoder);
        
        int length = 10;
        String password = userService.generatePassword(length);
        
        Assert.assertNotNull(password);
        Assert.assertEquals("passwords should have the requested length", length, password.length());
        
    }

}
