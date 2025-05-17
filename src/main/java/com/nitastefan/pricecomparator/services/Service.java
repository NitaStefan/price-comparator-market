package com.nitastefan.pricecomparator.services;

import com.nitastefan.pricecomparator.dao.DiscountDao;
import com.nitastefan.pricecomparator.dao.ProductDao;
import com.nitastefan.pricecomparator.dao.StoreCatalogDao;
import com.nitastefan.pricecomparator.models.Product;
import com.nitastefan.pricecomparator.utils.PriceAndUnitCalculator;
import com.nitastefan.pricecomparator.utils.Unit;

import java.time.LocalDate;
import java.util.*;

public class Service {
    private LocalDate currentDate;

    private final ProductDao productDao;

    private final StoreCatalogDao storeCatalogDao;

    private final DiscountDao discountDao;

    public Service(ProductDao productDao, StoreCatalogDao storeCatalogDao, DiscountDao discountDao) {
        this.currentDate = LocalDate.now();
        this.productDao = productDao;
        this.storeCatalogDao = storeCatalogDao;
        this.discountDao = discountDao;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    public Set<String> getAvailableProductNames() {
        Map<String, LocalDate> storeDate = storeCatalogDao.getCurrentDateOfProductsPerStore(currentDate);

        Set<String> allAvailableProducts = new HashSet<>();

        storeDate.forEach((storeName, latestAvailableDate) -> {
            Map<String, Map<String, Object>> productsForStoreAndDate = storeCatalogDao
                    .getProductsForStoreAndDate(storeName, latestAvailableDate);

            allAvailableProducts.addAll(productsForStoreAndDate.keySet());
        });

        return productDao.getUniquesProductNames(allAvailableProducts);
    }

    public Object getBestDeals() {
        Map<String, LocalDate> storeDate = storeCatalogDao.getCurrentDateOfProductsPerStore(currentDate);

        Map<String, Map<String, Object>> dealsByProductName = new HashMap<>();

        storeDate.forEach((storeName, latestAvailableDate) -> {
            Map<String, Map<String, Object>> productsForStoreAndDate = storeCatalogDao
                    .getProductsForStoreAndDate(storeName, latestAvailableDate);

            productsForStoreAndDate.keySet().forEach(
                    key -> {
                        // key -> productId
                        float price = (float) productsForStoreAndDate.get(key).get("price");
                        String currency = (String) productsForStoreAndDate.get(key).get("currency");
                        Product product = productDao.getProduct(key);
                        String productName = product.getName();
                        float pricePerUnit = PriceAndUnitCalculator.pricePerUnit(price, product.getPackageQty(), product.getPackageUnit());

                        //todo: verify if discount is available
                        if (!dealsByProductName.containsKey(productName) || (float) dealsByProductName.get(productName).get("pricePerUnit") < pricePerUnit)
                            dealsByProductName.put(productName, Map.of(
                                    "storeName", storeName,
                                    "pricePerUnit", pricePerUnit,
                                    "currency", currency,
                                    "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                    "qty", product.getPackageQty(),
                                    "unit", product.getPackageUnit(),
                                    "price", price
                                    //todo: add discount if applicable discount -> Discount object
                            ));
                    }
            );
        });

        return dealsByProductName;
    }

}
