package com.beskyd.ms_control.business.general;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

@Service
public class SchemeService {
    
    private final SchemeRepository repo;

    @Inject
    public SchemeService(SchemeRepository repo) {
        this.repo = repo;
    }

    public List<Scheme> findAll(){
        return repo.findAll();
    }
}
