package com.nitastefan.pricecomparator.utils;

import com.nitastefan.pricecomparator.models.Discount;

import java.time.LocalDate;

public class PriceCalculator {
    public static float findPricePerUnit(float price, float qty, String unit) {
        float result = price / qty * Unit.getValue(unit);
        return Math.round(result * 100) / 100.0f;
    }

    public static boolean isDiscountApplied(Discount discount, LocalDate currentDate) {
        if (discount == null) return false;

        return !currentDate.isBefore(discount.getFromDate()) && !currentDate.isAfter(discount.getToDate());
    }

    public static float applyDiscount(float price, byte percentage) {
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");

        float discountedPrice = price * (1 - (percentage / 100.0f));
        return Math.round(discountedPrice * 100) / 100.0f;
    }
}
