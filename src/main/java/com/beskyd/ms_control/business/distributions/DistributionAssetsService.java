package com.beskyd.ms_control.business.distributions;

import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

@Service
public class DistributionAssetsService {
    
    private final DistributionAssetsRepository repo;
    
    @Inject
    public DistributionAssetsService(DistributionAssetsRepository repo) {
        this.repo = repo;
    }
    
    /**
     * Returns object of {@link DistributionAssets} if found by complex id, or null - if not
     * @param distributionId
     * @param typeOfAssetsId
     * @return
     */
    public DistributionAssets findByComplexId(int distributionId, int typeOfAssetsId) {
        return repo.findById(new DistributionAssets.ComplexId(distributionId, typeOfAssetsId)).orElse(null);
    }
    
    public DistributionAssets save(DistributionAssets savable) {
        return repo.save(savable);
    }
    
    public void saveAll(Set<DistributionAssets> savables) {
        repo.saveAll(savables);
    }
}
