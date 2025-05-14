package com.nitastefan.pricecomparator.controllers;

import com.nitastefan.pricecomparator.dao.StoreDao;
import com.nitastefan.pricecomparator.models.Store;
import com.nitastefan.pricecomparator.services.StoreService;
import com.nitastefan.pricecomparator.utils.StringUtils;
import io.javalin.Javalin;

public class StoreController {

    private final StoreService storeService;

    //todo: replace with a server class
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    public void startServer() {
        Javalin app = Javalin.create(/*config*/);

        app.get("/", ctx -> ctx.result("Hello World"));

        app.get("/stores/{storeName}", ctx -> {
            String storeName = ctx.pathParam("storeName");
            storeName = StringUtils.capitalizeFirstLetter(storeName);

            Store store = storeService.getStoreByName(storeName);
            if (store == null)
                ctx.status(404).result("Store not found: " + storeName);
            else ctx.json(store);

        });

        app.start(7070);
    }

}
