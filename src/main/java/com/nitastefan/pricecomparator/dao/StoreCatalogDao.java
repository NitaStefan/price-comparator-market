package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.StoreCatalog;
import com.nitastefan.pricecomparator.models.StoreDate;

import java.time.LocalDate;
import java.util.*;
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

    public Map<String, TreeSet<LocalDate>> getSortedDatesByStore() {

        return storeCatalogInfo.keySet().stream()
                .collect(Collectors.groupingBy(
                        ProductStoreDateKey::storeName,
                        Collectors.mapping(ProductStoreDateKey::date, Collectors.toCollection(TreeSet::new))
                ));
    }

    public List<ProductStoreDateKey> getAvailableCatalogKeys(LocalDate currentDate) {
        Map<String, Set<LocalDate>> datesByStore = storeCatalogInfo.keySet().stream()
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

        return storeCatalogInfo.keySet().stream()
                .filter(key -> latestAvailableDates.contains(new StoreDate(key.storeName(), key.date())))
                .toList();
    }

    public List<ProductStoreDateKey> getCatalogKeysForStoreDate(StoreDate storeDate) {
        return storeCatalogInfo.keySet().stream()
                .filter(key -> Objects.equals(key.storeName(), storeDate.store()) && Objects.equals(key.date(), storeDate.date()))
                .toList();
    }

    public StoreCatalog getStoreCatalog(ProductStoreDateKey key) {
        return storeCatalogInfo.get(key);
    }
}
