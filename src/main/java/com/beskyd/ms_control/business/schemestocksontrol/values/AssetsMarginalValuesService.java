package com.beskyd.ms_control.business.schemestocksontrol.values;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.general.Scheme;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class AssetsMarginalValuesService {

    private final AssetsMarginalValuesRepository repo;
    
    @Inject
    public AssetsMarginalValuesService(AssetsMarginalValuesRepository repo) {
        this.repo = repo;
    }
    
    public List<AssetsMarginalValues> findAll(){
        return repo.findAll(Sort.by("productType.groupName").ascending());
    }
    
    /**
     * Find AssetsMarginalValues by product type and scheme
     * @param productType
     * @param scheme
     * @return AssetsMarginalValues object if found, if not - null
     */
    public AssetsMarginalValues findByComplexId(TypeOfAssets productType, Scheme scheme) {
        return findByComplexId(productType.getId(), scheme.getName());
    }
    
    /**
     * Find AssetsMarginalValues by product id and scheme name
     * @param productTypeId
     * @param schemeName
     * @return AssetsMarginalValues object if found, if not - null
     */
    public AssetsMarginalValues findByComplexId(Integer productTypeId, String schemeName) {
        return repo.findById(new AssetsMarginalValues.AMVId(productTypeId, schemeName)).orElse(null);
    }
    
    public List<AssetsMarginalValues> findByScheme(Scheme scheme){
        return repo.findByScheme_Name(scheme.getName(), Sort.by("productType.typeName").ascending());
    }
    
    public void save(AssetsMarginalValues savable) {
        repo.save(savable);
    }
    
    public void deleteByComplexId(TypeOfAssets productType, Scheme scheme) {
        deleteByComplexId(productType.getId(), scheme.getName());
    }
    
    public void deleteByComplexId(Integer productTypeId, String schemeName) {
        repo.deleteById(new AssetsMarginalValues.AMVId(productTypeId, schemeName));
    }
    
    public void deleteByProductTypeId(Integer productTypeId) {
        repo.deleteByProductTypeId(productTypeId);
    }
    
}
