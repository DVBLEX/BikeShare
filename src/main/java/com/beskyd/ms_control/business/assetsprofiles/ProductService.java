package com.beskyd.ms_control.business.assetsprofiles;

import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValuesService;

@Service
public class ProductService {

    private final ProductRepository repo;
    private final TypeOfAssetsService typesService;

    @Autowired
    private AssetsMarginalValuesService amvService;

    @Inject
    public ProductService(ProductRepository repo, TypeOfAssetsService typesService) {
        this.repo = repo;
        this.typesService = typesService;
    }

    public List<Product> findAll(){
        return repo.findAll(Sort.by("type.groupName"));
    }


    public  List<Product> findByTypeId(Integer typeId){
        return repo.findByType_Id(typeId);
    }

    public List<Product> findByPagesAndName(int pageNum, int pageSize, String name){
        return repo.filterByName(name, PageRequest.of(pageNum, pageSize, Sort.by("type.groupName")));
    }


    /**
     * Find {@link Product} by id
     * @param id {@link Product} id
     * @return {@link Product} object if found, null - if not
     */
    public Product findById(Integer id) {
      return repo.findById(id).orElse(null);
    }

    public Long getCount(String nameFilter) {
        return repo.countByNameFilter(nameFilter);
    }

    /**
     * If product name is {@code null}, sets it as empty string
     * @param product
     * @return
     * @throws AddingExistingProductException
     */
    public Product save(Product product) throws AddingExistingProductException {
        if(product.getProductId().getProductName() == null) {
            product.getProductId().setProductName("");
        }
        if (product.getDeliveryTime() == null) {
            product.setDeliveryTime(0);
        }
        if(product.getId() == null && !repo.findByType_GroupNameAndProductId_ProductNameAndProductId_Supplier_Id(product.getType().getGroupName(),
            product.getProductId().getProductName(),
            product.getProductId().getSupplier().getId())
            .isEmpty()) {
            throw new AddingExistingProductException(product);
        }

        return repo.save(product);
    }

    /**
     * Deletes {@link Product} by id and {@link TypeOfAssets} of this product, if it was
     * the last product attached to this type.
     *
     * 99% you will get an exception here, that foreign key restricts product from deleting.
     * It will be like this, until we will see the whole system's logic and deal with it.
     * @param id {@link Product} id
     */
    public void deleteById(Integer id) {

        Integer typeId = findById(id).getType().getId();

        repo.deleteById(id);

        if(findByTypeId(typeId).isEmpty()) {
            typesService.deleteById(typeId);
        }
    }



}
