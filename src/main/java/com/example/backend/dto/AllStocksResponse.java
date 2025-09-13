package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for all stocks response
 */
public class AllStocksResponse {
    
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @JsonProperty("totalStocks")
    private Integer totalStocks;
    
    @JsonProperty("stocks")
    private List<StockDataResponse> stocks;
    
    @JsonProperty("message")
    private String message;

    // Default constructor
    public AllStocksResponse() {}

    // Constructor
    public AllStocksResponse(LocalDate date, Integer totalStocks, List<StockDataResponse> stocks, String message) {
        this.date = date;
        this.totalStocks = totalStocks;
        this.stocks = stocks;
        this.message = message;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getTotalStocks() {
        return totalStocks;
    }

    public void setTotalStocks(Integer totalStocks) {
        this.totalStocks = totalStocks;
    }

    public List<StockDataResponse> getStocks() {
        return stocks;
    }

    public void setStocks(List<StockDataResponse> stocks) {
        this.stocks = stocks;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}