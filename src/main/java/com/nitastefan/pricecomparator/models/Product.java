package com.nitastefan.pricecomparator.models;

public class Product {
    private String productId;
    private String productName;
    private String productCategory;
    private String brand;
    private float packageQuantity;
    private String packageUnit;
    private float price;
    private String currency;

    public Product(String productId, String productName, String productCategory, String brand, float packageQuantity, String packageUnit, float price, String currency) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.brand = brand;
        this.packageQuantity = packageQuantity;
        this.packageUnit = packageUnit;
        this.price = price;
        this.currency = currency;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public float getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(float packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public String getPackageUnit() {
        return packageUnit;
    }

    public void setPackageUnit(String packageUnit) {
        this.packageUnit = packageUnit;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", brand='" + brand + '\'' +
                ", packageQuantity=" + packageQuantity +
                ", packageUnit='" + packageUnit + '\'' +
                ", price=" + price +
                ", currency='" + currency + '\'' +
                '}';
    }
}
