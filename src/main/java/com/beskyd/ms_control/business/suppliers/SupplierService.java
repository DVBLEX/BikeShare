package com.beskyd.ms_control.business.suppliers;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SupplierService {
    
    private final SupplierRepository repo;

    @Inject
    public SupplierService(SupplierRepository repo) {
        this.repo = repo;
    }
    
    public List<Supplier> findAll(){
        return repo.findAll(Sort.by("name").ascending());
    }
    
    /**
     * Find {@link Supplier} by id
     * @param id. If id == null, returns null
     * @return object, if present, null - if not
     */
    public Supplier findById(Integer id) {
        if(id == null) {
            return null;
        }
        return repo.findById(id).orElse(null);
    }
    
    public Supplier save(Supplier supplier) {
        return repo.save(supplier);
    }
    
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
