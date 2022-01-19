package com.beskyd.ms_control.assets;

import com.beskyd.ms_control.AppInitializer;
import com.beskyd.ms_control.business.assetsprofiles.*;
import com.beskyd.ms_control.business.requests.SaveProductRequest;
import com.beskyd.ms_control.business.suppliers.Supplier;
import com.beskyd.ms_control.business.suppliers.SupplierService;
import com.beskyd.ms_control.config.PersistenceJPAConfig;
import com.beskyd.ms_control.config.WebMvcConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppInitializer.class, PersistenceJPAConfig.class, WebMvcConfig.class })
@WebAppConfiguration
@Rollback
public class ProductTests {

    @Autowired
    private WebApplicationContext wac;
    
    private MockMvc mockMvc;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private SupplierService supplierService;

    @Autowired
    private TypeOfAssetsService typeOfAssetsService;
    
    @Before 
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    @Test
    public void saveDeleteTest() throws AddingExistingProductException {
        List<Supplier> suppliers = supplierService.findAll();
        if(suppliers.isEmpty()) {
            Assert.fail("There are no suppliers in the database");
        }
        List<TypeOfAssets> types = typeOfAssetsService.findAll();
        if(types.isEmpty()) {
            Assert.fail("There are no asset types in the database");
        }
        
//        Product testProduct = new Product("test_ chain zz", new BigDecimal("50.23"), suppliers.get(0), types.get(0), 20);
        Product testProduct = new Product("test_ chain zz", suppliers.get(0), types.get(0), 20, 0);

        List<Product> products = productService.findAll();
        int size1 = products.size();        
        
        productService.save(testProduct);
        
        Assert.assertNotNull(testProduct.getId());
        
        if(productService.findById(testProduct.getId()) == null) {
            Assert.fail("Can't find product by alias");
        }
        
        products = productService.findAll();
        int size2 = products.size();
        
        //Check if product was added
        Assert.assertTrue(size2 - size1 == 1);
        
        productService.deleteById(testProduct.getId());
        
        products = productService.findAll();
        
        //Check if product was deleted
        Assert.assertTrue(size2 - products.size() == 1);
    }
    
    @Test
    public void saveDeleteWithNewType_MVC_Test() throws Exception {
        List<Supplier> suppliers = supplierService.findAll();
        if(suppliers.isEmpty()) {
            Assert.fail("There are no suppliers in the database");
        }
        
        Product testProduct = new Product("HQ UP 009", suppliers.get(0), new TypeOfAssets("Car Assets", "Doors", "Red"), 20, 0);
        SaveProductRequest request = new SaveProductRequest(testProduct, null);
        
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/msc-api/assets/product").cookie(new Cookie("token", "ttkkyy")).content(request.toJSON()).contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
        .contentType("application/json;charset=UTF-8")).andReturn();
        
        String response = result.getResponse().getContentAsString();

        Gson gsonResult = new GsonBuilder().create();
        
        testProduct = gsonResult.fromJson(response, Product.class);
        
        Assert.assertNotNull(testProduct.getType());
        Assert.assertNotNull(testProduct.getType().getId());
        
        
        Product testProductTwo = new Product("Z UP 98", suppliers.get(0), testProduct.getType(), 20, 0);
        request = new SaveProductRequest(testProductTwo, null);
        
        result = this.mockMvc.perform(MockMvcRequestBuilders.post("/msc-api/assets/product").cookie(new Cookie("token", "ttkkyy")).content(request.toJSON()).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
            .contentType("application/json;charset=UTF-8")).andReturn();
        
        response = result.getResponse().getContentAsString();
        testProductTwo = gsonResult.fromJson(response, Product.class);
        Assert.assertNotNull(testProductTwo.getType());
        Assert.assertNotNull(testProductTwo.getType().getId());
        Assert.assertEquals(testProduct.getType().getId(), testProductTwo.getType().getId());
        
        
        
        String typeResponse = this.mockMvc.perform(MockMvcRequestBuilders.get("/msc-api/assets/types-of-assets/" + testProduct.getType().getId()).cookie(new Cookie("token", "ttkkyy")).contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
        .contentType("application/json;charset=UTF-8")).andReturn().getResponse().getContentAsString();
        
        TypeOfAssets returnedType = gsonResult.fromJson(typeResponse, TypeOfAssets.class);
        
        Assert.assertNotNull(returnedType);
        Assert.assertEquals(2, returnedType.getProducts().size());
        
        //delete one of the products
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/msc-api/assets/product/" + testProduct.getId()).cookie(new Cookie("token", "ttkkyy")).contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(MockMvcResultMatchers.status().isOk());
        
        
        //check again
        typeResponse = this.mockMvc.perform(MockMvcRequestBuilders.get("/msc-api/assets/types-of-assets/" + testProductTwo.getType().getId()).cookie(new Cookie("token", "ttkkyy")).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content()
            .contentType("application/json;charset=UTF-8")).andReturn().getResponse().getContentAsString();
            
        returnedType = gsonResult.fromJson(typeResponse, TypeOfAssets.class);
            
        Assert.assertNotNull(returnedType);
        Assert.assertEquals(1, returnedType.getProducts().size());
        
        
        
      //delete other product
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/msc-api/assets/product/" + testProductTwo.getId()).cookie(new Cookie("token", "ttkkyy")).contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(MockMvcResultMatchers.status().isOk());
        
        
        
      //and, check again
        typeResponse = this.mockMvc.perform(MockMvcRequestBuilders.get("/msc-api/assets/types-of-assets/" + returnedType.getId()).cookie(new Cookie("token", "ttkkyy")).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
            
        returnedType = gsonResult.fromJson(typeResponse, TypeOfAssets.class);
            
        Assert.assertNull(returnedType);
    }
    
//    //This test is kind of obsolete, since complex id was removed in favor of single column auto increment id
//    @Test
//    public void saveOrInsertTestThroughHttp() throws Exception {
//        if(this.mockMvc == null) {
//            Assert.fail("mockMvc is null");
//        }
//        
//        List<Product> products = productService.findAll();
//        if(products.isEmpty()) {
//            Assert.fail("There are no products in the database");
//        }
//        
//        Product oldProduct = products.get(0);
//        Product savableProduct = new Product("changed _test", new BigDecimal("20.50"), oldProduct.getProductId().getSupplier(), oldProduct.getType());
//        
//        SaveProductRequest request = new SaveProductRequest(savableProduct, oldProduct);
//        
//        this.mockMvc.perform(MockMvcRequestBuilders.post("/msc-api/assets/product").content(request.toJSON()).contentType(MediaType.APPLICATION_JSON_UTF8))
//        .andExpect(MockMvcResultMatchers.status().isOk());
//        
//        if(productService.findById(oldProduct.getId()) != null || productService.findById(savableProduct.getId()) == null) {
//            Assert.fail("record was not updated");
//        }
//    }
}
