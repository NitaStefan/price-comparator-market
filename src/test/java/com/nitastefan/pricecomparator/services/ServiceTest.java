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

        productDao.addProduct("P001", new Product("iaurt grecesc", "lactate", "Olympus", 1.0f, "l"));
        productDao.addProduct("P002", new Product("biscuiți cu unt", "gustări", "Milka", 500f, "g"));
        productDao.addProduct("P003", new Product("spaghetti nr.5", "paste făinoase", "Barilla", 500f, "g"));
        productDao.addProduct("P004", new Product("ulei floarea-soarelui", "alimente de bază", "Unisol", 600f, "ml"));
        productDao.addProduct("P005", new Product("suc portocale", "băuturi", "Cappy", 330f, "ml"));
        productDao.addProduct("P006", new Product("vin alb demisec", "băuturi", "Recas", 0.75f, "l"));
        productDao.addProduct("P007", new Product("ciocolată neagră 70%", "gustări", "Heidi", 100f, "g"));
        productDao.addProduct("P008", new Product("cartofi albi", "legume și fructe", "Generic", 1f, "kg"));
        productDao.addProduct("P009", new Product("ceapă galbenă", "legume și fructe", "Generic", 1f, "kg"));
        productDao.addProduct("P010", new Product("morcovi", "legume și fructe", "Generic", 0.5f, "kg"));
        productDao.addProduct("P011", new Product("suc portocale", "băuturi", "Santorini", 1f, "l"));
        productDao.addProduct("P012", new Product("cașcaval", "lactate", "K-Classic", 0.25f, "kg"));


        catalogDao.addStoreCatalog(new ProductStoreDateKey("P001", "kaufland", LocalDate.of(2025, 5, 12)), new StoreCatalog(9.1f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P003", "kaufland", LocalDate.of(2025, 5, 12)), new StoreCatalog(5.9f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P004", "kaufland", LocalDate.of(2025, 5, 14)), new StoreCatalog(5.9f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P007", "kaufland", LocalDate.of(2025, 5, 14)), new StoreCatalog(4.2f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P010", "kaufland", LocalDate.of(2025, 5, 14)), new StoreCatalog(2.5f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P012", "kaufland", LocalDate.of(2025, 5, 20)), new StoreCatalog(14.8f, "RON"));

        catalogDao.addStoreCatalog(new ProductStoreDateKey("P002", "lidl", LocalDate.of(2025, 5, 8)), new StoreCatalog(24.3f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P006", "lidl", LocalDate.of(2025, 5, 8)), new StoreCatalog(24.1f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P011", "lidl", LocalDate.of(2025, 5, 14)), new StoreCatalog(7.6f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P008", "lidl", LocalDate.of(2025, 5, 17)), new StoreCatalog(3.1f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P010", "lidl", LocalDate.of(2025, 5, 17)), new StoreCatalog(2.4f, "RON"));

        catalogDao.addStoreCatalog(new ProductStoreDateKey("P003", "mega_image", LocalDate.of(2025, 4, 28)), new StoreCatalog(6.2f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P009", "mega_image", LocalDate.of(2025, 5, 11)), new StoreCatalog(2.95f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P008", "mega_image", LocalDate.of(2025, 5, 10)), new StoreCatalog(3.25f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P009", "mega_image", LocalDate.of(2025, 5, 10)), new StoreCatalog(3.05f, "RON"));

        catalogDao.addStoreCatalog(new ProductStoreDateKey("P005", "profi", LocalDate.of(2025, 5, 9)), new StoreCatalog(7f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P006", "profi", LocalDate.of(2025, 5, 10)), new StoreCatalog(23.5f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P007", "profi", LocalDate.of(2025, 5, 10)), new StoreCatalog(4.15f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P009", "profi", LocalDate.of(2025, 5, 21)), new StoreCatalog(2.45f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P011", "profi", LocalDate.of(2025, 5, 21)), new StoreCatalog(7.25f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P012", "profi", LocalDate.of(2025, 5, 21)), new StoreCatalog(15.0f, "RON"));


        discountDao.addDiscount(
                new ProductStoreDateKey("P003", "kaufland", LocalDate.of(2025, 5, 10)),
                new Discount(LocalDate.of(2025, 5, 11), LocalDate.of(2025, 5, 13), (byte) 22)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P007", "kaufland", LocalDate.of(2025, 5, 15)),
                new Discount(LocalDate.of(2025, 5, 14), LocalDate.of(2025, 5, 16), (byte) 30)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P004", "kaufland", LocalDate.of(2025, 5, 15)),
                new Discount(LocalDate.of(2025, 5, 16), LocalDate.of(2025, 5, 16), (byte) 40)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P010", "kaufland", LocalDate.of(2025, 5, 15)),
                new Discount(LocalDate.of(2025, 5, 17), LocalDate.of(2025, 5, 22), (byte) 10)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P012", "kaufland", LocalDate.of(2025, 5, 21)),
                new Discount(LocalDate.of(2025, 5, 21), LocalDate.of(2025, 5, 25), (byte) 15)
        );


        // check the rest of the discounts from here
        discountDao.addDiscount(
                new ProductStoreDateKey("P008", "lidl", LocalDate.of(2025, 5, 17)),
                new Discount(LocalDate.of(2025, 5, 9), LocalDate.of(2025, 5, 11), (byte) 6)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P002", "lidl", LocalDate.of(2025, 5, 8)),
                new Discount(LocalDate.of(2025, 5, 15), LocalDate.of(2025, 5, 20), (byte) 10)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P006", "lidl", LocalDate.of(2025, 5, 8)),
                new Discount(LocalDate.of(2025, 4, 25), LocalDate.of(2025, 5, 5), (byte) 12)
        );

        discountDao.addDiscount(
                new ProductStoreDateKey("P003", "mega_image", LocalDate.of(2025, 5, 10)),
                new Discount(LocalDate.of(2025, 5, 9), LocalDate.of(2025, 5, 12), (byte) 12)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P009", "mega_image", LocalDate.of(2025, 5, 10)),
                new Discount(LocalDate.of(2025, 4, 30), LocalDate.of(2025, 5, 7), (byte) 5)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P008", "mega_image", LocalDate.of(2025, 5, 10)),
                new Discount(LocalDate.of(2025, 5, 9), LocalDate.of(2025, 5, 12), (byte) 8)
        );

        discountDao.addDiscount(
                new ProductStoreDateKey("P005", "profi", LocalDate.of(2025, 5, 9)),
                new Discount(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5), (byte) 10)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P006", "profi", LocalDate.of(2025, 5, 10)),
                new Discount(LocalDate.of(2025, 5, 9), LocalDate.of(2025, 5, 12), (byte) 10)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P012", "profi", LocalDate.of(2025, 5, 21)),
                new Discount(LocalDate.of(2025, 5, 22), LocalDate.of(2025, 5, 30), (byte) 20)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P011", "profi", LocalDate.of(2025, 5, 21)),
                new Discount(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 20), (byte) 1)
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
