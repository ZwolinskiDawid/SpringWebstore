package com.packt.webstore.domain.repository.impl;

import com.packt.webstore.domain.Product;
import com.packt.webstore.domain.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class InMemoryProductRepository implements ProductRepository {
	
    private List<Product> listOfProducts = new ArrayList<Product>();
    
    @Autowired
    private SessionFactory sessionFactory;

    public InMemoryProductRepository() {

    }

    @Override
    public Product getProductById(String productId) {
        Product productById = null;

        for(Product product : listOfProducts) {
            if(product!=null && product.getProductId() != null && 
                    product.getProductId().equals(productId)){
                productById = product;
                break;
            }
        }

        if(productById == null){
            throw new IllegalArgumentException("Brak produktu o wskazanym id: "+ productId);
        }

        return productById;
    }

    @Override
    public List<Product> getProductByCategory(String category) {
        
        List<Product> productsByCategory = new ArrayList<>();
        for(Product product : listOfProducts) {
            if(category.equalsIgnoreCase(product.getCategory()))
                productsByCategory.add(product);
        }
        
        return productsByCategory;        
    }

    @Override
    public List<Product> getAllProducts() {
        
        Session session = this.sessionFactory.getCurrentSession();
        NativeQuery query = session.createNativeQuery("select * from Product");
        query.addEntity(Product.class);
        return query.list();
    }

    @Override
    public List<Product> getProductsByManufacturer(String manufacturer) {
        
        List<Product> productsByBrand = new ArrayList<>();
        this.listOfProducts.stream().filter((product) -> {
            return product.getManufacturer().equalsIgnoreCase(manufacturer);
        }).forEachOrdered((product) -> {
            productsByBrand.add(product);
        });
        
        return productsByBrand;        
    }

    @Override
    public Set<Product> getProductsByFilter(Map<String, List<String>> filterParams, String category, String manufacturer) {
        
        Set<Product> productsByCategory = new HashSet<>();
        this.listOfProducts.stream().filter((product) -> {
            return product.getCategory().equalsIgnoreCase(category);
        }).forEachOrdered((product) -> {
            productsByCategory.add(product);
        });
        
        Set<Product> productsByCB = new HashSet<>();
        productsByCategory.stream().filter((product) -> {
            return product.getManufacturer().equalsIgnoreCase(manufacturer);
        }).forEachOrdered((product) -> {
            productsByCB.add(product);
        });
        
        Set<String> priceParams = filterParams.keySet();
        Set<BigDecimal> lowPrices = new HashSet<>();
        Set<BigDecimal> highPrices = new HashSet<>();
        
        if(priceParams.contains("low"))
            filterParams.get("low").stream().forEachOrdered((price) -> {
                lowPrices.add(BigDecimal.valueOf(Double.valueOf(price)));
            });
        if(priceParams.contains("high"))
            filterParams.get("high").stream().forEachOrdered((price) -> {
                highPrices.add(BigDecimal.valueOf(Double.valueOf(price)));
            });
        
        Optional<BigDecimal> low = lowPrices.stream().min((BigDecimal o1, BigDecimal o2) -> {
            return o1.compareTo(o2);
        });
        
        Optional<BigDecimal> high = highPrices.stream().max((BigDecimal o1, BigDecimal o2) -> {
            return o1.compareTo(o2);
        });
        
        Set<Product> filteredProducts = new HashSet<>();
        productsByCB.stream().filter((product) -> {
            return product.getUnitPrice().compareTo(low.orElse(BigDecimal.ZERO)) >= 0 &&
                    product.getUnitPrice().compareTo(high.orElse(product.getUnitPrice())) <= 0;
        }).forEachOrdered((product) -> {
            filteredProducts.add(product);
        });
        
        return filteredProducts;
    }

    @Override
    public void addProduct(Product product) {

        Session session = this.sessionFactory.getCurrentSession();
        session.save(product);
    }
}