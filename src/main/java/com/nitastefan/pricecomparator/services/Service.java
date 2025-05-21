package com.nitastefan.pricecomparator.services;

import com.nitastefan.pricecomparator.dao.DiscountDao;
import com.nitastefan.pricecomparator.dao.ProductDao;
import com.nitastefan.pricecomparator.dao.StoreCatalogDao;
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

    private final ProductBasket productBasket;

    public Service(ProductDao productDao, StoreCatalogDao storeCatalogDao, DiscountDao discountDao) {
        this.currentDate = LocalDate.now();
        this.productDao = productDao;
        this.storeCatalogDao = storeCatalogDao;
        this.discountDao = discountDao;
        this.productBasket = new ProductBasket();
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

    public Map<String, Set<String>> getAvailableProductsByCategory() {
        Map<String, Set<String>> allAvailableProducts = new HashMap<>();

        List<ProductStoreDateKey> availableProductsCatalog = storeCatalogDao.getAvailableProductsKeys(currentDate);

        availableProductsCatalog.forEach(psdKey -> {
            Product product = productDao.getProduct(psdKey.productId());

            allAvailableProducts.putIfAbsent(product.getCategory(), new HashSet<>());
            allAvailableProducts.get(product.getCategory()).add(product.getName());
        });

        return allAvailableProducts;
    }

    public Set<String> getAvailableProductNames() {
        Set<String> productsNames = new HashSet<>();
        List<ProductStoreDateKey> availableProductsCatalog = storeCatalogDao.getAvailableProductsKeys(currentDate);

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

            if (discount.getFromDate().isAfter(currentDate.minusDays(lastDays)) && !discount.getFromDate().isAfter(currentDate)) {
                result.add(Map.of(
                        "store", key.storeName(),
                        "productName", product.getName(),
                        "discountPercentage", discount.getPercentage(),
                        "discountStartDate", discount.getFromDate().toString(),
                        "brand", product.getBrand(),
                        "qty", product.getPackageQty(),
                        "unit", product.getPackageUnit()
                ));
            }
        });

        return result;
    }

