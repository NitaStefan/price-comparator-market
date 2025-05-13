package com.nitastefan.pricecomparator.utils;

import com.nitastefan.pricecomparator.models.Product;
import com.nitastefan.pricecomparator.models.Store;
import com.nitastefan.pricecomparator.dao.StoreDao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class FileLoader {

    public static void loadAllStores(StoreDao registry, String productsDir) throws IOException {
        Files.list(Path.of(productsDir))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        String fileName = file.getFileName().toString();
                        String[] parts = fileName.split("_");

                        if (parts.length != 2)
                            throw new IllegalArgumentException("Invalid file name format: " + fileName);

                        String storeName = StringUtils.capitalizeFirstLetter(parts[0]);
                        String dateString = parts[1].replace(".csv", "");
                        LocalDate date = LocalDate.parse(dateString);

                        List<Product> products = CsvParser.parseProducts(file.toString());

                        if (registry.isStoreRegistered(storeName))
                            registry.addProductsFromDateToStore(storeName, date, products);
                        else {
                            Store store = new Store(storeName);
                            store.getProductsByDate().put(LocalDate.parse(dateString), products);
                            registry.addStore(store);
                        }

                    } catch (IOException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                });
    }
}
