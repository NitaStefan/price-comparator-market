package com.nitastefan.pricecomparator.models;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Store {
    private String name;
    private Map<LocalDate, List<Product>> productsByDate;
    private Map<LocalDate, Map<String, Discount>> discountsByDate;

    public Store(String name) {
        this.name = name;
        this.productsByDate = new TreeMap<>();
        this.discountsByDate = new TreeMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<LocalDate, List<Product>> getProductsByDate() {
        return productsByDate;
    }

    public void setProductsByDate(Map<LocalDate, List<Product>> productsByDate) {
        this.productsByDate = productsByDate;
    }

    public Map<LocalDate, Map<String, Discount>> getDiscountsByDate() {
        return discountsByDate;
    }

    public void setDiscountsByDate(Map<LocalDate, Map<String, Discount>> discountsByDate) {
        this.discountsByDate = discountsByDate;
    }

    public void addDiscounts(LocalDate date, Map<String, Discount> discounts) {
        discountsByDate.put(date, discounts);
    }

    public void addProducts(LocalDate date, List<Product> products) {
        productsByDate.put(date, products);
    }
}
