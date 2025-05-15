//package com.nitastefan.pricecomparator.services;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class StoreServiceTest {
//
//    @Mock
//    private StoreDao storeDao;
//
//    @InjectMocks
//    private StoreServiceImpl storeService;
//
//    @Test
//    void testGetAvailableProductsFromStore_MidDateSelection() {
//        // Arrange
//        Store kaufland = new Store("Kaufland");
//        LocalDate date1 = LocalDate.of(2025, 5, 7);
//        LocalDate date2 = LocalDate.of(2025, 5, 10);
//        LocalDate date3 = LocalDate.of(2025, 5, 14);
//        LocalDate testDate = LocalDate.of(2025, 5, 13);
//
//        // Products for each date
//        List<Product> productsOnDate1 = List.of(new Product("P001", "Milk", "Dairy", "BrandA", 1.0f, "l", 10.0f, "RON"));
//        List<Product> productsOnDate2 = List.of(new Product("P002", "Bread", "Bakery", "BrandB", 0.5f, "pc", 5.0f, "RON"));
//        List<Product> productsOnDate3 = List.of(new Product("P003", "Eggs", "Dairy", "BrandC", 0.2f, "doz", 12.0f, "RON"));
//
//        kaufland.addProducts(date1, productsOnDate1);
//        kaufland.addProducts(date2, productsOnDate2);
//        kaufland.addProducts(date3, productsOnDate3);
//
//        // Mocking StoreDao behavior
//        when(storeDao.getStoreByName("Kaufland")).thenReturn(kaufland);
//
//        // Act
//        storeService.setCurrentDate(testDate);
//        List<Product> availableProducts = storeService.getAvailableProductsFromStore("Kaufland");
//
//        // Assert
//        assertEquals(1, availableProducts.size());
//        assertEquals("P002", availableProducts.getFirst().getProductId());
//        assertEquals("Bread", availableProducts.getFirst().getProductName());
//
//        // Verify interaction
//        verify(storeDao, times(1)).getStoreByName("Kaufland");
//    }
//}