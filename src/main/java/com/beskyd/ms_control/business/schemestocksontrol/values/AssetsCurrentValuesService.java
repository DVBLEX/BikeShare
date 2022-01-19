package com.beskyd.ms_control.business.schemestocksontrol.values;

import com.beskyd.ms_control.business.assetsprofiles.TypeOfAssets;
import com.beskyd.ms_control.business.general.Scheme;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class AssetsCurrentValuesService {

    private final AssetsCurrentValuesRepository repo;
    
    @Inject
    public AssetsCurrentValuesService(AssetsCurrentValuesRepository repo) {
        this.repo = repo;
    }
    
    public List<AssetsCurrentValues> findAll(){
        return repo.findAll(Sort.by("productType.groupName").ascending());
    }
    
    /**
     * Find AssetsCurrentValues by product type and scheme
     * @param productType
     * @param scheme
     * @return AssetsCurrentValues object if found, if not - null
     */
    public AssetsCurrentValues findByComplexId(TypeOfAssets productType, Scheme scheme) {
        return findByComplexId(productType.getId(), scheme.getName());
    }
    
    /**
     * Find AssetsCurrentValues by product type id and scheme name
     * @param productTypeId
     * @param schemeName
     * @return AssetsCurrentValues object if found, if not - null
     */
    public AssetsCurrentValues findByComplexId(Integer productTypeId, String schemeName) {
        return repo.findById(new AssetsCurrentValues.ACVId(productTypeId, schemeName)).orElse(null);
    }
    /**
     * Reducing quantity of {@link AssetsCurrentValues} with passed {@code productTypeId} and {@code schemeName}
     * @param productTypeId
     * @param schemeName
     * @param minusQuantity should be positive
     */
    public void reduceQuantity(int productTypeId, String schemeName, int minusQuantity) {
        if(minusQuantity < 0) {
            throw new NumberFormatException("minus quantity should not be negative");
        }
        repo.reduce(minusQuantity, productTypeId, schemeName);
    }
    
    public List<AssetsCurrentValues> findByScheme(Scheme scheme){
        return repo.findBySchemeName(scheme.getName(), Sort.by("productType.typeName").ascending());
    }
    
    public List<AssetsCurrentValues> findByProductTypeId(Integer id){
        return repo.findByProductTypeId(id, Sort.by("productType.typeName").ascending());
    }
    
    public AssetsCurrentValues save(AssetsCurrentValues savable) {        
        return repo.save(savable);
    }
    
    public void deleteByComplexId(TypeOfAssets productType, Scheme scheme) {
        deleteByComplexId(productType.getId(), scheme.getName());
    }
    
    public void deleteByComplexId(Integer productTypeId, String schemeName) {
        repo.deleteById(new AssetsCurrentValues.ACVId(productTypeId, schemeName));
    }
    
    public void deleteByProductTypeId(Integer productTypeId) {
        repo.deleteByProductTypeId(productTypeId);
    }
    
    public List<AssetsCurrentValues> findLackingAssets(){
        return repo.findAssetsWithLowStock();
    }
}
