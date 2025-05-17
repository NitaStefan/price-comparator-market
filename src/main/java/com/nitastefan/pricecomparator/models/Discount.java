package com.nitastefan.pricecomparator.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class Discount {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")

    private LocalDate toDate;

    byte percentage;

    public Discount(LocalDate fromDate, LocalDate toDate, byte percentage) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.percentage = percentage;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public byte getPercentage() {
        return percentage;
    }

    public void setPercentage(byte percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", percentage=" + percentage +
                '}';
    }
}
