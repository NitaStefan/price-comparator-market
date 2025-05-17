package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.StoreCatalog;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StoreCatalogDao {

    private Map<ProductStoreDateKey, StoreCatalog> storeCatalogInfo;

    public StoreCatalogDao() {
        this.storeCatalogInfo = new HashMap<>();
    }

    public Map<ProductStoreDateKey, StoreCatalog> getAllStoreCatalogInfo() {
        return storeCatalogInfo;
    }

    public void setAllStoreCatalogInfo(Map<ProductStoreDateKey, StoreCatalog> storeCatalog) {
        this.storeCatalogInfo = storeCatalog;
    }

    public void addStoreCatalog(ProductStoreDateKey key, StoreCatalog storeCatalog) {
        storeCatalogInfo.put(key, storeCatalog);
    }

    public Map<String, LocalDate> getCurrentDateOfProductsPerStore(LocalDate currentDate) {
        Map<String, List<LocalDate>> datesByStore = storeCatalogInfo.keySet().stream()
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

    //productId -> StoreCatalog - price, currency
    public Map<String, StoreCatalog> getProductsForStoreAndDate(String storeName, LocalDate date) {
        return storeCatalogInfo.entrySet().stream()
                .filter(entry -> entry.getKey().storeName().equals(storeName))
                .filter(entry -> entry.getKey().date().equals(date))
                .collect(Collectors.toMap(
                        entry -> entry.getKey().productId(),
                        entry -> new StoreCatalog(entry.getValue().getPrice(), entry.getValue().getCurrency())
                ));
    }
}
