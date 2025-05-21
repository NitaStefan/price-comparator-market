package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.Discount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DiscountDaoTest {

    private DiscountDao discountDao;

    @BeforeEach
    void setUp() {
        discountDao = new DiscountDao();
        discountDao.addDiscount(new ProductStoreDateKey("P001", "Kaufland", LocalDate.of(2025, 5, 1)), new Discount(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 7), (byte) 10));
        discountDao.addDiscount(new ProductStoreDateKey("P002", "Kaufland", LocalDate.of(2025, 5, 8)), new Discount(LocalDate.of(2025, 5, 8), LocalDate.of(2025, 5, 14), (byte) 15));
        discountDao.addDiscount(new ProductStoreDateKey("P003", "Kaufland", LocalDate.of(2025, 5, 11)), new Discount(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 16), (byte) 20));
        discountDao.addDiscount(new ProductStoreDateKey("P004", "Lidl", LocalDate.of(2025, 5, 3)), new Discount(LocalDate.of(2025, 5, 3), LocalDate.of(2025, 5, 9), (byte) 10));
        discountDao.addDiscount(new ProductStoreDateKey("P005", "Lidl", LocalDate.of(2025, 5, 4)), new Discount(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 12), (byte) 8));
        discountDao.addDiscount(new ProductStoreDateKey("P006", "Profi", LocalDate.of(2025, 5, 5)), new Discount(LocalDate.of(2025, 5, 4), LocalDate.of(2025, 5, 10), (byte) 15));
        discountDao.addDiscount(new ProductStoreDateKey("P007", "Profi", LocalDate.of(2025, 5, 5)), new Discount(LocalDate.of(2025, 5, 6), LocalDate.of(2025, 5, 8), (byte) 30));
        discountDao.addDiscount(new ProductStoreDateKey("P008", "MegaImage", LocalDate.of(2025, 5, 20)), new Discount(LocalDate.of(2025, 5, 20), LocalDate.of(2025, 5, 30), (byte) 25));
    }

    @Test
    void givenCurrentDate_whenComputingAvailableDates_thenReturnsCorrectDates() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 5, 10);

        // When
        Map<String, LocalDate> result = discountDao.getAvailableDiscountDatePerStore(currentDate);

        // Then
        assertEquals(LocalDate.of(2025, 5, 8), result.get("Kaufland"));
        assertEquals(LocalDate.of(2025, 5, 4), result.get("Lidl"));
        assertEquals(LocalDate.of(2025, 5, 5), result.get("Profi"));
    }

    @Test
    void givenEmptyDao_whenComputingAvailableDates_thenReturnsEmptyMap() {
        // Given
        DiscountDao emptyDiscountDao = new DiscountDao();
        LocalDate currentDate = LocalDate.of(2025, 5, 10);

        // When
        Map<String, LocalDate> result = emptyDiscountDao.getAvailableDiscountDatePerStore(currentDate);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void givenFutureDate_whenComputingAvailableDates_thenReturnsLatestDates() {
        // Given
        LocalDate futureDate = LocalDate.of(2025, 5, 25);

        // When
        Map<String, LocalDate> result = discountDao.getAvailableDiscountDatePerStore(futureDate);

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
        Map<String, LocalDate> result = discountDao.getAvailableDiscountDatePerStore(pastDate);

        // Then
        assertTrue(result.isEmpty());
    }


    @Test
    void givenCurrentDate_whenGettingAvailableDiscountKeys_thenReturnsCorrectKeys() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 5, 10);

        // When
        List<ProductStoreDateKey> result = discountDao.getAvailableDiscountKeys(currentDate);

        // Then
        List<String> expectedKeys = List.of("P002-Kaufland-2025-05-08", "P005-Lidl-2025-05-04", "P006-Profi-2025-05-05", "P007-Profi-2025-05-05");
        List<String> actualKeys = result.stream()
                .map(key -> String.format("%s-%s-%s", key.productId(), key.storeName(), key.date()))
                .toList();

        assertEquals(expectedKeys.size(), actualKeys.size());
        assertTrue(actualKeys.containsAll(expectedKeys));
    }

    @Test
    void givenPastDate_whenGettingAvailableDiscountKeys_thenReturnsEmptyList() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 4, 30);

        // When
        List<ProductStoreDateKey> result = discountDao.getAvailableDiscountKeys(currentDate);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void givenFutureDate_whenGettingAvailableDiscountKeys_thenReturnsAllLatestPerStore() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 5, 21);

        // When
        List<ProductStoreDateKey> result = discountDao.getAvailableDiscountKeys(currentDate);

        // Then
        List<String> expectedKeys = List.of("P003-Kaufland-2025-05-11", "P005-Lidl-2025-05-04",
                "P006-Profi-2025-05-05", "P007-Profi-2025-05-05", "P008-MegaImage-2025-05-20");

        List<String> actualKeys = result.stream()
                .map(key -> String.format("%s-%s-%s", key.productId(), key.storeName(), key.date()))
                .toList();

        assertEquals(expectedKeys.size(), actualKeys.size());
        assertTrue(actualKeys.containsAll(expectedKeys));
    }

    @Test
    void givenEmptyDao_whenGettingAvailableDiscountKeys_thenReturnsEmptyList() {
        // Given
        DiscountDao emptyDiscountDao = new DiscountDao();
        LocalDate currentDate = LocalDate.of(2025, 5, 10);

        // When
        List<ProductStoreDateKey> result = emptyDiscountDao.getAvailableDiscountKeys(currentDate);

        // Then
        assertTrue(result.isEmpty());
    }
}
