package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model representing stock data from TWSE STOCK_DAY_ALL API
 * This model matches the response format from https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL
 */
public class TwseStockData {

    @JsonProperty("Date")
    private String date; // ROC date format: YYYMMDD (e.g., "1140912")
    
    @JsonProperty("Code")
    private String code; // Stock code (e.g., "0050")
    
    @JsonProperty("Name") 
    private String name; // Stock name (e.g., "元大台灣50")
    
    @JsonProperty("TradeVolume")
    private String tradeVolume; // Trade volume as string (may contain commas)
    
    @JsonProperty("TradeValue")
    private String tradeValue; // Trade value as string (may contain commas)
    
    @JsonProperty("OpeningPrice")
    private String openingPrice; // Opening price as string
    
    @JsonProperty("HighestPrice")
    private String highestPrice; // Highest price as string
    
    @JsonProperty("LowestPrice")
    private String lowestPrice; // Lowest price as string
    
    @JsonProperty("ClosingPrice")
    private String closingPrice; // Closing price as string
    
    @JsonProperty("Change")
    private String change; // Price change as string
    
    @JsonProperty("Transaction")
    private String transaction; // Number of transactions as string

    // Default constructor
    public TwseStockData() {}

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTradeVolume() {
        return tradeVolume;
    }

    public void setTradeVolume(String tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public String getTradeValue() {
        return tradeValue;
    }

    public void setTradeValue(String tradeValue) {
        this.tradeValue = tradeValue;
    }

    public String getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(String openingPrice) {
        this.openingPrice = openingPrice;
    }

    public String getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(String highestPrice) {
        this.highestPrice = highestPrice;
    }

    public String getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(String lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public String getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(String closingPrice) {
        this.closingPrice = closingPrice;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    /**
     * Convert ROC date to LocalDate
     * ROC format: YYYMMDD (e.g., "1140912" -> 2025-09-12)
     */
    public LocalDate parseDate() {
        if (date == null || date.length() != 7) {
            throw new IllegalArgumentException("Invalid ROC date format: " + date);
        }
        
        int rocYear = Integer.parseInt(date.substring(0, 3));
        int month = Integer.parseInt(date.substring(3, 5));
        int day = Integer.parseInt(date.substring(5, 7));
        
        // Convert ROC year to AD year
        int adYear = rocYear + 1911;
        
        return LocalDate.of(adYear, month, day);
    }

    /**
     * Parse trade volume to Long
     */
    public Long parseTradeVolume() {
        return parseStringToLong(tradeVolume);
    }

    /**
     * Parse trade value to Long  
     */
    public Long parseTradeValue() {
        return parseStringToLong(tradeValue);
    }

    /**
     * Parse opening price to BigDecimal
     */
    public BigDecimal parseOpeningPrice() {
        return parseStringToBigDecimal(openingPrice);
    }

    /**
     * Parse highest price to BigDecimal
     */
    public BigDecimal parseHighestPrice() {
        return parseStringToBigDecimal(highestPrice);
    }

    /**
     * Parse lowest price to BigDecimal
     */
    public BigDecimal parseLowestPrice() {
        return parseStringToBigDecimal(lowestPrice);
    }

    /**
     * Parse closing price to BigDecimal
     */
    public BigDecimal parseClosingPrice() {
        return parseStringToBigDecimal(closingPrice);
    }

    /**
     * Parse transaction count to Integer
     */
    public Integer parseTransaction() {
        return parseStringToInteger(transaction);
    }

    // Helper methods
    private Long parseStringToLong(String value) {
        if (value == null || value.trim().isEmpty() || "--".equals(value)) {
            return 0L;
        }
        return Long.parseLong(value.replace(",", ""));
    }

    private BigDecimal parseStringToBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || "--".equals(value)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.replace(",", ""));
    }

    private Integer parseStringToInteger(String value) {
        if (value == null || value.trim().isEmpty() || "--".equals(value)) {
            return 0;
        }
        return Integer.parseInt(value.replace(",", ""));
    }
}