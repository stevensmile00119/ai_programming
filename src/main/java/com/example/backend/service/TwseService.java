package com.example.backend.service;

import com.example.backend.model.StockDailyData;
import com.example.backend.model.TwseApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for fetching Taiwan Stock Exchange data
 */
@Service
public class TwseService {

    private final RestTemplate restTemplate;
    private static final String TWSE_API_URL = "https://openapi.twse.com.tw/exchangeReport/STOCK_DAY";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TWSE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public TwseService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Query stock data for a specific date range
     * 
     * @param stockCode Stock code (e.g., "2330")
     * @param startDate Start date in YYYYMMDD format
     * @param endDate End date in YYYYMMDD format
     * @return List of StockDailyData
     * @throws RuntimeException if API call fails
     */
    public List<StockDailyData> queryStockData(String stockCode, String startDate, String endDate) {
        validateInputs(stockCode, startDate, endDate);
        
        List<StockDailyData> allData = new ArrayList<>();
        
        // TWSE API returns data for one month at a time, so we need to call it for each month
        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        
        LocalDate currentMonth = start.withDayOfMonth(1);
        LocalDate endMonth = end.withDayOfMonth(1);
        
        while (!currentMonth.isAfter(endMonth)) {
            try {
                List<StockDailyData> monthData = fetchMonthlyData(stockCode, currentMonth);
                
                // Filter data within the specified date range
                for (StockDailyData data : monthData) {
                    if (!data.getDate().isBefore(start) && !data.getDate().isAfter(end)) {
                        allData.add(data);
                    }
                }
                
            } catch (RestClientException e) {
                throw new RuntimeException("Failed to fetch stock data from TWSE API: " + e.getMessage(), e);
            }
            
            currentMonth = currentMonth.plusMonths(1);
        }
        
        return allData;
    }

    /**
     * Fetch stock data for a specific month
     */
    private List<StockDailyData> fetchMonthlyData(String stockCode, LocalDate monthDate) {
        String dateParam = monthDate.format(DATE_FORMATTER);
        String url = TWSE_API_URL + "?response=json&date=" + dateParam + "&stockNo=" + stockCode;
        
        TwseApiResponse response = restTemplate.getForObject(url, TwseApiResponse.class);
        
        if (response == null || !"OK".equals(response.getStat())) {
            throw new RuntimeException("Invalid response from TWSE API");
        }
        
        return convertApiDataToStockData(response);
    }

    /**
     * Convert TWSE API response data to StockDailyData list
     */
    private List<StockDailyData> convertApiDataToStockData(TwseApiResponse response) {
        List<StockDailyData> stockDataList = new ArrayList<>();
        
        if (response.getData() == null || response.getData().isEmpty()) {
            return stockDataList;
        }

        for (List<String> row : response.getData()) {
            if (row.size() >= 9) {
                try {
                    StockDailyData stockData = new StockDailyData();
                    
                    // Parse date (format: 113/12/01 -> 2024/12/01, need to convert ROC year to AD year)
                    String dateStr = row.get(0);
                    LocalDate date = parseRocDate(dateStr);
                    stockData.setDate(date);
                    
                    // Parse numeric fields (remove commas)
                    stockData.setTradeVolume(parseLong(row.get(1)));
                    stockData.setTradeValue(parseLong(row.get(2)));
                    stockData.setOpeningPrice(parseBigDecimal(row.get(3)));
                    stockData.setHighestPrice(parseBigDecimal(row.get(4)));
                    stockData.setLowestPrice(parseBigDecimal(row.get(5)));
                    stockData.setClosingPrice(parseBigDecimal(row.get(6)));
                    stockData.setChange(row.get(7));
                    stockData.setTransaction(parseInt(row.get(8)));
                    
                    stockDataList.add(stockData);
                } catch (Exception e) {
                    // Skip invalid data rows
                    continue;
                }
            }
        }
        
        return stockDataList;
    }

    /**
     * Parse ROC date format (e.g., "113/12/01") to LocalDate
     * ROC year = AD year - 1911
     */
    private LocalDate parseRocDate(String rocDateStr) {
        String[] parts = rocDateStr.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format: " + rocDateStr);
        }
        
        int rocYear = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        
        // Convert ROC year to AD year
        int adYear = rocYear + 1911;
        
        return LocalDate.of(adYear, month, day);
    }

    /**
     * Validate input parameters
     */
    private void validateInputs(String stockCode, String startDate, String endDate) {
        if (stockCode == null || stockCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Stock code cannot be empty");
        }
        
        if (!stockCode.matches("\\d{4}")) {
            throw new IllegalArgumentException("Stock code must be 4 digits");
        }
        
        if (startDate == null || !startDate.matches("\\d{8}")) {
            throw new IllegalArgumentException("Start date must be in YYYYMMDD format");
        }
        
        if (endDate == null || !endDate.matches("\\d{8}")) {
            throw new IllegalArgumentException("End date must be in YYYYMMDD format");
        }
        
        LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
        LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
        
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    // Helper methods for parsing
    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty() || "--".equals(value)) {
            return 0L;
        }
        return Long.parseLong(value.replace(",", ""));
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty() || "--".equals(value)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.replace(",", ""));
    }

    private Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty() || "--".equals(value)) {
            return 0;
        }
        return Integer.parseInt(value.replace(",", ""));
    }
}