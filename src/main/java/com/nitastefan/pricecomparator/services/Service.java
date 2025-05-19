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

    public Set<String> getAvailableProductNames() {
        Set<String> allAvailableProducts = new HashSet<>();

        List<ProductStoreDateKey> availableProductsCatalog = storeCatalogDao.getAvailableProductsKeys(currentDate);

        availableProductsCatalog.forEach(psdKey -> {
            Product product = productDao.getProduct(psdKey.productId());
            allAvailableProducts.add(product.getName());
        });

        return allAvailableProducts;
    }

    public Map<String, Object> getBestDeals(BasketFilter basketFilter) {
        List<ProductStoreDateKey> availableProductsCatalog = storeCatalogDao.getAvailableProductsKeys(currentDate);
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

                        Map<String, Object> secondInterval = Map.of(
                                "from", discount.getFromDate().toString(),
                                "to", !isDiscountToDateAfterEndDate ? discount.getToDate().toString() : endDate.toString(),
                                "pricePerUnit", reducedPricePerUnit,
                                "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                "currency", currency
                        );

                        result.get(product.getName()).get(store).add(secondInterval);

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
