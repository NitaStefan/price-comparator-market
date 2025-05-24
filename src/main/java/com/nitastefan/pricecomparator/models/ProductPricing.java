package com.nitastefan.pricecomparator.models;

import com.nitastefan.pricecomparator.utils.PriceCalculator;
import com.nitastefan.pricecomparator.utils.Unit;

public record ProductPricing(float price, float quantity, String unit) {

    public float getPricePerUnit() {
        return PriceCalculator.findPricePerUnit(price, quantity, unit);
    }

    public String getStandardUnit() {
        return Unit.getStandard(unit);
    }
}