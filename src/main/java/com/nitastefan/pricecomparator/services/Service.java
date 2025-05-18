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

    //todo: check if there is a way to compute deals for the items from the basket
    public Object getBestDeals(BasketFilter basketFilter) {
        List<ProductStoreDateKey> availableProductsCatalog = storeCatalogDao.getAvailableProductsKeys(currentDate);

        Map<String, LocalDate> storeDateDiscount = discountDao.getAvailableDiscountDatePerStore(currentDate);

        Map<String, Set<Map<String, Object>>> dealsByProductName = new HashMap<>();

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

            //todo: compute total for best deals and then per store per product name

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

        return dealsByProductName;
    }

    public void establishBasket(List<String> productNames) {
        productBasket.establishBasket(productNames);
    }
}
