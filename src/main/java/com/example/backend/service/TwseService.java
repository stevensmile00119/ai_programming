package com.example.backend.service;

import com.example.backend.dto.StockDataResponse;
import com.example.backend.model.TwseStockData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.time.LocalDate;

/**
 * Service for fetching Taiwan Stock Exchange data from STOCK_DAY_ALL API
 */
@Service
public class TwseService {

    private final RestTemplate restTemplate;
    private static final String TWSE_API_URL = "https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL";

    public TwseService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetch all stock data for the current trading day
     * 
     * @return List of TwseStockData for all stocks
     * @throws RuntimeException if API call fails
     */
    public List<TwseStockData> getAllStockData() {
        try {
            TwseStockData[] response = restTemplate.getForObject(TWSE_API_URL, TwseStockData[].class);
            
            if (response == null || response.length == 0) {
                throw new RuntimeException("No data returned from TWSE API");
            }
            
            return Arrays.asList(response);
            
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch stock data from TWSE API: " + e.getMessage(), e);
        }
    }

    /**
     * Get stock data for a specific stock code
     * 
     * @param stockCode Stock code (e.g., "2330", "0050")
     * @return TwseStockData for the specific stock, or null if not found
     * @throws RuntimeException if API call fails
     */
    public TwseStockData getStockData(String stockCode) {
        validateStockCode(stockCode);
        
        List<TwseStockData> allStocks = getAllStockData();
        
        return allStocks.stream()
                .filter(stock -> stockCode.equals(stock.getCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Convert TwseStockData to StockDataResponse
     */
    public StockDataResponse convertToStockDataResponse(TwseStockData stockData) {
        if (stockData == null) {
            return null;
        }

        try {
            return new StockDataResponse(
                stockData.getCode(),
                stockData.getName(),
                stockData.parseDate(),
                stockData.parseOpeningPrice(),
                stockData.parseHighestPrice(),
                stockData.parseLowestPrice(),
                stockData.parseClosingPrice(),
                stockData.getChange(),
                stockData.parseTradeVolume(),
                stockData.parseTradeValue(),
                stockData.parseTransaction()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse stock data for code: " + stockData.getCode(), e);
        }
    }

    /**
     * Convert list of TwseStockData to list of StockDataResponse
     */
    public List<StockDataResponse> convertToStockDataResponseList(List<TwseStockData> stockDataList) {
        return stockDataList.stream()
                .map(this::convertToStockDataResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get the trading date from the stock data
     * Assumes all stocks have the same date, takes from first stock
     */
    public LocalDate getTradingDate() {
        List<TwseStockData> allStocks = getAllStockData();
        
        if (allStocks.isEmpty()) {
            throw new RuntimeException("No stock data available");
        }
        
        return allStocks.get(0).parseDate();
    }

    /**
     * Validate stock code input
     */
    private void validateStockCode(String stockCode) {
        if (stockCode == null || stockCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock code cannot be empty");
        }
        
        // Stock codes can be 4 digits (e.g., "2330") or 4 characters with leading zeros (e.g., "0050") 
        if (!stockCode.matches("\\d{4}")) {
            throw new IllegalArgumentException("Stock code must be 4 digits");
        }
    }
}