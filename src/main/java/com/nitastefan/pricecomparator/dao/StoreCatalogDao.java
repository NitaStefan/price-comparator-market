package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.StoreCatalog;

import java.util.Comparator;
import java.util.TreeMap;

public class StoreCatalogDao {

    private TreeMap<ProductStoreDateKey, StoreCatalog> storeCatalogInfo;

    public StoreCatalogDao() {
        this.storeCatalogInfo = new TreeMap<>(Comparator.comparing(ProductStoreDateKey::date));
    }

    public TreeMap<ProductStoreDateKey, StoreCatalog> getAllStoreCatalogInfo() {
        return storeCatalogInfo;
    }

    public void setAllStoreCatalogInfo(TreeMap<ProductStoreDateKey, StoreCatalog> storeCatalog) {
        this.storeCatalogInfo = storeCatalog;
    }

    public void addStoreCatalog(ProductStoreDateKey key, StoreCatalog storeCatalog) {
        storeCatalogInfo.put(key, storeCatalog);
    }
}
