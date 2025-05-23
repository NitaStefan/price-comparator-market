package com.nitastefan.pricecomparator.dto;

public class ProductTargetsRequest {

    private float price;
    private float quantity;
    private String unit;
    private String productName;

    public ProductTargetsRequest() {
    }

    public ProductTargetsRequest(float price, float quantity, String unit, String productName) {
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
        this.productName = productName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}