//    public Map<String, Object> getBestDeals(BasketFilter basketFilter) {
//        List<ProductStoreDateKey> availableProductsCatalog = storeCatalogDao.getAvailableProductsKeys(currentDate);
//        Map<String, LocalDate> storeDateDiscount = discountDao.getAvailableDiscountDatePerStore(currentDate);
//
//        Map<String, Set<Map<String, Object>>> dealsByProductName = new HashMap<>();
//        Map<String, Float> totalPerStore = new HashMap<>();
//        Map<String, Integer> productCountPerStore = new HashMap<>();
//
//        availableProductsCatalog.forEach(key -> {
//            String storeName = key.storeName();
//            String productId = key.productId();
//            StoreCatalog storeCatalog = storeCatalogDao.getStoreCatalog(key);
//            Product product = productDao.getProduct(productId);
//            String productName = product.getName();
//            float price = storeCatalog.getPrice();
//
//            //apply basket filtering if requested
//            if (basketFilter == BasketFilter.USE && !productBasket.contains(productName)) return;
//
//            //check if there is a discount that applies
//            LocalDate availableDiscountDate = storeDateDiscount.get(storeName);
//            ProductStoreDateKey psdKey = new ProductStoreDateKey(productId, storeName, availableDiscountDate);
//            Discount discount = discountDao.getDiscount(psdKey);
//
//            boolean isDiscountApplied = PriceCalculator.isDiscountApplied(discount, currentDate);
//            if (isDiscountApplied)
//                price = PriceCalculator.applyDiscount(price, discount.getPercentage());
//
//            float pricePerUnit = PriceCalculator.findPricePerUnit(price, product.getPackageQty(), product.getPackageUnit());
//
//            if (basketFilter == BasketFilter.USE) {
//                totalPerStore.put(storeName, totalPerStore.getOrDefault(storeName, 0f) + price);
//                productCountPerStore.put(storeName, productCountPerStore.getOrDefault(storeName, 0) + 1);
//            }
//
//            //in sorted order by pricePerUnit
//            dealsByProductName.putIfAbsent(productName, new TreeSet<>(Comparator.comparingDouble(deal -> (float) deal.get("pricePerUnit"))));
//
//            dealsByProductName.get(productName).add(Map.of(
//                    "storeName", storeName,
//                    "pricePerUnit", pricePerUnit,
//                    "price", price,
//                    "currency", storeCatalog.getCurrency(),
//                    "quantity", product.getPackageQty(),
//                    "unit", product.getPackageUnit(),
//                    "brand", product.getBrand(),
//                    "standardUnit", Unit.getStandard(product.getPackageUnit()),
//                    "isDiscountApplied", isDiscountApplied,
//                    "discount", discount != null ? discount : "null"
//            ));
//        });
//
//        float totalForBestDeals = 0;
//        if (basketFilter == BasketFilter.USE)
//            totalForBestDeals = (float) dealsByProductName.values().stream()
//                    .mapToDouble(deals -> (float) ((TreeSet<Map<String, Object>>) deals).first().get("price"))
//                    .sum();
//
//        return Map.of(
//                "deals", dealsByProductName,
//                //add the following calculations only for products from the basket
//                "totalPerStore", totalPerStore.isEmpty() ? "null" : totalPerStore,
//                "totalForBestDeals", totalForBestDeals == 0 ? "null" : totalForBestDeals,
//                "productCountPerStore", productCountPerStore.isEmpty() ? "null" : productCountPerStore
//        );
//    }

    public Map<String, Object> getBestDeals(BasketFilter basketFilter) {
        List<ProductStoreDateKey> availableCatalog = storeCatalogDao.getAvailableProductsKeys(currentDate);
        Map<String, LocalDate> storeDiscountDates = discountDao.getAvailableDiscountDatePerStore(currentDate);

        Map<String, Set<Map<String, Object>>> dealsByProduct = buildDealsByProduct(availableCatalog, storeDiscountDates, basketFilter);

        Float totalForBestDeals = basketFilter == BasketFilter.USE ? calculateBestDealTotal(dealsByProduct) : null;
//        Map<String, Float> totalPerStore = basketFilter == BasketFilter.USE ? calculateTotalPerStore(dealsByProduct) : null;
//        Map<String, Integer> productCountPerStore = basketFilter == BasketFilter.USE ? calculateProductCountPerStore(dealsByProduct) : null;

        return Map.of(
                "deals", dealsByProduct,
                "totalForBestDeals", totalForBestDeals == null ? "null" : totalForBestDeals
//                "totalPerStore", totalPerStore == null ? "null" : totalPerStore,
//                "productCountPerStore", productCountPerStore == null ? "null" : productCountPerStore
        );
    }

    private Map<String, Set<Map<String, Object>>> buildDealsByProduct(
            List<ProductStoreDateKey> availableCatalog,
            Map<String, LocalDate> storeDiscountDates,
            BasketFilter basketFilter
    ) {
        Map<String, Set<Map<String, Object>>> deals = new HashMap<>();

        for (ProductStoreDateKey key : availableCatalog) {
            String store = key.storeName();
            String productId = key.productId();

            Product product = productDao.getProduct(productId);
            String name = product.getName();
            StoreCatalog catalog = storeCatalogDao.getStoreCatalog(key);
            float price = catalog.getPrice();

            if (basketFilter == BasketFilter.USE && !productBasket.contains(name)) continue;

            LocalDate discountDate = storeDiscountDates.get(store);
            Discount discount = discountDao.getDiscount(new ProductStoreDateKey(productId, store, discountDate));
            boolean hasDiscount = PriceCalculator.isDiscountApplied(discount, currentDate);

            if (hasDiscount)
                price = PriceCalculator.applyDiscount(price, discount.getPercentage());

            float pricePerUnit = PriceCalculator.findPricePerUnit(price, product.getPackageQty(), product.getPackageUnit());

            deals.putIfAbsent(name, new TreeSet<>(Comparator.comparingDouble(d -> (float) d.get("pricePerUnit"))));

            Map<String, Object> deal = Map.of(
                    "storeName", store,
                    "price", price,
                    "pricePerUnit", pricePerUnit,
                    "currency", catalog.getCurrency(),
                    "quantity", product.getPackageQty(),
                    "unit", product.getPackageUnit(),
                    "brand", product.getBrand(),
                    "standardUnit", Unit.getStandard(product.getPackageUnit()),
                    "isDiscountApplied", hasDiscount,
                    "discount", discount != null ? discount : "null"
            );

            deals.get(name).add(deal);
        }

        return deals;
    }

    //todo: create separate method that groups deals by store

    private Float calculateBestDealTotal(Map<String, Set<Map<String, Object>>> dealsByProduct) {
        return (float) dealsByProduct.values().stream()
                .map(deals -> ((TreeSet<Map<String, Object>>) deals).first())
                .mapToDouble(d -> (float) d.get("price"))
                .sum();
    }

