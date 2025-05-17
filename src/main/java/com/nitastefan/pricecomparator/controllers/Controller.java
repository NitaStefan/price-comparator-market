package com.nitastefan.pricecomparator.controllers;

import com.nitastefan.pricecomparator.dto.ApiResponse;
import com.nitastefan.pricecomparator.services.Service;
import io.javalin.Javalin;

import java.util.Set;

public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    public void startServer() {
        Javalin app = Javalin.create(/*config*/);

        app.get("/", ctx -> ctx.result("Hello World"));

        app.get("/test", ctx -> ctx.status(404).json(new ApiResponse<>("Error occurred", null, false)));
//
//        //set current date for simulation
//        app.post("/date", ctx -> {
//            try {
//                DateRequest dateRequest = ctx.bodyAsClass(DateRequest.class);
//
//                if (dateRequest.getDate() == null) {
//                    ctx.status(400).json(
//                            new ApiResponse<>("Date parameter is required in the body", null, false)
//                    );
//                    return;
//                }
//
//                LocalDate newDate = LocalDate.parse(dateRequest.getDate());
//                storeService.setCurrentDate(newDate);
//                ctx.json(new ApiResponse<>("Current date set successfully", newDate.toString(), true));
//
//            } catch (Exception e) {
//                ctx.status(400).json(new ApiResponse<>("Invalid date format. Use yyyy-MM-dd", null, false));
//            }
//        });
//
        app.get("/available-products", ctx -> {
            try {
                Set<String> productNames = service.getAvailableProductNames();
                ctx.json(new ApiResponse<>("Available products retrieved successfully", productNames, true));
            } catch (Exception e) {
                ctx.status(500).json(new ApiResponse<>("An unexpected error occurred", null, false));
            }
        });

        app.get("/best-deals", ctx -> {
            try {
                var bestDeals = service.getBestDeals();
                ctx.json(new ApiResponse<>("Best deals retrieved successfully", bestDeals, true));
            } catch (Exception e) {
                ctx.status(500).json(new ApiResponse<>("An unexpected error occurred :" + e.getMessage(), null, false));
            }
        });

        app.start(7070);
    }

}
