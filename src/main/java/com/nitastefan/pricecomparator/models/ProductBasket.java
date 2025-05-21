package com.nitastefan.pricecomparator.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductBasket {

    Set<String> productNames;

    public ProductBasket() {
        this.productNames = new HashSet<>();
    }


    public void establishBasket(List<String> productNames) {
        this.productNames.clear();
        this.productNames.addAll(productNames);
    }

    public boolean contains(String productName){
        return productNames.contains(productName);
    }

    public Set<String> getProductNames() {
        return productNames;
    }
}
