package com.nitastefan.pricecomparator.services;

public class SimpleCalculator {

    public int add(int a, int b){
        if(a == 0) throw new IllegalArgumentException("a cannot be 0");
        return a+b;
    }
}
