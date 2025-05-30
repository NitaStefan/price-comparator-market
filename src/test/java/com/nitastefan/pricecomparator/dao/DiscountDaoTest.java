package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.Discount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountDaoTest {

    private DiscountDao discountDao;

    @BeforeEach
    void setUp() {
        discountDao = new DiscountDao();
        discountDao.addDiscount(new ProductStoreDateKey("P001", "kaufland", LocalDate.of(2025, 5, 1)), new Discount(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 7), (byte) 10));
        discountDao.addDiscount(new ProductStoreDateKey("P002", "kaufland", LocalDate.of(2025, 5, 8)), new Discount(LocalDate.of(2025, 5, 8), LocalDate.of(2025, 5, 14), (byte) 15));
        discountDao.addDiscount(new ProductStoreDateKey("P003", "kaufland", LocalDate.of(2025, 5, 11)), new Discount(LocalDate.of(2025, 5, 10), LocalDate.of(2025, 5, 16), (byte) 20));
        discountDao.addDiscount(new ProductStoreDateKey("P004", "lidl", LocalDate.of(2025, 5, 3)), new Discount(LocalDate.of(2025, 5, 3), LocalDate.of(2025, 5, 9), (byte) 10));
        discountDao.addDiscount(new ProductStoreDateKey("P005", "lidl", LocalDate.of(2025, 5, 4)), new Discount(LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 12), (byte) 8));
        discountDao.addDiscount(new ProductStoreDateKey("P006", "profi", LocalDate.of(2025, 5, 5)), new Discount(LocalDate.of(2025, 5, 4), LocalDate.of(2025, 5, 10), (byte) 15));
        discountDao.addDiscount(new ProductStoreDateKey("P007", "profi", LocalDate.of(2025, 5, 5)), new Discount(LocalDate.of(2025, 5, 6), LocalDate.of(2025, 5, 8), (byte) 30));
        discountDao.addDiscount(new ProductStoreDateKey("P008", "mega_image", LocalDate.of(2025, 5, 20)), new Discount(LocalDate.of(2025, 5, 20), LocalDate.of(2025, 5, 30), (byte) 25));
    }

    @Test
    void givenCurrentDate_whenComputingAvailableDates_thenReturnsCorrectDates() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 5, 10);

        // When
        Map<String, LocalDate> result = discountDao.getAvailableDiscountDatePerStore(currentDate);

        // Then
        assertEquals(LocalDate.of(2025, 5, 8), result.get("kaufland"));
        assertEquals(LocalDate.of(2025, 5, 4), result.get("lidl"));
        assertEquals(LocalDate.of(2025, 5, 5), result.get("profi"));
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
        assertEquals(LocalDate.of(2025, 5, 11), result.get("kaufland"));
        assertEquals(LocalDate.of(2025, 5, 4), result.get("lidl"));
        assertEquals(LocalDate.of(2025, 5, 5), result.get("profi"));
        assertEquals(LocalDate.of(2025, 5, 20), result.get("mega_image"));
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
        List<String> expectedKeys = List.of("P002-kaufland-2025-05-08", "P005-lidl-2025-05-04", "P006-profi-2025-05-05", "P007-profi-2025-05-05");
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
        List<String> expectedKeys = List.of("P003-kaufland-2025-05-11", "P005-lidl-2025-05-04",
                "P006-profi-2025-05-05", "P007-profi-2025-05-05", "P008-mega_image-2025-05-20");

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

    @Test
    void givenValidStoreAndDate_whenGettingDiscountDateForStore_thenReturnsCorrectDate() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 5, 11);

        // When
        LocalDate kauflandDate = discountDao.getAvailableDiscountDateForStore(currentDate, "kaufland");
        LocalDate lidlDate = discountDao.getAvailableDiscountDateForStore(currentDate, "lidl");
        LocalDate profiDate = discountDao.getAvailableDiscountDateForStore(currentDate, "profi");

        // Then
        assertEquals(LocalDate.of(2025, 5, 11), kauflandDate);
        assertEquals(LocalDate.of(2025, 5, 4), lidlDate);
        assertEquals(LocalDate.of(2025, 5, 5), profiDate);
    }

    @Test
    void givenStoreWithNoValidDiscounts_whenGettingDiscountDateForStore_thenReturnsNull() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 5, 5);

        // mega_image only has a future discount
        LocalDate result = discountDao.getAvailableDiscountDateForStore(currentDate, "mega_image");

        // Then
        assertNull(result);
    }

    @Test
    void givenInvalidStore_whenGettingDiscountDateForStore_thenReturnsNull() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 5, 10);

        // Store that doesn’t exist in the data
        LocalDate result = discountDao.getAvailableDiscountDateForStore(currentDate, "Carrefour");

        // Then
        assertNull(result);
    }

    @Test
    void givenFutureDate_whenGettingDiscountDateForStore_thenReturnsLatestAvailable() {
        // Given
        LocalDate currentDate = LocalDate.of(2025, 5, 25);

        // When
        LocalDate kauflandDate = discountDao.getAvailableDiscountDateForStore(currentDate, "kaufland");
        LocalDate mega_imageDate = discountDao.getAvailableDiscountDateForStore(currentDate, "mega_image");

        // Then
        assertEquals(LocalDate.of(2025, 5, 11), kauflandDate);
        assertEquals(LocalDate.of(2025, 5, 20), mega_imageDate);
    }

}
