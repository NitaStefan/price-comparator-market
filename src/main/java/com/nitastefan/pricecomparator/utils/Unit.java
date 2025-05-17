package com.nitastefan.pricecomparator.utils;

import java.util.Map;

public class Unit {

    private static final Map<String, Float> VALUE_CONVERSIONS = Map.of(
            "kg", 1.0f,
            "g", 1000f,
            "l", 1.0f,
            "ml", 1000f,
            "role", 1.0f,
            "buc", 1.0f
    );

    private static final Map<String, String> STANDARD_UNITS = Map.of(
            "kg", "kg",
            "g", "kg",
            "l", "l",
            "ml", "l",
            "role", "role",
            "buc", "buc"
    );

    public static float getValue(String unit) {
        Float value = VALUE_CONVERSIONS.get(unit);
        if (value == null) {
            throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
        return value;
    }

    public static String getStandard(String unit) {
        String standard = STANDARD_UNITS.get(unit);
        if (standard == null) {
            throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
        return standard;
    }
}
