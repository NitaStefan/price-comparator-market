package com.nitastefan.pricecomparator.services;

import com.nitastefan.pricecomparator.dao.*;
import com.nitastefan.pricecomparator.dto.ProductTargetsRequest;
import com.nitastefan.pricecomparator.keys.ProductStoreDateKey;
import com.nitastefan.pricecomparator.models.*;
import com.nitastefan.pricecomparator.utils.BasketFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        ProductDao productDao = new ProductDao();
        StoreCatalogDao catalogDao = new StoreCatalogDao();
        DiscountDao discountDao = new DiscountDao();

        service = new Service(productDao, catalogDao, discountDao);
        service.setCurrentDate(LocalDate.of(2025, 5, 14));

        productDao.addProduct("P001", new Product("iaurt grecesc", "lactate", "Olympus", 1.0f, "l"));
        productDao.addProduct("P002", new Product("biscuiți cu unt", "gustări", "Milka", 500f, "g"));
        productDao.addProduct("P003", new Product("spaghetti nr.5", "paste făinoase", "Barilla", 500f, "g"));
        productDao.addProduct("P004", new Product("ulei floarea-soarelui", "alimente de bază", "Unisol", 600f, "ml"));
        productDao.addProduct("P005", new Product("suc portocale", "băuturi", "Cappy", 330f, "ml"));
        productDao.addProduct("P006", new Product("vin alb demisec", "băuturi", "Recas", 0.75f, "l"));
        productDao.addProduct("P007", new Product("ciocolată neagră 70%", "gustări", "Heidi", 100f, "g"));
        productDao.addProduct("P008", new Product("cartofi albi", "legume și fructe", "Generic", 1f, "kg"));
        productDao.addProduct("P009", new Product("ceapă galbenă", "legume și fructe", "Generic", 1f, "kg"));
        productDao.addProduct("P010", new Product("morcovi", "legume și fructe", "Generic", 0.5f, "kg"));
        productDao.addProduct("P011", new Product("suc portocale", "băuturi", "Santorini", 1f, "l"));
        productDao.addProduct("P012", new Product("cașcaval", "lactate", "K-Classic", 0.25f, "kg"));


        catalogDao.addStoreCatalog(new ProductStoreDateKey("P001", "kaufland", LocalDate.of(2025, 5, 12)), new StoreCatalog(9.1f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P003", "kaufland", LocalDate.of(2025, 5, 12)), new StoreCatalog(5.9f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P004", "kaufland", LocalDate.of(2025, 5, 14)), new StoreCatalog(5.9f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P007", "kaufland", LocalDate.of(2025, 5, 14)), new StoreCatalog(4.2f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P010", "kaufland", LocalDate.of(2025, 5, 14)), new StoreCatalog(2.5f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P012", "kaufland", LocalDate.of(2025, 5, 20)), new StoreCatalog(14.8f, "RON"));

        catalogDao.addStoreCatalog(new ProductStoreDateKey("P002", "lidl", LocalDate.of(2025, 5, 8)), new StoreCatalog(24.3f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P006", "lidl", LocalDate.of(2025, 5, 8)), new StoreCatalog(24.1f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P011", "lidl", LocalDate.of(2025, 5, 14)), new StoreCatalog(7.6f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P008", "lidl", LocalDate.of(2025, 5, 17)), new StoreCatalog(3.1f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P010", "lidl", LocalDate.of(2025, 5, 17)), new StoreCatalog(2.4f, "RON"));

        catalogDao.addStoreCatalog(new ProductStoreDateKey("P003", "mega_image", LocalDate.of(2025, 4, 28)), new StoreCatalog(6.2f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P008", "mega_image", LocalDate.of(2025, 5, 10)), new StoreCatalog(3.25f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P009", "mega_image", LocalDate.of(2025, 5, 10)), new StoreCatalog(3.05f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P009", "mega_image", LocalDate.of(2025, 5, 12)), new StoreCatalog(2.95f, "RON"));

        catalogDao.addStoreCatalog(new ProductStoreDateKey("P005", "profi", LocalDate.of(2025, 5, 9)), new StoreCatalog(7f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P006", "profi", LocalDate.of(2025, 5, 10)), new StoreCatalog(23.5f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P007", "profi", LocalDate.of(2025, 5, 10)), new StoreCatalog(4.15f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P009", "profi", LocalDate.of(2025, 5, 21)), new StoreCatalog(2.45f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P011", "profi", LocalDate.of(2025, 5, 21)), new StoreCatalog(7.25f, "RON"));
        catalogDao.addStoreCatalog(new ProductStoreDateKey("P012", "profi", LocalDate.of(2025, 5, 21)), new StoreCatalog(15.0f, "RON"));


        discountDao.addDiscount(
                new ProductStoreDateKey("P003", "kaufland", LocalDate.of(2025, 5, 10)),
                new Discount(LocalDate.of(2025, 5, 11), LocalDate.of(2025, 5, 13), (byte) 22)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P007", "kaufland", LocalDate.of(2025, 5, 15)),
                new Discount(LocalDate.of(2025, 5, 14), LocalDate.of(2025, 5, 16), (byte) 30)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P004", "kaufland", LocalDate.of(2025, 5, 15)),
                new Discount(LocalDate.of(2025, 5, 16), LocalDate.of(2025, 5, 16), (byte) 40)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P010", "kaufland", LocalDate.of(2025, 5, 15)),
                new Discount(LocalDate.of(2025, 5, 17), LocalDate.of(2025, 5, 22), (byte) 10)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P012", "kaufland", LocalDate.of(2025, 5, 21)),
                new Discount(LocalDate.of(2025, 5, 21), LocalDate.of(2025, 5, 25), (byte) 15)
        );

        discountDao.addDiscount(
                new ProductStoreDateKey("P006", "lidl", LocalDate.of(2025, 5, 13)),
                new Discount(LocalDate.of(2025, 5, 13), LocalDate.of(2025, 5, 13), (byte) 28)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P008", "lidl", LocalDate.of(2025, 5, 19)),
                new Discount(LocalDate.of(2025, 5, 20), LocalDate.of(2025, 5, 25), (byte) 9)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P010", "lidl", LocalDate.of(2025, 5, 19)),
                new Discount(LocalDate.of(2025, 4, 19), LocalDate.of(2025, 5, 21), (byte) 18)
        );

        discountDao.addDiscount(
                new ProductStoreDateKey("P003", "mega_image", LocalDate.of(2025, 4, 28)),
                new Discount(LocalDate.of(2025, 4, 28), LocalDate.of(2025, 5, 9), (byte) 5)
        );

        discountDao.addDiscount(
                new ProductStoreDateKey("P006", "profi", LocalDate.of(2025, 5, 12)),
                new Discount(LocalDate.of(2025, 5, 13), LocalDate.of(2025, 5, 15), (byte) 10)
        );
        discountDao.addDiscount(
                new ProductStoreDateKey("P007", "profi", LocalDate.of(2025, 5, 12)),
                new Discount(LocalDate.of(2025, 5, 14), LocalDate.of(2025, 5, 18), (byte) 14)
        );
    }


    @Test
    void getAvailableProductsByCategory_returnsCorrectlyGroupedProducts() {
        Map<String, Set<String>> result = service.getAvailableProductsByCategory();

        assertEquals(4, result.size());

        assertTrue(result.containsKey("băuturi"));
        assertTrue(result.containsKey("gustări"));
        assertTrue(result.containsKey("legume și fructe"));
        assertTrue(result.containsKey("alimente de bază"));

        assertEquals(Set.of("vin alb demisec", "suc portocale"), result.get("băuturi"));
        assertEquals(Set.of("ciocolată neagră 70%"), result.get("gustări"));
        assertEquals(Set.of("morcovi", "ceapă galbenă"), result.get("legume și fructe"));
        assertEquals(Set.of("ulei floarea-soarelui"), result.get("alimente de bază"));
    }


    @Test
    void getAvailableProductsByCategory_ignoresUnavailableDates() {
        service.setCurrentDate(LocalDate.of(2025, 4, 27)); // Before any available date

        Map<String, Set<String>> result = service.getAvailableProductsByCategory();
        assertTrue(result.isEmpty());
    }


    @Test
    void getAvailableProductsByCategory_withEmptyCatalog_returnsEmptyMap() {
        Service emptyService = new Service(new ProductDao(), new StoreCatalogDao(), new DiscountDao());

        Map<String, Set<String>> result = emptyService.getAvailableProductsByCategory();
        assertTrue(result.isEmpty());
    }

    @Test
    void getLatestDiscounts_returnsOnlyRecentDiscounts() {

        List<Map<String, Object>> result = service.getLatestDiscounts(3); // From 2025-05-11 to 2025-05-14

        assertEquals(3, result.size());

        // Check that expected discounts are present
        Set<String> productNames = new HashSet<>();
        for (Map<String, Object> discount : result) {
            productNames.add((String) discount.get("productName"));
        }

        assertTrue(productNames.contains("vin alb demisec"));
        assertTrue(productNames.contains("ciocolată neagră 70%"));
    }


    @Test
    void getLatestDiscounts_returnsEmptyListWhenNoRecentDiscounts() {
        List<Map<String, Object>> result = service.getLatestDiscounts(0);

        assertTrue(result.isEmpty());
    }

    @Test
    void getBestDeals_withoutBasketFilter_returnsDealsGroupedByProduct() {
        Map<String, Object> result = service.getBestDeals(BasketFilter.NOT_USE);

        assertNotNull(result.get("deals"));
        Map<String, Set<Map<String, Object>>> deals = (Map<String, Set<Map<String, Object>>>) result.get("deals");

        assertEquals(6, deals.size());

        assertTrue(deals.containsKey("ulei floarea-soarelui"));
        assertTrue(deals.containsKey("ciocolată neagră 70%"));
        assertTrue(deals.containsKey("vin alb demisec"));
        assertTrue(deals.containsKey("suc portocale"));
        assertTrue(deals.containsKey("morcovi"));
        assertTrue(deals.containsKey("ceapă galbenă"));

        // Check structure of a specific deal
        Set<Map<String, Object>> vinAlbDemisecDeal = deals.get("vin alb demisec");
        Set<Map<String, Object>> morcoviDeal = deals.get("morcovi");

        assertEquals(28.2f, (float) ((TreeSet<Map<String, Object>>) vinAlbDemisecDeal).first().get("pricePerUnit"));
        assertEquals(5f, (float) ((TreeSet<Map<String, Object>>) morcoviDeal).first().get("pricePerUnit"));
    }

    @Test
    void getBasketDealsByStore_returnsCorrectDealsGroupedByStore() {
        service.establishBasket(List.of("vin alb demisec", "ciocolată neagră 70%", "morcovi"));

        Map<String, Map<String, Object>> result = service.getBasketDealsByStore();

        assertEquals(2, result.size());
        assertTrue(result.containsKey("profi"));
        assertTrue(result.containsKey("kaufland"));

        Map<String, Object> profiDeals = result.get("profi");
        Map<String, Object> kauflandDeals = result.get("kaufland");

        // Check profi results for vin alb demisec
        assertTrue(profiDeals.containsKey("vin alb demisec"));
        Map<String, Object> vinDeal = (Map<String, Object>) profiDeals.get("vin alb demisec");
        assertEquals(28.2f, (float) vinDeal.get("pricePerUnit"), 0.01f);
        assertTrue((Boolean) vinDeal.get("isDiscountApplied"));

        // Check kaufland results for ciocolată neagră 70% and morcovi
        assertTrue(kauflandDeals.containsKey("ciocolată neagră 70%"));
        assertTrue(kauflandDeals.containsKey("morcovi"));

        Map<String, Object> morcoviDeal = (Map<String, Object>) kauflandDeals.get("morcovi");
        assertEquals(5.0f, (float) morcoviDeal.get("pricePerUnit"), 0.01f);
        assertFalse((Boolean) morcoviDeal.get("isDiscountApplied")); // discount starts after currentDate

        // Check total is present
        assertTrue(profiDeals.containsKey("total"));
        assertTrue(kauflandDeals.containsKey("total"));

        assertEquals(24.72f, (float) profiDeals.get("total"), 0.01f);
        assertEquals(6.7f, (float) kauflandDeals.get("total"), 0.01f);
    }

    @Test
    void getBasketDealsByStore_ignoresUnavailableProducts() {
        // Use a product that is not available on the current date
        service.establishBasket(List.of("cașcaval")); // cașcaval available only from 20 May

        Map<String, Map<String, Object>> result = service.getBasketDealsByStore();
        assertTrue(result.isEmpty());
    }

    @Test
    void checkWatchedProducts_returnsCorrectProductsBelowTargetPrice() {
        // Establish the targets
        service.establishTargets(List.of(
                new ProductTargetsRequest(4.2f, 350f, "g", "spaghetti nr.5"),
                new ProductTargetsRequest(10.0f, 200f, "g", "ciocolată neagră 70%")
        ));

        // Run the check
        Map<String, Map<String, Object>> result = service.checkWatchedProducts();

        // Validate
        assertEquals(1, result.size());
        assertTrue(result.containsKey("ciocolată neagră 70%"));

        Map<String, Object> chocolateDeal = result.get("ciocolată neagră 70%");
        float pricePerUnit = (float) chocolateDeal.get("pricePerUnit");

        // 4.2 RON / 100g = 42 RON/kg -> which is below 50 RON/kg target
        assertTrue(pricePerUnit <= 50.0f);
    }

    @Test
    void checkWatchedProducts_returnsEmptyWhenNoDealsMatchTarget() {
        // Set an unrealistic low target so nothing qualifies
        service.establishTargets(List.of(
                new ProductTargetsRequest(1.0f, 500f, "g", "spaghetti nr.5")
        ));

        Map<String, Map<String, Object>> result = service.checkWatchedProducts();

        assertTrue(result.isEmpty());
    }

}
