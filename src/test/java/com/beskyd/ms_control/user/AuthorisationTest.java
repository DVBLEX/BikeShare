package com.beskyd.ms_control.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
import com.beskyd.ms_control.business.requests.UserCredentialsRequest;
import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
@Rollback
public class AuthorisationTest {

    @Autowired
    private WebApplicationContext wac;
    
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    @Before 
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    
    @Test
    public void testMockMvcSetup() {
        ServletContext servletContext = wac.getServletContext();
         
        assertNotNull(servletContext);
        assertTrue(servletContext instanceof MockServletContext);
        assertNotNull(wac.getBean("authorisationController"));
    }
    
//    @Test
//    public void testSignInActivePassword() throws Exception {        
//        User tempUser = new User();
//        tempUser.setActive(true);
//        tempUser.setCity("Dublin");
//        tempUser.setFirstName("James");
//        tempUser.setLastName("Bond");
//        tempUser.setUserEmail("test@temp.com");
//        tempUser.setPasswordHash(passwordEncoder.encode("qwerty"));
//        tempUser.setUserRole("0,0,1,1,0");
//        tempUser.setState(User.STATE_ACTIVE_PASSWORD);
//        
//        userRepository.save(tempUser);
//        
//        UserCredentialsRequest request = new UserCredentialsRequest(tempUser.getUserEmail(), "qwerty");
//        
//        this.mockMvc.perform(MockMvcRequestBuilders.put("/login").content(request.toJSON()).contentType(MediaType.APPLICATION_JSON_UTF8))
//        .andExpect(MockMvcResultMatchers.status().isOk());
//        
//        userRepository.delete(tempUser);
//    }
//    
//    @Test
//    public void testSignInFirstLogin() throws Exception {
//        User tempUser = new User();
//        tempUser.setActive(true);
//        tempUser.setCity("Dublin");
//        tempUser.setFirstName("James");
//        tempUser.setLastName("Bond");
//        tempUser.setUserEmail("test@temp.com");
//        tempUser.setPasswordHash(passwordEncoder.encode("qwerty"));
//        tempUser.setUserRole("0,0,1,1,0");
//        tempUser.setState(User.STATE_FIRST_LOGIN);
//        
//        userRepository.save(tempUser);
//        
//        UserCredentialsRequest request = new UserCredentialsRequest(tempUser.getUserEmail(), "qwerty");
//        
//        this.mockMvc.perform(MockMvcRequestBuilders.put("/login").content(request.toJSON()).contentType(MediaType.APPLICATION_JSON_UTF8))
//        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
//        .contentType("application/json;charset=UTF-8"))
//        .andExpect(MockMvcResultMatchers.jsonPath("$.passwordRecoveryToken").exists());
//        
//        userRepository.delete(tempUser);
//    }
    
}
