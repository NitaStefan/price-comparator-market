package com.nitastefan.pricecomparator.models;

public class StoreCatalog {

    float price;
    String currency;

    public StoreCatalog(float price, String currency) {
        this.price = price;
        this.currency = currency;
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
        return "StoreCatalog{" +
                "price=" + price +
                ", currency='" + currency + '\'' +
                '}';
    }
}
