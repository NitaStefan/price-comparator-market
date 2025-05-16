package com.nitastefan.pricecomparator.dao;

import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.Discount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

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
        Map<String, LocalDate> result = discountDao.computeAvailableDiscountDate(currentDate);

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
        Map<String, LocalDate> result = emptyDiscountDao.computeAvailableDiscountDate(currentDate);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void givenFutureDate_whenComputingAvailableDates_thenReturnsLatestDates() {
        // Given
        LocalDate futureDate = LocalDate.of(2025, 5, 25);

        // When
        Map<String, LocalDate> result = discountDao.computeAvailableDiscountDate(futureDate);

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
        Map<String, LocalDate> result = discountDao.computeAvailableDiscountDate(pastDate);

        // Then
        assertTrue(result.isEmpty());
    }
}
