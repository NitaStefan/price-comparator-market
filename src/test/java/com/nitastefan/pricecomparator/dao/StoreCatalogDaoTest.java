package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.StoreCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StoreCatalogDaoTest {

    private StoreCatalogDao storeCatalogDao;

    @BeforeEach
    void setUp() {
        storeCatalogDao = new StoreCatalogDao();
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P001", "Kaufland", LocalDate.of(2025, 5, 1)), new StoreCatalog(10.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P002", "Kaufland", LocalDate.of(2025, 5, 8)), new StoreCatalog(15.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P003", "Kaufland", LocalDate.of(2025, 5, 11)), new StoreCatalog(20.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P004", "Lidl", LocalDate.of(2025, 5, 3)), new StoreCatalog(8.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P005", "Lidl", LocalDate.of(2025, 5, 4)), new StoreCatalog(9.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P006", "Profi", LocalDate.of(2025, 5, 5)), new StoreCatalog(12.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P007", "Profi", LocalDate.of(2025, 5, 5)), new StoreCatalog(11.0f, "RON"));
        storeCatalogDao.addStoreCatalog(new ProductStoreDateKey("P008", "MegaImage", LocalDate.of(2025, 5, 20)), new StoreCatalog(25.0f, "RON"));
    }

    @Test
    void givenCurrentDate_whenComputingAvailableDates_thenReturnsCorrectDates() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 5, 10);

        // When
        Map<String, LocalDate> result = storeCatalogDao.getCurrentDateOfProductsPerStore(currentDate);

        // Then
        assertEquals(LocalDate.of(2025, 5, 8), result.get("Kaufland"));
        assertEquals(LocalDate.of(2025, 5, 4), result.get("Lidl"));
        assertEquals(LocalDate.of(2025, 5, 5), result.get("Profi"));
    }

    @Test
    void givenEmptyDao_whenComputingAvailableDates_thenReturnsEmptyMap() {
        // Given
        StoreCatalogDao emptyStoreCatalogDao = new StoreCatalogDao();
        LocalDate currentDate = LocalDate.of(2025, 5, 10);

        // When
        Map<String, LocalDate> result = emptyStoreCatalogDao.getCurrentDateOfProductsPerStore(currentDate);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void givenFutureDate_whenComputingAvailableDates_thenReturnsLatestDates() {
        // Given
        LocalDate futureDate = LocalDate.of(2025, 5, 25);

        // When
        Map<String, LocalDate> result = storeCatalogDao.getCurrentDateOfProductsPerStore(futureDate);

        // Then
        assertEquals(LocalDate.of(2025, 5, 11), result.get("Kaufland"));
        assertEquals(LocalDate.of(2025, 5, 4), result.get("Lidl"));
        assertEquals(LocalDate.of(2025, 5, 5), result.get("Profi"));
        assertEquals(LocalDate.of(2025, 5, 20), result.get("MegaImage"));
    }

    @Test
    void givenPastDate_whenComputingAvailableDates_thenReturnsEmptyMap() {
        // Given
        LocalDate pastDate = LocalDate.of(2025, 4, 30);

        // When
        Map<String, LocalDate> result = storeCatalogDao.getCurrentDateOfProductsPerStore(pastDate);

        // Then
        assertTrue(result.isEmpty());
    }

}
