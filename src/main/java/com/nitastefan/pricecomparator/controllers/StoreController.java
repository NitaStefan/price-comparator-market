package com.nitastefan.pricecomparator.controllers;

import com.nitastefan.pricecomparator.dto.ApiResponse;
import com.nitastefan.pricecomparator.dto.DateRequest;
import com.nitastefan.pricecomparator.models.Product;
import com.nitastefan.pricecomparator.models.Store;
import com.nitastefan.pricecomparator.services.StoreService;
import com.nitastefan.pricecomparator.utils.StringUtils;
import io.javalin.Javalin;

import java.time.LocalDate;
import java.util.List;

public class StoreController {

    private final StoreService storeService;

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
                ctx.status(404).json(new ApiResponse<>("Store not found: " + storeName, null, false));
            else
                ctx.json(new ApiResponse<>("Store retrieved successfully", store, true));
        });

        //set current date for simulation
        app.post("/date", ctx -> {
            try {
                DateRequest dateRequest = ctx.bodyAsClass(DateRequest.class);

                if (dateRequest.getDate() == null) {
                    ctx.status(400).json(
                            new ApiResponse<>("Date parameter is required in the body", null, false)
                    );
                    return;
                }

                LocalDate newDate = LocalDate.parse(dateRequest.getDate());
                storeService.setCurrentDate(newDate);
                ctx.json(new ApiResponse<>("Current date set successfully", newDate.toString(), true));

            } catch (Exception e) {
                ctx.status(400).json(new ApiResponse<>("Invalid date format. Use yyyy-MM-dd", null, false));
            }
        });

        app.get("/stores/{storeName}/available-products", ctx -> {
            try {
                String storeName = ctx.pathParam("storeName");
                storeName = StringUtils.capitalizeFirstLetter(storeName);

                List<Product> products = storeService.getAvailableProductsFromStore(storeName);

                if (products.isEmpty()) {
                    ctx.status(404).json(new ApiResponse<>("No available products found for store: " + storeName, null, false));
                } else {
                    ctx.json(new ApiResponse<>("Available products retrieved successfully", products, true));
                }

            } catch (Exception e) {
                ctx.status(500).json(new ApiResponse<>("An unexpected error occurred", null, false));
            }
        });

        app.start(7070);
    }

}
