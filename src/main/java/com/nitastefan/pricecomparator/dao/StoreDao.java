package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.models.Store;

import java.util.HashMap;
import java.util.Map;

public class StoreDao {
    private Map<String, Store> stores;

    public StoreDao() {
        stores = new HashMap<>();
    }

    public Map<String, Store> getStores() {
        return stores;
    }

    public void setStores(Map<String, Store> stores) {
        this.stores = stores;
    }

    public void addStore(Store store) {
        if (stores.containsKey(store.getName()))
            throw new IllegalArgumentException("Store with name '" + store.getName() + "' already exists. Cannot add duplicate store.");

        stores.put(store.getName(), store);
    }

    public Store getStoreByName(String name) {
        return stores.get(name);
    }

    public boolean isStoreRegistered(String name) {
        return stores.containsKey(name);
    }


}
