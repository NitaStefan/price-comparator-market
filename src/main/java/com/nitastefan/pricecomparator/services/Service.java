package com.nitastefan.pricecomparator.services;

import com.nitastefan.pricecomparator.dao.DiscountDao;
import com.nitastefan.pricecomparator.dao.ProductDao;
import com.nitastefan.pricecomparator.dao.StoreCatalogDao;
import com.nitastefan.pricecomparator.dto.ProductDealDto;
import com.nitastefan.pricecomparator.dto.ProductTargetsRequest;
import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.*;
import com.nitastefan.pricecomparator.utils.BasketFilter;
import com.nitastefan.pricecomparator.utils.PriceCalculator;
import com.nitastefan.pricecomparator.utils.Unit;

import java.time.LocalDate;
import java.util.*;

public class Service {
    private final ProductDao productDao;

    private final StoreCatalogDao storeCatalogDao;

    private final DiscountDao discountDao;

    private LocalDate currentDate;

    private ProductBasket productBasket;

    private ProductsWithTarget productsWithTarget;

    public Service(ProductDao productDao, StoreCatalogDao storeCatalogDao, DiscountDao discountDao) {
        this.currentDate = LocalDate.now();
        this.productDao = productDao;
        this.storeCatalogDao = storeCatalogDao;
        this.discountDao = discountDao;
        this.productBasket = new ProductBasket();
        this.productsWithTarget = new ProductsWithTarget();
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public Set<String> getProductsFromBasket() {
        return productBasket.getProductNames();
    }

    public Map<String, ProductPricing> getProductsWithTarget() {
        return productsWithTarget.getProducts();
    }

    public void setProductBasket(ProductBasket productBasket) {
        this.productBasket = productBasket;
    }

    public Map<String, Set<String>> getAvailableProductsByCategory() {
        Map<String, Set<String>> allAvailableProducts = new HashMap<>();

        List<ProductStoreDateKey> availableProductsCatalog = storeCatalogDao.getAvailableCatalogKeys(currentDate);

        availableProductsCatalog.forEach(psdKey -> {
            Product product = productDao.getProduct(psdKey.productId());

            allAvailableProducts.putIfAbsent(product.getCategory(), new HashSet<>());
            allAvailableProducts.get(product.getCategory()).add(product.getName());
        });

        return allAvailableProducts;
    }

    public Set<String> getAvailableProductNames() {
        Set<String> productsNames = new HashSet<>();
        List<ProductStoreDateKey> availableProductsCatalog = storeCatalogDao.getAvailableCatalogKeys(currentDate);

        availableProductsCatalog.forEach(psdKey -> {
            Product product = productDao.getProduct(psdKey.productId());
            productsNames.add(product.getName());
        });

        return productsNames;
    }

    public List<Map<String, Object>> getLatestDiscounts(int lastDays) {
        List<ProductStoreDateKey> availableDiscountKeys = discountDao.getAvailableDiscountKeys(currentDate);
        List<Map<String, Object>> result = new ArrayList<>();

        //eventually get date of the current catalog to get the price
        availableDiscountKeys.forEach(key -> {
            Discount discount = discountDao.getDiscount(key);
            Product product = productDao.getProduct(key.productId());

            if (discount.getFromDate().isAfter(currentDate.minusDays(lastDays)) && !discount.getFromDate().isAfter(currentDate) && !discount.getToDate().isBefore(currentDate)) {
                result.add(Map.of(
                        "store", key.storeName(),
                        "productName", product.getName(),
                        "discountPercentage", discount.getPercentage(),
                        "discountStartDate", discount.getFromDate().toString(),
                        "discountEndDate", discount.getToDate().toString(),
                        "brand", product.getBrand(),
                        "quantity", product.getPackageQty(),
                        "unit", product.getPackageUnit()
                ));
            }
        });

        return result;
    }

    public Map<String, Object> getBestDeals(BasketFilter basketFilter) {
        List<ProductStoreDateKey> availableCatalog = storeCatalogDao.getAvailableCatalogKeys(currentDate);
        Map<String, LocalDate> storeDiscountDate = discountDao.getAvailableDiscountDatePerStore(currentDate);

        Map<String, Set<ProductDealDto>> dealsByProductName = new HashMap<>();

        for (ProductStoreDateKey key : availableCatalog) {
            StoreCatalog storeCatalog = storeCatalogDao.getStoreCatalog(key);
            Product product = productDao.getProduct(key.productId());
            float price = storeCatalog.getPrice();

            //apply basket filtering if requested
            if (basketFilter == BasketFilter.USE && !productBasket.contains(product.getName()))
                continue;

            //check if there is a discount that applies
            LocalDate currentDiscountDate = storeDiscountDate.get(key.storeName());
            ProductStoreDateKey psdKey = new ProductStoreDateKey(key.productId(), key.storeName(), currentDiscountDate);
            Discount discount = discountDao.getDiscount(psdKey);

            //discount is applied if it's not null and currently active (within date range)
            boolean hasDiscount = PriceCalculator.isDiscountApplied(discount, currentDate);
            if (hasDiscount)
                price = PriceCalculator.applyDiscount(price, discount.getPercentage());

            //calculate price per standard unit (e.g., 12 RON for 500g becomes 24 RON/kg)
            float pricePerUnit = PriceCalculator.findPricePerUnit(price, product.getPackageQty(), product.getPackageUnit());

            //in sorted order by pricePerUnit (best deal to worst deal)
            dealsByProductName.putIfAbsent(product.getName(), new TreeSet<>(Comparator.comparingDouble(ProductDealDto::getPricePerUnit)));

            dealsByProductName.get(product.getName()).add(new ProductDealDto.Builder()
                    .storeName(key.storeName())
                    .productName(product.getName())
                    .price(price)
                    .pricePerUnit(pricePerUnit)
                    .currency(storeCatalog.getCurrency())
                    .quantity(product.getPackageQty())
                    .unit(product.getPackageUnit())
                    .standardUnit(Unit.getStandard(product.getPackageUnit()))
                    .brand(product.getBrand())
                    .isDiscountApplied(hasDiscount)
                    .discount(discount)
                    .build()
            );
        }

        float totalForBestDeals = 0;
        if (basketFilter == BasketFilter.USE)
            totalForBestDeals = (float) dealsByProductName.values().stream()
                    .mapToDouble(deals -> ((TreeSet<ProductDealDto>) deals).first().getPrice()).sum();

        return Map.of(
                "deals", dealsByProductName,
                //if showing deals from the basket, also add the total for the best deals
                "totalForBestDeals", totalForBestDeals == 0 ? "null" : totalForBestDeals
        );
    }

    public Map<String, Map<String, Object>> getBasketDealsByStore() {
        List<ProductStoreDateKey> availableCatalog = storeCatalogDao.getAvailableCatalogKeys(currentDate);
        Map<String, LocalDate> storeDiscountDate = discountDao.getAvailableDiscountDatePerStore(currentDate);

        Map<String, Map<String, Object>> dealsByStore = new HashMap<>();

        for (ProductStoreDateKey key : availableCatalog) {
            String store = key.storeName();
            String productId = key.productId();

            Product product = productDao.getProduct(productId);
            StoreCatalog catalog = storeCatalogDao.getStoreCatalog(key);
            float price = catalog.getPrice();

            if (!productBasket.contains(product.getName())) continue;

            LocalDate discountDate = storeDiscountDate.get(store);
            Discount discount = discountDao.getDiscount(new ProductStoreDateKey(productId, store, discountDate));
            boolean hasDiscount = PriceCalculator.isDiscountApplied(discount, currentDate);

            if (hasDiscount)
                price = PriceCalculator.applyDiscount(price, discount.getPercentage());

            float pricePerUnit = PriceCalculator.findPricePerUnit(price, product.getPackageQty(), product.getPackageUnit());

            dealsByStore.putIfAbsent(store, new HashMap<>());
            dealsByStore.get(store).put("total", (float) dealsByStore.get(store).getOrDefault("total", 0f) + price);

            dealsByStore.get(store).put(product.getName(), new ProductDealDto.Builder()
                    .price(price)
                    .pricePerUnit(pricePerUnit)
                    .currency(catalog.getCurrency())
                    .quantity(product.getPackageQty())
                    .unit(product.getPackageUnit())
                    .standardUnit(Unit.getStandard(product.getPackageUnit()))
                    .brand(product.getBrand())
                    .isDiscountApplied(hasDiscount)
                    .discount(discount)
                    .build());
        }

        return dealsByStore;
    }

    //other variations can be written
    public Map<String, Map<String, List<Object>>> getPriceTimeline() {
        Map<String, TreeSet<LocalDate>> catalogSortedDates = storeCatalogDao.getSortedDatesByStore();

        Map<String, Map<String, List<Object>>> result = new HashMap<>();

        catalogSortedDates.forEach((store, catalogDates) -> {
            //create a sorted list to be able to access current and next date by index
            List<LocalDate> dateList = new ArrayList<>(catalogDates);

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate startDate = dateList.get(i);
                LocalDate defaultEndDate = startDate.plusWeeks(2);
                //treat endDate as inclusive
                LocalDate endDate = i + 1 != dateList.size() ? dateList.get(i + 1).minusDays(1) : defaultEndDate;

                //get all available products from a store listed on a specific date
                StoreDate storeDate = new StoreDate(store, startDate);
                List<ProductStoreDateKey> catalogKeys = storeCatalogDao.getCatalogKeysForStoreDate(storeDate);

                catalogKeys.forEach(catalogKey -> {
                    StoreCatalog catalog = storeCatalogDao.getStoreCatalog(catalogKey);
                    Product product = productDao.getProduct(catalogKey.productId());

                    //get the available discount date that applies on endDate to check price between startDate and endDate
                    LocalDate discountDate = discountDao.getAvailableDiscountDateForStore(endDate, catalogKey.storeName());

                    ProductStoreDateKey discountKey = new ProductStoreDateKey(catalogKey.productId(), catalogKey.storeName(), discountDate);
                    Discount discount = discountDao.getDiscount(discountKey);

                    result.putIfAbsent(product.getName(), new HashMap<>());
                    result.get(product.getName()).putIfAbsent(store, new ArrayList<>());

                    float pricePerUnit = PriceCalculator.findPricePerUnit(catalog.getPrice(), product.getPackageQty(), product.getPackageUnit());

                    //check if we have a discount period between startDate and endDate
                    if (discount != null && !discount.getFromDate().isBefore(startDate) && !discount.getToDate().isAfter(endDate)) {
                        float reducedPricePerUnit = PriceCalculator.applyDiscount(pricePerUnit, discount.getPercentage());

                        //if the price does not start reduced, put the regular price until the potential discount starts
                        if (discount.getFromDate().isAfter(startDate)) {
                            Map<String, Object> firstInterval = Map.of(
                                    "from", startDate.toString(),
                                    "to", discount.getFromDate().minusDays(1).toString(),
                                    "pricePerUnit", pricePerUnit,
                                    "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                    "currency", catalog.getCurrency()
                            );
                            result.get(product.getName()).get(store).add(firstInterval);

                        }

                        boolean isDiscountToDateAfterEndDate = discount.getToDate().isAfter(endDate);

                        //category, unit, quantity, brand, price can also be added
                        Map<String, Object> secondInterval = Map.of(
                                "from", discount.getFromDate().toString(),
                                //make sure the toDate of the discount does not go further than the available time of a product
                                "to", !isDiscountToDateAfterEndDate ? discount.getToDate().toString() : endDate.toString(),
                                "pricePerUnit", reducedPricePerUnit,
                                "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                "currency", catalog.getCurrency()
                        );

                        result.get(product.getName()).get(store).add(secondInterval);

                        //in case the discount does not cover the whole interval
                        if (discount.getToDate().isBefore(endDate)) {
                            Map<String, Object> thirdInterval = Map.of(
                                    "from", discount.getToDate().plusDays(1).toString(),
                                    "to", endDate.toString(),
                                    "pricePerUnit", pricePerUnit,
                                    "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                    "currency", catalog.getCurrency()
                            );
                            result.get(product.getName()).get(store).add(thirdInterval);
                        }
                    } else {
                        //no discount is applied in the startDate - endDate period
                        Map<String, Object> testInterval = Map.of(
                                "from", startDate.toString(),
                                "to", endDate.toString(),
                                "pricePerUnit", pricePerUnit,
                                "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                "currency", catalog.getCurrency()
                        );

                        result.get(product.getName()).get(store).add(testInterval);
                    }
                });
            }
        });

        return result;
    }

