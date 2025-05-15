package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.Discount;

import java.util.Comparator;
import java.util.TreeMap;

public class DiscountDao {

    private TreeMap<ProductStoreDateKey, Discount> discounts;

    public DiscountDao() {
        this.discounts = new TreeMap<>(Comparator.comparing(ProductStoreDateKey::date));
    }

    public TreeMap<ProductStoreDateKey, Discount> getAllDiscounts() {
        return discounts;
    }

    public void setAllDiscounts(TreeMap<ProductStoreDateKey, Discount> discounts) {
        this.discounts = discounts;
    }

    public void addDiscount(ProductStoreDateKey key, Discount discount) {
        discounts.put(key, discount);
    }
}
