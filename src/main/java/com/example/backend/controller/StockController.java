package com.example.backend.controller;

import com.example.backend.dto.ErrorResponse;
import com.example.backend.dto.StockDataResponse;
import com.example.backend.dto.AllStocksResponse;
import com.example.backend.model.TwseStockData;
import com.example.backend.service.TwseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST Controller for Taiwan Stock Exchange current day stock data queries
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
     * Get all current day stock data
     * 
     * @return ResponseEntity with all stock data or error message
     */
    @GetMapping("/stocks")
    public ResponseEntity<?> getAllStockData() {
        try {
            List<TwseStockData> allStockData = twseService.getAllStockData();
            List<StockDataResponse> stockResponses = twseService.convertToStockDataResponseList(allStockData);
            
            AllStocksResponse response = new AllStocksResponse(
                twseService.getTradingDate(),
                stockResponses.size(),
                stockResponses,
                "All stock data retrieved successfully"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "External API Error", e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred");
        }
    }

    /**
     * Get current day stock data for a specific stock code
     * 
     * @param stockCode Stock code (4 digits, e.g., "2330", "0050")
     * @return ResponseEntity with stock data or error message
     */
    @GetMapping("/stocks/{stockCode}")
    public ResponseEntity<?> getStockData(@PathVariable String stockCode) {
        try {
            TwseStockData stockData = twseService.getStockData(stockCode);
            
            if (stockData == null) {
                return buildErrorResponse(HttpStatus.NOT_FOUND, "Stock Not Found", 
                    "No data found for stock code: " + stockCode);
            }
            
            StockDataResponse response = twseService.convertToStockDataResponse(stockData);
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
}