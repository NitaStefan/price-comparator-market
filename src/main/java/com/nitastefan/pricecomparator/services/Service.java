package com.nitastefan.pricecomparator.services;

import com.nitastefan.pricecomparator.dao.DiscountDao;
import com.nitastefan.pricecomparator.dao.ProductDao;
import com.nitastefan.pricecomparator.dao.StoreCatalogDao;
import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.Discount;
import com.nitastefan.pricecomparator.models.Product;
import com.nitastefan.pricecomparator.models.StoreCatalog;
import com.nitastefan.pricecomparator.utils.PriceCalculator;
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
            Map<String, StoreCatalog> productsForStoreAndDate = storeCatalogDao
                    .getProductsForStoreAndDate(storeName, latestAvailableDate);

            allAvailableProducts.addAll(productsForStoreAndDate.keySet());
        });

        return productDao.getUniquesProductNames(allAvailableProducts);
    }

    //todo: split into best deals for a set of products by name (to reuse for basket)
    public Object getBestDeals() {
        Map<String, LocalDate> storeDateCatalog = storeCatalogDao.getCurrentDateOfProductsPerStore(currentDate);
        Map<String, LocalDate> storeDateDiscount = discountDao.getAvailableDiscountDatePerStore(currentDate);

        Map<String, Map<String, Object>> dealsByProductName = new HashMap<>();

        storeDateCatalog.forEach((storeName, latestAvailableDate) -> {
            Map<String, StoreCatalog> productsForStoreAndDate = storeCatalogDao
                    .getProductsForStoreAndDate(storeName, latestAvailableDate);

            productsForStoreAndDate.keySet().forEach(
                    key -> {
                        // key -> productId
                        StoreCatalog storeCatalog = productsForStoreAndDate.get(key);
                        Product product = productDao.getProduct(key);
                        String productName = product.getName();
                        float price = storeCatalog.getPrice();

                        //check if there is a discount that applies
                        LocalDate discountDateForStore = storeDateDiscount.get(storeName);
                        ProductStoreDateKey psdKey = new ProductStoreDateKey(key, storeName, discountDateForStore);
                        Discount discount = discountDao.getDiscount(psdKey);

                        boolean isDiscountApplied = PriceCalculator.isDiscountApplied(discount, currentDate);
                        if (isDiscountApplied)
                            price = PriceCalculator.applyDiscount(price, discount.getPercentage());

                        float pricePerUnit = PriceCalculator.findPricePerUnit(price, product.getPackageQty(), product.getPackageUnit());

                        if (!dealsByProductName.containsKey(productName) || (float) dealsByProductName.get(productName).get("pricePerUnit") > pricePerUnit)
                            dealsByProductName.put(productName, Map.of(
                                    "storeName", storeName,
                                    "pricePerUnit", pricePerUnit,
                                    "price", price,
                                    "currency", storeCatalog.getCurrency(),
                                    "quantity", product.getPackageQty(),
                                    "unit", product.getPackageUnit(),
                                    "standardUnit", Unit.getStandard(product.getPackageUnit()),
                                    "isDiscountApplied", isDiscountApplied,
                                    "discount", discount != null ? discount : "null"
                            ));
                    }
            );
        });

        return dealsByProductName;
    }

}
