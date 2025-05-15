package com.nitastefan.pricecomparator.services;

import com.nitastefan.pricecomparator.dao.DiscountDao;
import com.nitastefan.pricecomparator.dao.ProductDao;
import com.nitastefan.pricecomparator.dao.StoreCatalogDao;

import java.time.LocalDate;

public class Service {
    private LocalDate currentDate;

    private final ProductDao productDao;

    private final StoreCatalogDao storeCatalogDao;

    private final DiscountDao discountDao;

    public Service(ProductDao productDao, StoreCatalogDao storeCatalogDao, DiscountDao discountDao) {
        this.currentDate = LocalDate.now();
        this.productDao = productDao;
        this.storeCatalogDao = storeCatalogDao;
        this.discountDao = discountDao;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }
}
