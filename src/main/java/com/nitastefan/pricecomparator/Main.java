package com.nitastefan.pricecomparator;

import com.nitastefan.pricecomparator.controllers.StoreController;
import com.nitastefan.pricecomparator.dao.StoreDao;
import com.nitastefan.pricecomparator.services.StoreService;
import com.nitastefan.pricecomparator.utils.FileLoader;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        StoreDao storeDao = storeSetup();
        StoreService storeService = new StoreService(storeDao);
        StoreController storeController = new StoreController(storeService);

        storeController.startServer();
    }

    private static StoreDao storeSetup() throws IOException {
        String productsDir = Paths.get("src/main/resources/data/products/").toString();
        String discountsDir = Paths.get("src/main/resources/data/discounts/").toString();

        StoreDao storeDao = new StoreDao();

        FileLoader.loadAllStoresData(storeDao, productsDir, discountsDir);

        return storeDao;
    }

}