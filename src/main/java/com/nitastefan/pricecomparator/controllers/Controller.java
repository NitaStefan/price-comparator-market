package com.nitastefan.pricecomparator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nitastefan.pricecomparator.dto.ApiResponse;
import com.nitastefan.pricecomparator.dto.DateRequest;
import com.nitastefan.pricecomparator.dto.ProductTargetsRequest;
import com.nitastefan.pricecomparator.services.Service;
import com.nitastefan.pricecomparator.utils.BasketFilter;
import io.javalin.Javalin;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    private void badRequest(io.javalin.http.Context ctx, String message) {
        ctx.status(400).json(new ApiResponse<>(message, null, false));
    }

    public void startServer() {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.allowHost("http://localhost:5173");
                });
            });
        });

        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(new ApiResponse<>("An unexpected error occurred: " + e.getMessage(), null, false));
        });

        app.error(404, ctx -> {
            ctx.json(new ApiResponse<>("Endpoint not found", null, false));
        });


        app.get("/", ctx -> ctx.result("Hello World"));

        app.get("/test", ctx -> ctx.status(404).json(new ApiResponse<>("Error occurred", null, false)));

        //current or shopping date
        app.post("/date", ctx -> {
            DateRequest dateRequest = ctx.bodyAsClass(DateRequest.class);

            if (dateRequest.getDate() == null) {
                badRequest(ctx, "Date parameter is required in the body");
                return;
            }

            LocalDate newDate = LocalDate.parse(dateRequest.getDate());
            service.setCurrentDate(newDate);
            ctx.json(new ApiResponse<>("Current date set successfully", newDate.toString(), true));
        });


        app.post("/basket", ctx -> {
            try {
                List<String> productNames = ctx.bodyAsClass(List.class);
                if (productNames == null || productNames.isEmpty()) {
                    badRequest(ctx, "Product list cannot be empty");
                    return;
                }

                service.establishBasket(productNames);
                ctx.json(new ApiResponse<>("Basket established successfully", productNames, true));
            } catch (Exception e) {
                badRequest(ctx, "Invalid input. Expected a JSON array of product names.");
            }
        });

        app.post("/product-targets", ctx -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<ProductTargetsRequest> productTargets = objectMapper.readValue(ctx.body(), new TypeReference<>() {});
                if (productTargets == null || productTargets.isEmpty()) {
                    badRequest(ctx, "Product targets list cannot be empty");
                    return;
                }

                service.establishTargets(productTargets);
                ctx.json(new ApiResponse<>("Product targets established successfully", productTargets, true));
            } catch (Exception e) {
                badRequest(ctx, "Invalid input. Expected a JSON array of product targets.");
            }
        });


        app.get("/available-products-by-category", ctx -> {
            var productNames = service.getAvailableProductsByCategory();
            ctx.json(new ApiResponse<>("Available products by category retrieved successfully", productNames, true));
        });

        app.get("/available-product-names", ctx -> {
            var productNames = service.getAvailableProductNames();
            ctx.json(new ApiResponse<>("Available product names retrieved successfully", productNames, true));
        });

        app.get("/basket-products", ctx -> {
            Set<String> basketProducts = service.getProductsFromBasket();
            ctx.json(new ApiResponse<>("Basket products retrieved successfully", basketProducts, true));
        });

        app.get("/products-with-target", ctx -> {
            var productsWithTarget = service.getProductsWithTarget();
            ctx.json(new ApiResponse<>("Products with target retrieved successfully", productsWithTarget, true));
        });

        app.get("/latest-discounts", ctx -> {
            String lastDaysParam = ctx.queryParam("lastDays");
            int lastDays = lastDaysParam == null ? 1 : Integer.parseInt(lastDaysParam);

            var latestDiscounts = service.getLatestDiscounts(lastDays);
            ctx.json(new ApiResponse<>("Latest discounts retrieved successfully", latestDiscounts, true));
        });

        app.get("/all-products/best-deals", ctx -> {
            var bestDeals = service.getBestDeals(BasketFilter.NOT_USE);
            ctx.json(new ApiResponse<>("Best deals of all products retrieved successfully", bestDeals, true));
        });

        app.get("/basket/best-deals", ctx -> {
            var bestDeals = service.getBestDeals(BasketFilter.USE);
            ctx.json(new ApiResponse<>("Best deals of products from basket retrieved successfully", bestDeals, true));
        });

        app.get("/basket/best-deals-by-store", ctx -> {
            var bestDeals = service.getBasketDealsByStore();
            ctx.json(new ApiResponse<>("Best deals for basket by store retrieved successfully", bestDeals, true));
        });

        app.get("/check-watched-products", ctx -> {
            var watchedProducts = service.checkWatchedProducts();
            ctx.json(new ApiResponse<>("Targets for watched products retrieved successfully", watchedProducts, true));
        });

        app.get("/product-timeline", ctx -> {
            var productTimeline = service.getPriceTimeline();
            ctx.json(new ApiResponse<>("Product timeline retrieved successfully", productTimeline, true));
        });

        //deprecated methods
        app.get("/all-products/best-deals-deprecated", ctx -> {
            var bestDeals = service.getBestDealsDeprecated(BasketFilter.NOT_USE);
            ctx.json(new ApiResponse<>("Best deals of all products retrieved successfully", bestDeals, true));
        });

        app.get("/basket/best-deals-deprecated", ctx -> {
            var bestDeals = service.getBestDealsDeprecated(BasketFilter.USE);
            ctx.json(new ApiResponse<>("Best deals for basket retrieved successfully", bestDeals, true));
        });

        app.start(7070);
    }

}
