package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for individual stock data response
 */
public class StockDataResponse {
    
    @JsonProperty("stockCode")
    private String stockCode;
    
    @JsonProperty("stockName")
    private String stockName;
    
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
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
    
    @JsonProperty("tradeVolume")
    private Long tradeVolume;
    
    @JsonProperty("tradeValue")
    private Long tradeValue;
    
    @JsonProperty("transaction")
    private Integer transaction;

    // Default constructor
    public StockDataResponse() {}

    // Constructor with all fields
    public StockDataResponse(String stockCode, String stockName, LocalDate date, 
                           BigDecimal openingPrice, BigDecimal highestPrice, 
                           BigDecimal lowestPrice, BigDecimal closingPrice, 
                           String change, Long tradeVolume, Long tradeValue, 
                           Integer transaction) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.date = date;
        this.openingPrice = openingPrice;
        this.highestPrice = highestPrice;
        this.lowestPrice = lowestPrice;
        this.closingPrice = closingPrice;
        this.change = change;
        this.tradeVolume = tradeVolume;
        this.tradeValue = tradeValue;
        this.transaction = transaction;
    }

    // Getters and Setters
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public Integer getTransaction() {
        return transaction;
    }

    public void setTransaction(Integer transaction) {
        this.transaction = transaction;
    }
}