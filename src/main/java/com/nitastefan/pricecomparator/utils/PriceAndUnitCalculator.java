package com.nitastefan.pricecomparator.utils;

public class PriceAndUnitCalculator {
    public static float pricePerUnit(float price, float qty, String unit) {
        return price * 1 / qty * Unit.getValue(unit);
    }
}
