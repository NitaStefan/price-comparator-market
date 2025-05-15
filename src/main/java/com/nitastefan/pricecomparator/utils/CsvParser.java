package com.nitastefan.pricecomparator.utils;

import com.nitastefan.pricecomparator.dao.DiscountDao;
import com.nitastefan.pricecomparator.dao.ProductDao;
import com.nitastefan.pricecomparator.dao.StoreCatalogDao;
import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.Discount;
import com.nitastefan.pricecomparator.models.Product;
import com.nitastefan.pricecomparator.models.StoreCatalog;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class CsvParser {

    public static void parseProducts(ProductDao productDao, StoreCatalogDao storeCatalogDao, Path file) throws IOException {
        String fileName = file.getFileName().toString();
        String[] parts = fileName.split("_");

        //validate file name format
        if (parts.length != 2)
            throw new IllegalArgumentException("Invalid file name format: " + fileName);

        String storeName = parts[0];
        String dateString = parts[1].replace(".csv", "");
        LocalDate date = LocalDate.parse(dateString);

        try (BufferedReader reader = Files.newBufferedReader(Path.of(file.toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(";");
                if (columns.length == 8) {
                    String productId = columns[0];
                    String productName = columns[1];
                    String productCategory = columns[2];
                    String brand = columns[3];
                    float packageQuantity = Float.parseFloat(columns[4]);
                    String packageUnit = columns[5];
                    float price = Float.parseFloat(columns[6]);
                    String currency = columns[7];

                    productDao.addProduct(productId, new Product(productName, productCategory, brand, packageQuantity, packageUnit));
                    storeCatalogDao.addStoreCatalog(new ProductStoreDateKey(productId, storeName, date), new StoreCatalog(price, currency));
                }
            }
        }
    }

    public static void parseDiscounts(DiscountDao discountDao, Path file) throws IOException {
        String fileName = file.getFileName().toString();
        String[] parts = fileName.split("_");

        if (parts.length != 3 || !parts[1].equals("discounts"))
            throw new IllegalArgumentException("Invalid discount file name format: " + fileName);

        String storeName = parts[0];
        String dateString = parts[2].replace(".csv", "");
        LocalDate date = LocalDate.parse(dateString);

        try (BufferedReader reader = Files.newBufferedReader(Path.of(file.toString()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(";");
                if (columns.length == 9) {
                    String productId = columns[0];
                    String productName = columns[1];
                    String productCategory = columns[2];
                    String brand = columns[3];
                    float packageQuantity = Float.parseFloat(columns[4]);
                    String packageUnit = columns[5];
                    LocalDate fromDate = LocalDate.parse(columns[6]);
                    LocalDate toDate = LocalDate.parse(columns[7]);
                    byte discountPercentage = Byte.parseByte(columns[8]);

                    discountDao.addDiscount(new ProductStoreDateKey(productId, storeName, date), new Discount(fromDate, toDate, discountPercentage));
                }
            }
        }
    }
}
