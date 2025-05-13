package com.nitastefan.pricecomparator;

import com.nitastefan.pricecomparator.dao.StoreDao;
import com.nitastefan.pricecomparator.utils.FileLoader;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
//        Javalin app = Javalin.create(/*config*/);
//        app.get("/", ctx -> ctx.result("Hello World"))
//                .start(7070);

        StoreDao storeDao = storeSetup();

    }

    public static StoreDao storeSetup() throws IOException {
        String productsDir = Paths.get("src/main/resources/data/products/").toString();
        String discountsDir = Paths.get("src/main/resources/data/discounts/").toString();

        StoreDao storeDao = new StoreDao();

        FileLoader.loadAllStoresData(storeDao, productsDir, discountsDir);

//        printAllStoresDate(storeDao);

        return storeDao;
    }

    private static void printAllStoresDate(StoreDao storeDao) {
        storeDao.getStores().forEach((name, store) -> {
            System.out.println("\nStore: " + name);

            // Print products
            store.getProductsByDate().forEach((date, products) -> {
                System.out.println("\nProducts for " + date + ":");
                products.forEach(System.out::println);
            });

            // Print discounts
            store.getDiscountsByDate().forEach((date, discounts) -> {
                System.out.println("\nDiscounts for " + date + ":");
                discounts.forEach((productId, discount) -> {
                    System.out.println("Product ID: " + productId + " -> " + discount);
                });
            });
        });
    }
}