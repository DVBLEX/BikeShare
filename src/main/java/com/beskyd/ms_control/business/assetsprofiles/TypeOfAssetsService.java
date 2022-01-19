package com.beskyd.ms_control.business.assetsprofiles;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TypeOfAssetsService {

    private final TypeOfAssetsRepository repo;
    
    @PersistenceContext
    private EntityManager em;

    @Inject
    public TypeOfAssetsService(TypeOfAssetsRepository repo) {
        this.repo = repo;
    }
    
    public List<TypeOfAssets> findAll(){
        return repo.findAll(Sort.by("groupName"));
    }
    
    /**
     * Find {@link TypeOfAssets} by id
     * @param id {@link TypeOfAssets}'s id
     * @return {@link TypeOfAssets} object if present, null - if not
     */
    public TypeOfAssets findById(int id) {
        return repo.findById(id).orElse(null);        
    }
    
    public TypeOfAssets findByName(String name){
        return repo.findByGroupName(name);
    }
    
    public TypeOfAssets save(TypeOfAssets type) {
        return repo.save(type);
    }
    
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
    
    public void deleteByName(String name) {
        repo.deleteByTypeName(name);
    }
    
    /**
     * Get all groups of assets types by selecting distinct values from asset_group column of table types_of_assets
     * @return list of type groups
     */
    public List<String> getAllTypeGroups() {
        return em.createNativeQuery("select distinct asset_group from types_of_assets").getResultList();
    }
    
    
}
