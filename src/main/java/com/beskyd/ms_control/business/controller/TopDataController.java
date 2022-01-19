package com.beskyd.ms_control.business.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beskyd.ms_control.business.general.Scheme;
import com.beskyd.ms_control.business.general.SchemeResponseItem;
import com.beskyd.ms_control.business.general.SchemeService;
import com.beskyd.ms_control.business.general.StatesRepository;
import com.beskyd.ms_control.business.general.StatesResponse;
import com.beskyd.ms_control.business.general.SystemParametersResponse;
import com.beskyd.ms_control.business.general.SystemParametersService;
import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserCredentialsResponse;
import com.beskyd.ms_control.business.usermanagement.UserService;

@RestController
@RequestMapping("/msc-api/top-data")
public class TopDataController {

    private final SchemeService schemeService;
    private final StatesRepository statesRepo;
    private final UserService userService;
    private final SystemParametersService systemParamsService;
    
    @Inject
    public TopDataController(SchemeService schemeService, StatesRepository statesRepo, UserService userService, SystemParametersService systemParamsService) {
        this.schemeService = schemeService;
        this.statesRepo = statesRepo;
        this.userService = userService;
        this.systemParamsService = systemParamsService;
    }

    @GetMapping("/schemes")
    public List<SchemeResponseItem> getAllSchemes(){
        List<Scheme> schemes = schemeService.findAll();
        List<SchemeResponseItem> responseList = new ArrayList<>();
        
        schemes.stream().forEach(s -> responseList.add(new SchemeResponseItem(s.getName())));
        
        return responseList;
    }
    
    @GetMapping("/states/stock-requests")
    public List<StatesResponse> getStatesOfStockRequests(){
        return StatesResponse.createListFrom(statesRepo.findByType(1));
    }
    
    @GetMapping("/states/purchase-orders")
    public List<StatesResponse> getStatesOfPurchaseOrders(){
        return StatesResponse.createListFrom(statesRepo.findByType(2));
    }
    
    @GetMapping("/states/distribution")
    public List<StatesResponse> getStatesOfDistribution(){
        return StatesResponse.createListFrom(statesRepo.findByType(3));
    }
    
    @GetMapping("/get-user-creds")
    public UserCredentialsResponse getUserCredentials(Principal principal) {
        User user = userService.findOne(principal.getName());

        return new UserCredentialsResponse(user.getFirstName(), user.getLastName(), user.getUserEmail());
    }
    
    @GetMapping("/system-parameters")
    public Map<String, String> getAllParametersAsMap(){
        return systemParamsService.findAllParameters();
    }
    
    @GetMapping("/system-parameters/{name}")
    public String getParameterByName(@PathVariable("name") String name) {
        return systemParamsService.findParameterByName(name);
    }
    
    @PostMapping("/system-parameters")
    public void saveParameter(@RequestBody SystemParametersResponse response) {
        systemParamsService.save(response.toOriginal());
    }
    
    @PostMapping("/system-parameters/list")
    public void saveParametersFromList(@RequestBody List<SystemParametersResponse> responses) {
        systemParamsService.save(SystemParametersResponse.toOriginals(responses));
    }
    
}
