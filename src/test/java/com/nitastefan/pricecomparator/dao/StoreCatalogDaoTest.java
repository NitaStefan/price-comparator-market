package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.StoreCatalog;
import com.nitastefan.pricecomparator.models.StoreDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoreCatalogDaoTest {

    private StoreCatalogDao storeCatalogDao;

    @BeforeEach
    void setUp() {
        storeCatalogDao = new StoreCatalogDao();
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P001", "kaufland", LocalDate.of(2025, 5, 1)), new StoreCatalog(10.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P002", "kaufland", LocalDate.of(2025, 5, 8)), new StoreCatalog(15.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P003", "kaufland", LocalDate.of(2025, 5, 11)), new StoreCatalog(20.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P004", "lidl", LocalDate.of(2025, 5, 3)), new StoreCatalog(8.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P005", "lidl", LocalDate.of(2025, 5, 4)), new StoreCatalog(9.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P006", "profi", LocalDate.of(2025, 5, 5)), new StoreCatalog(12.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P007", "profi", LocalDate.of(2025, 5, 5)), new StoreCatalog(11.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P008", "mega_image", LocalDate.of(2025, 5, 20)), new StoreCatalog(25.0f, "RON"));
    }

    @Test
    void givenCurrentDate_whenGettingAvailableProductKeys_thenReturnsMostRecentPerStore() {
        LocalDate currentDate = LocalDate.of(2025, 5, 11);

        List<ProductStoreDateKey> result = storeCatalogDao.getAvailableCatalogKeys(currentDate);

        assertEquals(4, result.size());
        assertTrue(result.contains(new ProductStoreDateKey("P003", "kaufland", LocalDate.of(2025, 5, 11))));
        assertTrue(result.contains(new ProductStoreDateKey("P005", "lidl", LocalDate.of(2025, 5, 4))));
        assertTrue(result.contains(new ProductStoreDateKey("P006", "profi", LocalDate.of(2025, 5, 5))) ||
                result.contains(new ProductStoreDateKey("P007", "profi", LocalDate.of(2025, 5, 5))));
    }

    @Test
    void givenFutureDate_whenGettingAvailableProductKeys_thenReturnsAllLatestPerStore() {
        LocalDate currentDate = LocalDate.of(2025, 5, 30);

        List<ProductStoreDateKey> result = storeCatalogDao.getAvailableCatalogKeys(currentDate);

        assertEquals(5, result.size());
        assertTrue(result.contains(new ProductStoreDateKey("P003", "kaufland", LocalDate.of(2025, 5, 11))));
        assertTrue(result.contains(new ProductStoreDateKey("P005", "lidl", LocalDate.of(2025, 5, 4))));
        assertTrue(result.contains(new ProductStoreDateKey("P008", "mega_image", LocalDate.of(2025, 5, 20))));
    }

    @Test
    void givenPastDate_whenGettingAvailableProductKeys_thenReturnsEmptyList() {
        LocalDate currentDate = LocalDate.of(2025, 4, 30);

        List<ProductStoreDateKey> result = storeCatalogDao.getAvailableCatalogKeys(currentDate);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenEmptyCatalog_whenGettingAvailableProductKeys_thenReturnsEmptyList() {
        StoreCatalogDao emptyDao = new StoreCatalogDao();
        LocalDate currentDate = LocalDate.of(2025, 5, 10);

        List<ProductStoreDateKey> result = emptyDao.getAvailableCatalogKeys(currentDate);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenValidStoreDate_whenGettingCatalogKeys_thenReturnsCorrectKeys() {
        StoreDate storeDate = new StoreDate("kaufland", LocalDate.of(2025, 5, 8));

        List<ProductStoreDateKey> result = storeCatalogDao.getCatalogKeysForStoreDate(storeDate);

        assertEquals(1, result.size());
        assertEquals("P002", result.getFirst().productId());
    }

    @Test
    void givenInvalidStoreDate_whenGettingCatalogKeys_thenReturnsEmptyList() {
        StoreDate storeDate = new StoreDate("lidl", LocalDate.of(2024, 5, 5));

        List<ProductStoreDateKey> result = storeCatalogDao.getCatalogKeysForStoreDate(storeDate);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenMultipleProductsSameStoreAndDate_whenGettingCatalogKeys_thenReturnsAllMatching() {
        StoreDate storeDate = new StoreDate("profi", LocalDate.of(2025, 5, 5));

        List<ProductStoreDateKey> result = storeCatalogDao.getCatalogKeysForStoreDate(storeDate);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(key -> key.productId().equals("P006")));
        assertTrue(result.stream().anyMatch(key -> key.productId().equals("P007")));
    }
}