//    private Map<String, Float> calculateTotalPerStore(Map<String, Set<Map<String, Object>>> dealsByProduct) {
//        Map<String, Float> totals = new HashMap<>();
//
//        for (Set<Map<String, Object>> productDeals : dealsByProduct.values()) {
//            Map<String, Object> best = ((TreeSet<Map<String, Object>>) productDeals).first();
//            String store = (String) best.get("storeName");
//            float price = (float) best.get("price");
//            totals.put(store, totals.getOrDefault(store, 0f) + price);
//        }
//
//        return totals;
//    }
//
//    private Map<String, Integer> calculateProductCountPerStore(Map<String, Set<Map<String, Object>>> dealsByProduct) {
//        Map<String, Integer> counts = new HashMap<>();
//
//        for (Set<Map<String, Object>> productDeals : dealsByProduct.values()) {
//            Map<String, Object> best = ((TreeSet<Map<String, Object>>) productDeals).first();
//            String store = (String) best.get("storeName");
//            counts.put(store, counts.getOrDefault(store, 0) + 1);
//        }
//
//        return counts;
//    }

    //other variations can be written
    public Map<String, Map<String, List<Object>>> getPriceTimeline() {
        Map<String, TreeSet<LocalDate>> catalogSortedDates = storeCatalogDao.getSortedDatesByStore();

        Map<String, Map<String, List<Object>>> result = new HashMap<>();

        catalogSortedDates.forEach((store, catalogDates) -> {
            List<LocalDate> dateList = new ArrayList<>(catalogDates);

            for (int i = 0; i < dateList.size(); i++) {
                LocalDate startDate = dateList.get(i);
                //treat endDate as inclusive
                LocalDate endDate = i + 1 != dateList.size() ? dateList.get(i + 1).minusDays(1) : startDate.plusWeeks(2);

                StoreDate storeDate = new StoreDate(store, startDate);
                List<ProductStoreDateKey> catalogKeys = storeCatalogDao.getCatalogKeysForStoreDate(storeDate);

                catalogKeys.forEach(catalogKey -> {
                    StoreCatalog catalog = storeCatalogDao.getStoreCatalog(catalogKey);
                    float price = catalog.getPrice();
                    String currency = catalog.getCurrency();
                    Product product = productDao.getProduct(catalogKey.productId());
                    Map<String, LocalDate> storeDateDiscount = discountDao.getAvailableDiscountDatePerStore(endDate);
                    LocalDate discountDate = storeDateDiscount.get(catalogKey.storeName());

                    ProductStoreDateKey discountKey = new ProductStoreDateKey(catalogKey.productId(), catalogKey.storeName(), discountDate);
                    Discount discount = discountDao.getDiscount(discountKey);

                    result.putIfAbsent(product.getName(), new HashMap<>());
                    result.get(product.getName()).putIfAbsent(store, new ArrayList<>());

                    float pricePerUnit = PriceCalculator.findPricePerUnit(price, product.getPackageQty(), product.getPackageUnit());

                    if (discount != null && !discount.getFromDate().isBefore(startDate) && !discount.getToDate().isAfter(endDate)) {
                        float reducedPricePerUnit = PriceCalculator.applyDiscount(pricePerUnit, discount.getPercentage());

                        //might start directly reduced
                        if (discount.getFromDate().isAfter(startDate)) {
                            Map<String, Object> firstInterval = Map.of(
                                    "from", startDate.toString(),
                                    "to", discount.getFromDate().minusDays(1).toString(),
                                    "pricePerUnit", pricePerUnit,
                                    "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                    "currency", currency
                            );
                            result.get(product.getName()).get(store).add(firstInterval);
                        }

                        boolean isDiscountToDateAfterEndDate = discount.getToDate().isAfter(endDate);

                        //category, unit, quantity, brand, price can also be added
                        Map<String, Object> secondInterval = Map.of(
                                "from", discount.getFromDate().toString(),
                                "to", !isDiscountToDateAfterEndDate ? discount.getToDate().toString() : endDate.toString(),
                                "pricePerUnit", reducedPricePerUnit,
                                "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                "currency", currency
                        );

                        result.get(product.getName()).get(store).add(secondInterval);

                        //in case the discount does not cover the whole interval
                        if (discount.getToDate().isBefore(endDate)) {
                            Map<String, Object> thirdInterval = Map.of(
                                    "from", discount.getToDate().plusDays(1).toString(),
                                    "to", endDate.toString(),
                                    "pricePerUnit", pricePerUnit,
                                    "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                    "currency", currency
                            );
                            result.get(product.getName()).get(store).add(thirdInterval);
                        }
                    } else {
                        Map<String, Object> testInterval = Map.of(
                                "from", startDate.toString(),
                                "to", endDate.toString(),
                                "pricePerUnit", pricePerUnit,
                                "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                "currency", currency
                        );

                        result.get(product.getName()).get(store).add(testInterval);
                    }

                });
            }
        });

        return result;
    }

    public void establishBasket(List<String> productNames) {
        productBasket.establishBasket(productNames);
    }
}
