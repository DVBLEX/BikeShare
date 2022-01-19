package com.beskyd.ms_control.operationsLogging;

import java.util.List;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
import com.beskyd.ms_control.business.audit.OperationsLoggingService;
import com.beskyd.ms_control.business.audit.SystemOperationDoesNotExistException;
import com.beskyd.ms_control.business.audit.SystemOperations;
import com.beskyd.ms_control.config.addLogic.JsonAware;
import com.beskyd.ms_control.business.usermanagement.UserDoesNotExistException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
public class OperationsLoggingTests {

    @Autowired
    private OperationsLoggingService operLoggingService;
    
    
    @Test
    public void checkIfThereAreSystemOperations() {
        if(operLoggingService.findAllSystemOperations().isEmpty()) {
            Assert.fail("There are no system operations in database!");
        }
    }
    
    @Test
    public void testGetActionGroups() {
        List<String> actionGroups = operLoggingService.getSystemOperationsGroupList();
        List<SystemOperations> allSystemOperations = operLoggingService.findAllSystemOperations();
        
        Assert.assertTrue("if there are at leaset one system operation, action groups can't have a size of 0", allSystemOperations.isEmpty() ? true : !actionGroups.isEmpty());
        Assert.assertTrue("action groupds size can't be bigger than all system operations", actionGroups.size() <= allSystemOperations.size());
    }
    
    @Test
    public void testPushLogWithBadUser() {
        boolean catched = false;
        
        try{
            operLoggingService.pushLog(1100, "bad@email.com", Mockito.mock(JsonAware.class), null, "3");
        } catch (UserDoesNotExistException e) {
            catched = true;
        } catch (Exception e) {
            
        }
        
        Assert.assertTrue("UserDoesNotExistException were not thrown", catched);
    }
    
    @Test
    public void testPushLogWithBadOperation() {
        boolean catched = false;
        
        try{
            operLoggingService.pushLog(999999, "nazar@telclic.net", Mockito.mock(JsonAware.class), null, "3");
        } catch (SystemOperationDoesNotExistException e) {
            catched = true;
        } catch (Exception e) {
            
        }
        
        Assert.assertTrue("SystemOperationDoesNotExistException were not thrown", catched);
    }
    
    @Test
    public void testPutTwoNullValues() {
        boolean catched = false;
        
        try{
            operLoggingService.pushLog(1100, "nazar@telclic.net", null, null, "3");
        } catch (JSONException e) {
            catched = true;
        } catch (Exception e) {
            
        }
        
        Assert.assertFalse("JSONException were not thrown", catched);
    }
    
    @Test
    public void testSuccessUserLogin() throws Exception {
        Integer id = operLoggingService.pushLog(1100, "nazar@telclic.net", "successful login"); 
        
        Assert.assertNotNull(operLoggingService.findLogById(id));
    }
    
    /**
     * Just test, if there are no exceptions
     */
    @Test
    public void lightTestOnFindByPages() {
        operLoggingService.findByPages(0, 10);
    }
}
