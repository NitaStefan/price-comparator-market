package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.models.Product;

import java.util.HashMap;
import java.util.Map;

public class ProductDao {
    private Map<String, Product> products;

    public ProductDao() {
        this.products = new HashMap<>();
    }

    public Map<String, Product> getAllProducts() {
        return products;
    }

    public void setAllProducts(Map<String, Product> products) {
        this.products = products;
    }

    public void addProduct(String id,Product product){
        products.put(id, product);
    }

}
