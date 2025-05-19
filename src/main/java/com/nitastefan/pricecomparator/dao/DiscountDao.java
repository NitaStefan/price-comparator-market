package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.Discount;
import com.nitastefan.pricecomparator.models.StoreDate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DiscountDao {

    private Map<ProductStoreDateKey, Discount> discounts;

    public DiscountDao() {
        this.discounts = new HashMap<>();
    }

    public Map<ProductStoreDateKey, Discount> getAllDiscounts() {
        return discounts;
    }

    public void setAllDiscounts(Map<ProductStoreDateKey, Discount> discounts) {
        this.discounts = discounts;
    }

    public void addDiscount(ProductStoreDateKey key, Discount discount) {
        discounts.put(key, discount);
    }

    public Map<String, LocalDate> getAvailableDiscountDatePerStore(LocalDate currentDate) {
        Map<String, List<LocalDate>> datesByStore = discounts.keySet().stream()
                .collect(Collectors.groupingBy(
                        ProductStoreDateKey::storeName,
                        Collectors.mapping(ProductStoreDateKey::date, Collectors.toList())
                ));

        Map<String, LocalDate> latestAvailableDates = new HashMap<>();
        datesByStore.forEach((store, dates) -> {
            LocalDate latestDate = LocalDate.MIN;

            for (LocalDate date : dates)
                if (!date.isAfter(currentDate) && date.isAfter(latestDate))
                    latestDate = date;

            if (!latestDate.equals(LocalDate.MIN))
                latestAvailableDates.put(store, latestDate);
        });

        return latestAvailableDates;
    }

    public List<ProductStoreDateKey> getAvailableDiscountKeys(LocalDate currentDate) {
        Map<String, Set<LocalDate>> datesByStore = discounts.keySet().stream()
                .collect(Collectors.groupingBy(
                        ProductStoreDateKey::storeName,
                        Collectors.mapping(ProductStoreDateKey::date, Collectors.toSet())
                ));

        Set<StoreDate> latestAvailableDates = new HashSet<>();

        datesByStore.forEach((store, dates) -> {
            LocalDate latestDate = LocalDate.MIN;

            for (LocalDate date : dates)
                if (!date.isAfter(currentDate) && date.isAfter(latestDate))
                    latestDate = date;

            if (!latestDate.equals(LocalDate.MIN))
                latestAvailableDates.add(new StoreDate(store, latestDate));
        });

        return discounts.keySet().stream()
                .filter(key -> latestAvailableDates.contains(new StoreDate(key.storeName(), key.date())))
                .collect(Collectors.toList());
    }

    public Discount getDiscount(ProductStoreDateKey key) {
        return discounts.get(key);
    }
}