    public Map<String, ProductDealDto> checkWatchedProducts() {

        List<ProductStoreDateKey> availableCatalog = storeCatalogDao.getAvailableCatalogKeys(currentDate);
        Map<String, LocalDate> storeDiscountDate = discountDao.getAvailableDiscountDatePerStore(currentDate);

        Map<String, ProductDealDto> result = new HashMap<>();

        for (ProductStoreDateKey key : availableCatalog) {
            String store = key.storeName();
            String productId = key.productId();

            Product product = productDao.getProduct(productId);
            String productName = product.getName();
            StoreCatalog catalog = storeCatalogDao.getStoreCatalog(key);
            float price = catalog.getPrice();

            if (!productsWithTarget.contains(productName)) continue;

            //check if there is a discount that applies at that time (discount is null if there is no discount or discount date)
            LocalDate discountDate = storeDiscountDate.get(store);
            Discount discount = discountDao.getDiscount(new ProductStoreDateKey(productId, store, discountDate));
            boolean hasDiscount = PriceCalculator.isDiscountApplied(discount, currentDate);

            if (hasDiscount)
                price = PriceCalculator.applyDiscount(price, discount.getPercentage());

            ProductPricing pricing = productsWithTarget.getPricing(productName);
            float targetPricePerUnit = PriceCalculator.findPricePerUnit(pricing.price(), pricing.quantity(), pricing.unit());
            float pricePerUnit = PriceCalculator.findPricePerUnit(price, product.getPackageQty(), product.getPackageUnit());

            if (pricePerUnit <= targetPricePerUnit) {
                ProductDealDto targetProduct = new ProductDealDto.Builder()
                        .storeName(store)
                        .price(price)
                        .pricePerUnit(pricePerUnit)
                        .currency(catalog.getCurrency())
                        .quantity(product.getPackageQty())
                        .unit(product.getPackageUnit())
                        .standardUnit(Unit.getStandard(product.getPackageUnit()))
                        .brand(product.getBrand())
                        .build();

                //among all the products below the target, get the one with the best offer
                if (result.containsKey(productName)) {
                    if (result.get(productName).getPricePerUnit() > pricePerUnit)
                        result.put(productName, targetProduct);

                } else result.put(productName, targetProduct);
            }
        }

        return result;
    }

