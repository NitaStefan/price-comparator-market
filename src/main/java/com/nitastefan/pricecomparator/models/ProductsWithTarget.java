package com.nitastefan.pricecomparator.models;

import com.nitastefan.pricecomparator.dto.ProductTargetsRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsWithTarget {

    private Map<String, ProductPricing> products;

    public ProductsWithTarget() {
        products = new HashMap<>();
    }

    public Map<String, ProductPricing> getProducts() {
        return products;
    }

    public void setProducts(Map<String, ProductPricing> products) {
        this.products = products;
    }

    public boolean contains(String productName) {
        return products.containsKey(productName);
    }

    public ProductPricing getPricing(String productName) {
        return products.get(productName);
    }

    public void establishTargets(List<ProductTargetsRequest> targets) {
        products.clear();

        for (ProductTargetsRequest target : targets) {
            products.put(target.getProductName(), new ProductPricing(target.getPrice(), target.getQuantity(), target.getUnit()));
        }
    }
}
