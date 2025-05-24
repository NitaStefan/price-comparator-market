package com.nitastefan.pricecomparator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nitastefan.pricecomparator.models.Discount;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDealDto {
    private String storeName;
    private String productName;
    private float price;
    private float pricePerUnit;
    private String currency;
    private float quantity;
    private String unit;
    private String standardUnit;
    private String brand;
    private boolean isDiscountApplied;
    private Discount discount;

    private ProductDealDto(Builder builder) {
        this.storeName = builder.storeName;
        this.productName = builder.productName;
        this.price = builder.price;
        this.pricePerUnit = builder.pricePerUnit;
        this.currency = builder.currency;
        this.quantity = builder.quantity;
        this.unit = builder.unit;
        this.standardUnit = builder.standardUnit;
        this.brand = builder.brand;
        this.isDiscountApplied = builder.isDiscountApplied;
        this.discount = builder.discount;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getProductName() {
        return productName;
    }

    public float getPrice() {
        return price;
    }

    public float getPricePerUnit() {
        return pricePerUnit;
    }

    public String getCurrency() {
        return currency;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getStandardUnit() {
        return standardUnit;
    }

    public String getBrand() {
        return brand;
    }

    public boolean isDiscountApplied() {
        return isDiscountApplied;
    }

    public Discount getDiscount() {
        return discount;
    }

    public static class Builder {
        private String storeName;
        private String productName;
        private float price;
        private float pricePerUnit;
        private String currency;
        private float quantity;
        private String unit;
        private String standardUnit;
        private String brand;
        private boolean isDiscountApplied;
        private Discount discount;

        public Builder storeName(String storeName) { this.storeName = storeName; return this; }
        public Builder productName(String productName) { this.productName = productName; return this; }
        public Builder price(float price) { this.price = price; return this; }
        public Builder pricePerUnit(float pricePerUnit) { this.pricePerUnit = pricePerUnit; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder quantity(float quantity) { this.quantity = quantity; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder standardUnit(String standardUnit) { this.standardUnit = standardUnit; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder isDiscountApplied(boolean isDiscountApplied) { this.isDiscountApplied = isDiscountApplied; return this; }
        public Builder discount(Discount discount) { this.discount = discount; return this; }

        public ProductDealDto build() {
            return new ProductDealDto(this);
        }
    }


}
