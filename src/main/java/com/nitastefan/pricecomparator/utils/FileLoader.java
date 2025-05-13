package com.nitastefan.pricecomparator.utils;

import com.nitastefan.pricecomparator.models.Discount;
import com.nitastefan.pricecomparator.models.Product;
import com.nitastefan.pricecomparator.models.Store;
import com.nitastefan.pricecomparator.dao.StoreDao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class FileLoader {

    public static void loadAllStoresData(StoreDao storeDao, String productsDir, String discountsDir) throws IOException {
        loadAllProducts(storeDao, productsDir);
        loadAllDiscounts(storeDao, discountsDir);
    }

    private static void loadAllProducts(StoreDao storeDao, String productsDir) throws IOException {
        try (var paths = Files.list(Path.of(productsDir))) {
            paths.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            String fileName = file.getFileName().toString();
                            String[] parts = fileName.split("_");

                            //validate file name format
                            if (parts.length != 2)
                                throw new IllegalArgumentException("Invalid file name format: " + fileName);

                            String storeName = StringUtils.capitalizeFirstLetter(parts[0]);
                            String dateString = parts[1].replace(".csv", "");
                            LocalDate date = LocalDate.parse(dateString);

                            List<Product> products = CsvParser.parseProducts(file.toString());

                            if (!storeDao.isStoreRegistered(storeName))
                                storeDao.addStore(new Store(storeName));

                            storeDao.getStoreByName(storeName).addProducts(date, products);

                        } catch (IOException | IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }


    private static void loadAllDiscounts(StoreDao storeDao, String discountsDir) throws IOException {
        try (var paths = Files.list(Path.of(discountsDir))) {
            paths.filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            String fileName = file.getFileName().toString();
                            String[] parts = fileName.split("_");

                            if (parts.length != 3 || !parts[1].equals("discounts"))
                                throw new IllegalArgumentException("Invalid discount file name format: " + fileName);

                            String storeName = StringUtils.capitalizeFirstLetter(parts[0]);
                            String dateString = parts[2].replace(".csv", "");
                            LocalDate date = LocalDate.parse(dateString);

                            Map<String, Discount> discounts = CsvParser.parseDiscounts(file.toString());

                            if (!storeDao.isStoreRegistered(storeName))
                                storeDao.addStore(new Store(storeName));

                            storeDao.getStoreByName(storeName).addDiscounts(date, discounts);

                        } catch (IOException | IllegalArgumentException e) {
                            System.err.println("Error loading discounts from file '" + file.getFileName() + "': " + e.getMessage());
                        }
                    });
        }
    }
}
