package com.example.backend.dto;

import com.example.backend.model.StockDailyData;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for Stock Query Response
 */
public class StockQueryResponse {
    
    @JsonProperty("stockCode")
    private String stockCode;
    
    @JsonProperty("stockName")
    private String stockName;
    
    @JsonProperty("queryPeriod")
    private String queryPeriod;
    
    @JsonProperty("data")
    private List<StockDailyData> data;
    
    @JsonProperty("message")
    private String message;

    // Default constructor
    public StockQueryResponse() {}

    // Constructor
    public StockQueryResponse(String stockCode, String stockName, String queryPeriod, List<StockDailyData> data, String message) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.queryPeriod = queryPeriod;
        this.data = data;
        this.message = message;
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

    public String getQueryPeriod() {
        return queryPeriod;
    }

    public void setQueryPeriod(String queryPeriod) {
        this.queryPeriod = queryPeriod;
    }

    public List<StockDailyData> getData() {
        return data;
    }

    public void setData(List<StockDailyData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}