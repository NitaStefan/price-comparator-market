package com.nitastefan.pricecomparator.keys;

import java.time.LocalDate;

public record ProductStoreDateKey(String productId, String storeName, LocalDate date) {
}
