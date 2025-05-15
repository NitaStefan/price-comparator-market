package com.nitastefan.pricecomparator.models;

public class Product {
    private String name;
    private String category;
    private String brand;
    private float packageQty;
    private String packageUnit;

    public Product(String name, String category, String brand, float packageQty, String packageUnit) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.packageQty = packageQty;
        this.packageUnit = packageUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public float getPackageQty() {
        return packageQty;
    }

    public void setPackageQty(float packageQty) {
        this.packageQty = packageQty;
    }

    public String getPackageUnit() {
        return packageUnit;
    }

    public void setPackageUnit(String packageUnit) {
        this.packageUnit = packageUnit;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", packageQty=" + packageQty +
                ", packageUnit='" + packageUnit + '\'' +
                '}';
    }
}