    public void establishBasket(List<String> productNames) {
        productBasket.setProductNames(new HashSet<>(productNames));
    }

    public void establishTargets(List<ProductTargetsRequest> targets) {
        productsWithTarget.establishTargets(targets);
    }

//DEPRECATED

    public Map<String, Object> getBestDealsDeprecated(BasketFilter basketFilter) {
        List<ProductStoreDateKey> availableProductsCatalog = storeCatalogDao.getAvailableCatalogKeys(currentDate);
        Map<String, LocalDate> storeDateDiscount = discountDao.getAvailableDiscountDatePerStore(currentDate);

        Map<String, Set<Map<String, Object>>> dealsByProductName = new HashMap<>();
        Map<String, Float> totalPerStore = new HashMap<>();
        Map<String, Integer> productCountPerStore = new HashMap<>();

        availableProductsCatalog.forEach(key -> {
            String storeName = key.storeName();
            String productId = key.productId();
            StoreCatalog storeCatalog = storeCatalogDao.getStoreCatalog(key);
            Product product = productDao.getProduct(productId);
            String productName = product.getName();
            float price = storeCatalog.getPrice();

            //apply basket filtering if requested
            if (basketFilter == BasketFilter.USE && !productBasket.contains(productName))
                return;

            //check if there is a discount that applies
            LocalDate availableDiscountDate = storeDateDiscount.get(storeName);
            ProductStoreDateKey psdKey = new ProductStoreDateKey(productId, storeName, availableDiscountDate);
            Discount discount = discountDao.getDiscount(psdKey);

            boolean isDiscountApplied = PriceCalculator.isDiscountApplied(discount, currentDate);
            if (isDiscountApplied)
                price = PriceCalculator.applyDiscount(price, discount.getPercentage());

            float pricePerUnit = PriceCalculator.findPricePerUnit(price, product.getPackageQty(), product.getPackageUnit());

            if (basketFilter == BasketFilter.USE) {
                totalPerStore.put(storeName, totalPerStore.getOrDefault(storeName, 0f) + price);
                productCountPerStore.put(storeName, productCountPerStore.getOrDefault(storeName, 0) + 1);
            }

            //in sorted order by pricePerUnit
            dealsByProductName.putIfAbsent(productName, new TreeSet<>(Comparator.comparingDouble(deal -> (float) deal.get("pricePerUnit"))));

            dealsByProductName.get(productName).add(Map.of(
                    "storeName", storeName,
                    "pricePerUnit", pricePerUnit,
                    "price", price,
                    "currency", storeCatalog.getCurrency(),
                    "quantity", product.getPackageQty(),
                    "unit", product.getPackageUnit(),
                    "brand", product.getBrand(),
                    "standardUnit", Unit.getStandard(product.getPackageUnit()),
                    "isDiscountApplied", isDiscountApplied,
                    "discount", discount != null ? discount : "null"
            ));
        });

        float totalForBestDeals = 0;
        if (basketFilter == BasketFilter.USE)
            totalForBestDeals = (float) dealsByProductName.values().stream()
                    .mapToDouble(deals -> (float) ((TreeSet<Map<String, Object>>) deals).first().get("price"))
                    .sum();

        return Map.of(
                "deals", dealsByProductName,
                //add the following calculations only for products from the basket
                "totalPerStore", totalPerStore.isEmpty() ? "null" : totalPerStore,
                "totalForBestDeals", totalForBestDeals == 0 ? "null" : totalForBestDeals,
                "productCountPerStore", productCountPerStore.isEmpty() ? "null" : productCountPerStore
        );
    }
}
