package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.StoreCatalog;

import java.util.HashMap;
import java.util.Map;

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
}
