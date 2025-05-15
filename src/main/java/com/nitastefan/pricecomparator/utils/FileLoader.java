package com.nitastefan.pricecomparator.utils;

import com.nitastefan.pricecomparator.dao.DiscountDao;
import com.nitastefan.pricecomparator.dao.ProductDao;
import com.nitastefan.pricecomparator.dao.StoreCatalogDao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileLoader {

    public static void loadAllStoresData(ProductDao productDao, StoreCatalogDao storeCatalogDao, DiscountDao discountDao, String productsDir, String discountsDir) throws IOException {
        loadFromProductsDir(productDao, storeCatalogDao, productsDir);
        loadFromDiscountsDir(discountDao, discountsDir);
    }

    private static void loadFromProductsDir(ProductDao productDao, StoreCatalogDao storeCatalogDao, String productsDir) throws IOException {
        try (var paths = Files.list(Path.of(productsDir))) {
            paths.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            CsvParser.parseProducts(productDao, storeCatalogDao, file);

                        } catch (IOException | IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }


    private static void loadFromDiscountsDir(DiscountDao discountDao, String discountsDir) throws IOException {
        try (var paths = Files.list(Path.of(discountsDir))) {
            paths.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {

                            CsvParser.parseDiscounts(discountDao, file);

                        } catch (IOException | IllegalArgumentException e) {
                            System.err.println("Error loading discounts from file '" + file.getFileName() + "': " + e.getMessage());
                        }
                    });
        }
    }
}
