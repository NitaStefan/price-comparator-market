package com.nitastefan.pricecomparator.services;

import com.nitastefan.pricecomparator.dao.*;
import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        ProductDao productDao = new ProductDao();
        StoreCatalogDao catalogDao = new StoreCatalogDao();
        DiscountDao discountDao = new DiscountDao();

        service = new Service(productDao, catalogDao, discountDao);
        service.setCurrentDate(LocalDate.of(2025, 5, 10));

        productDao.addProduct("P001", new Product("Milk", "Dairy", "BrandA", 1.0f, "l"));
        productDao.addProduct("P002", new Product("Bread", "Bakery", "BrandB", 500f, "g"));
        productDao.addProduct("P003", new Product("Chicken Breast", "Meat", "Agricola", 1f, "kg"));
        productDao.addProduct("P004", new Product("Detergent", "Household", "Ariel", 2.5f, "l"));
        productDao.addProduct("P005", new Product("Orange Juice", "Beverages", "Cappy", 1f, "l"));

        catalogDao.addStoreCatalog(new ProductStoreDateKey("P001", "Kaufland", LocalDate.of(2025, 5, 10)), new StoreCatalog(6.0f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P002", "Lidl", LocalDate.of(2025, 5, 8)), new StoreCatalog(3.2f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P003", "Mega Image", LocalDate.of(2025, 5, 10)), new StoreCatalog(28.0f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P004", "Kaufland", LocalDate.of(2025, 5, 10)), new StoreCatalog(50.0f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P005", "Profi", LocalDate.of(2025, 5, 9)), new StoreCatalog(7.6f, "RON"));

        discountDao.addDiscount(
                new ProductStoreDateKey("P003", "Mega Image", LocalDate.of(2025, 5, 10)),
                new Discount(LocalDate.of(2025, 5, 9), LocalDate.of(2025, 5, 12), (byte) 12)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P004", "Kaufland", LocalDate.of(2025, 5, 10)),
                new Discount(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 16), (byte) 22)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P005", "Profi", LocalDate.of(2025, 5, 9)),
                new Discount(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5), (byte) 10) // expired
        );
    }


    @Test
    void getAvailableProductsByCategory_returnsCorrectlyGroupedProducts() {
        Map<String, Set<String>> result = service.getAvailableProductsByCategory();

        assertEquals(2, result.size());

        assertTrue(result.containsKey("Dairy"));
        assertTrue(result.containsKey("Bakery"));

        assertEquals(Set.of("Milk", "Cheese"), result.get("Dairy"));
        assertEquals(Set.of("Bread"), result.get("Bakery"));
    }

    @Test
    void getAvailableProductsByCategory_ignoresUnavailableDates() {
        // Change current date to before any product is available
        service.setCurrentDate(LocalDate.of(2025, 5, 1));

        Map<String, Set<String>> result = service.getAvailableProductsByCategory();
        assertTrue(result.isEmpty());
    }

    @Test
    void getAvailableProductsByCategory_withEmptyCatalog_returnsEmptyMap() {
        Service emptyService = new Service(new ProductDao(), new StoreCatalogDao(), new DiscountDao());
        emptyService.setCurrentDate(LocalDate.of(2025, 5, 10));

        Map<String, Set<String>> result = emptyService.getAvailableProductsByCategory();
        assertTrue(result.isEmpty());
    }
}
