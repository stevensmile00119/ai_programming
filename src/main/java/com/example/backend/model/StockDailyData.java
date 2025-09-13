package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * Model representing daily stock data for a single trading day
 */
public class StockDailyData {
    
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate date;
    
    @JsonProperty("tradeVolume")
    private Long tradeVolume;
    
    @JsonProperty("tradeValue")
    private Long tradeValue;
    
    @JsonProperty("openingPrice")
    private BigDecimal openingPrice;
    
    @JsonProperty("highestPrice") 
    private BigDecimal highestPrice;
    
    @JsonProperty("lowestPrice")
    private BigDecimal lowestPrice;
    
    @JsonProperty("closingPrice")
    private BigDecimal closingPrice;
    
    @JsonProperty("change")
    private String change;
    
    @JsonProperty("transaction")
    private Integer transaction;

    // Default constructor
    public StockDailyData() {}

    // Constructor with all fields
    public StockDailyData(LocalDate date, Long tradeVolume, Long tradeValue, 
                         BigDecimal openingPrice, BigDecimal highestPrice, 
                         BigDecimal lowestPrice, BigDecimal closingPrice, 
                         String change, Integer transaction) {
        this.date = date;
        this.tradeVolume = tradeVolume;
        this.tradeValue = tradeValue;
        this.openingPrice = openingPrice;
        this.highestPrice = highestPrice;
        this.lowestPrice = lowestPrice;
        this.closingPrice = closingPrice;
        this.change = change;
        this.transaction = transaction;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getTradeVolume() {
        return tradeVolume;
    }

    public void setTradeVolume(Long tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public Long getTradeValue() {
        return tradeValue;
    }

    public void setTradeValue(Long tradeValue) {
        this.tradeValue = tradeValue;
    }

    public BigDecimal getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(BigDecimal openingPrice) {
        this.openingPrice = openingPrice;
    }

    public BigDecimal getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(BigDecimal highestPrice) {
        this.highestPrice = highestPrice;
    }

    public BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(BigDecimal lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public BigDecimal getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(BigDecimal closingPrice) {
        this.closingPrice = closingPrice;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public Integer getTransaction() {
        return transaction;
    }

    public void setTransaction(Integer transaction) {
        this.transaction = transaction;
    }
}