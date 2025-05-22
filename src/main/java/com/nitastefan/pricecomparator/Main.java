package com.nitastefan.pricecomparator;

import com.nitastefan.pricecomparator.controllers.Controller;
import com.nitastefan.pricecomparator.dao.DiscountDao;
import com.nitastefan.pricecomparator.dao.ProductDao;
import com.nitastefan.pricecomparator.dao.StoreCatalogDao;
import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.services.Service;
import com.nitastefan.pricecomparator.utils.FileLoader;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String productsDir = Paths.get("src/main/resources/data/products/").toString();
        String discountsDir = Paths.get("src/main/resources/data/discounts/").toString();

        ProductDao productDao = new ProductDao();
        StoreCatalogDao storeCatalogDao = new StoreCatalogDao();
        DiscountDao discountDao = new DiscountDao();

        FileLoader.loadAllStoresData(productDao, storeCatalogDao, discountDao, productsDir, discountsDir);

        Service service = new Service(productDao, storeCatalogDao, discountDao);

        Controller controller = new Controller(service);

        //test setup
        service.setCurrentDate(LocalDate.of(2025, 5, 21));
        service.establishBasket(List.of("piper negru măcinat", "morcovi", "ciocolată neagră 70%", "șampon păr gras"));

        controller.startServer();
    }

}