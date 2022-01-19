package com.beskyd.ms_control.business.general;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class SystemParametersService {

    private final SystemParametersRepository repo;

    public SystemParametersService(SystemParametersRepository repo) {
        this.repo = repo;
    }
    
    public String findParameterByName(String name) {
        return repo.findById(name).orElse(new SystemParameters(name, "")).getParameterValue();
    }
    
    public Map<String, String> findAllParameters(){
        return repo.findAll().stream().collect(Collectors.toMap(SystemParameters::getParameterName, SystemParameters::getParameterValue));
    }
    
    public void save(SystemParameters param) {
        repo.save(param);
    }
    
    public void  save(List<SystemParameters> params) {
        repo.saveAll(params);
    }
}
