package com.example.backend.controller;

import com.example.backend.dto.ErrorResponse;
import com.example.backend.dto.StockQueryResponse;
import com.example.backend.model.StockDailyData;
import com.example.backend.service.TwseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST Controller for Taiwan Stock Exchange stock data queries
 */
@RestController
@RequestMapping("/api/twse")
public class StockController {

    private final TwseService twseService;

    @Autowired
    public StockController(TwseService twseService) {
        this.twseService = twseService;
    }

    /**
     * Query stock daily data for a specific stock code and date range
     * 
     * @param stockNo Stock code (4 digits, e.g., "2330")
     * @param startDate Start date in YYYYMMDD format
     * @param endDate End date in YYYYMMDD format
     * @return ResponseEntity with stock data or error message
     */
    @GetMapping("/stock/{stockNo}")
    public ResponseEntity<?> getStockData(
            @PathVariable String stockNo,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        try {
            List<StockDailyData> stockData = twseService.queryStockData(stockNo, startDate, endDate);
            
            StockQueryResponse response = new StockQueryResponse(
                stockNo,
                getStockName(stockNo), // You could enhance this with a stock name lookup
                startDate + " to " + endDate,
                stockData,
                "Data retrieved successfully"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Input", e.getMessage());
        } catch (RuntimeException e) {
            return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "External API Error", e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
        }
    }

    /**
     * Health check endpoint for the TWSE API service
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().body("TWSE API service is running");
    }

    /**
     * Build error response
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            error,
            message,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            status.value()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Get stock name by stock code (simplified implementation)
     * In a real application, this could be enhanced with a database lookup
     */
    private String getStockName(String stockCode) {
        // Simple mapping for common stocks
        switch (stockCode) {
            case "2330": return "台積電";
            case "2317": return "鴻海";
            case "2454": return "聯發科";
            case "2382": return "廣達";
            case "3008": return "大立光";
            case "2303": return "聯電";
            case "1301": return "台塑";
            case "2881": return "富邦金";
            case "2002": return "中鋼";
            case "1216": return "統一";
            default: return "股票代碼 " + stockCode;
        }
    }
}