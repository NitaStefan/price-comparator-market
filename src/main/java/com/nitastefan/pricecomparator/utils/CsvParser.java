package com.nitastefan.pricecomparator.utils;

import com.nitastefan.pricecomparator.models.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    public static List<Product> parseProducts(String filePath) throws IOException {
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
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

                    products.add(new Product(productId, productName, productCategory, brand, packageQuantity, packageUnit, price, currency));
                }
            }
        }

        return products;
    }
}
