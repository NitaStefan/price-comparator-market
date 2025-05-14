package com.nitastefan.pricecomparator.services;

import com.nitastefan.pricecomparator.dao.StoreDao;
import com.nitastefan.pricecomparator.models.Product;
import com.nitastefan.pricecomparator.models.Store;

import java.time.LocalDate;
import java.util.List;

public class StoreService {

    private final StoreDao storeDao;

    private LocalDate currentDate;

    public StoreService(StoreDao storeDao) {
        this.storeDao = storeDao;
        this.currentDate = LocalDate.now();
    }

    public StoreDao getStoreDao() {
        return storeDao;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public Store getStoreByName(String name) {
        return storeDao.getStoreByName(name);
    }

    public List<Product> getAvailableProductsFromStore(String storeName) {
        Store store = getStoreByName(storeName);
        return store.getCurrentlyAvailableProducts(currentDate);
    }
}
