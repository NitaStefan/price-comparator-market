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

        String productsDir = Paths.get("src/main/resources/data/products/").toString();

        // Create the registry
        StoreDao registry = new StoreDao();

        // Load all stores
        FileLoader.loadAllStores(registry, productsDir);

        // Print the loaded stores
        registry.getStores().forEach((name, store) -> {
            System.out.println("\nStore: " + name);
            store.getProductsByDate().forEach((date, products) -> {
                System.out.println("Date: " + date);
                products.forEach(System.out::println);
            });
        });

    }
}