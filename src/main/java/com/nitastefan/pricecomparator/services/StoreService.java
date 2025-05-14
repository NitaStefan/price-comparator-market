package com.nitastefan.pricecomparator.services;

import com.nitastefan.pricecomparator.dao.StoreDao;
import com.nitastefan.pricecomparator.models.Store;

public class StoreService {

    private final StoreDao storeDao;

    public StoreService(StoreDao storeDao) {
        this.storeDao = storeDao;
    }

    public Store getStoreByName(String name) {
        return storeDao.getStoreByName(name);
    }
}
