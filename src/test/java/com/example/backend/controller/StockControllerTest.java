package com.example.backend.controller;

import com.example.backend.dto.ErrorResponse;
import com.example.backend.dto.StockQueryResponse;
import com.example.backend.model.StockDailyData;
import com.example.backend.service.TwseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StockController
 * Tests the complete HTTP request/response flow with realistic data
 */
@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TwseService twseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetStockData_Success_TSMC() throws Exception {
        // Arrange - Mock successful response with realistic TSMC data
        List<StockDailyData> mockData = Arrays.asList(
                new StockDailyData(
                        LocalDate.of(2024, 12, 2),
                        25486598L,
                        28945678142L,
                        new BigDecimal("1135.00"),
                        new BigDecimal("1145.00"),
                        new BigDecimal("1125.00"),
                        new BigDecimal("1140.00"),
                        "+5.00",
                        39847
                ),
                new StockDailyData(
                        LocalDate.of(2024, 12, 3),
                        22345123L,
                        25234567890L,
                        new BigDecimal("1140.00"),
                        new BigDecimal("1150.00"),
                        new BigDecimal("1130.00"),
                        new BigDecimal("1145.00"),
                        "+5.00",
                        35678
                )
        );

        when(twseService.queryStockData("2330", "20241201", "20241205"))
                .thenReturn(mockData);

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("startDate", "20241201")
                        .param("endDate", "20241205"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stockCode").value("2330"))
                .andExpect(jsonPath("$.stockName").value("台積電"))
                .andExpect(jsonPath("$.queryPeriod").value("20241201 to 20241205"))
                .andExpect(jsonPath("$.message").value("Data retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].date").value("2024/12/02"))
                .andExpect(jsonPath("$.data[0].openingPrice").value(1135.00))
                .andExpect(jsonPath("$.data[0].highestPrice").value(1145.00))
                .andExpect(jsonPath("$.data[0].lowestPrice").value(1125.00))
                .andExpect(jsonPath("$.data[0].closingPrice").value(1140.00))
                .andExpect(jsonPath("$.data[0].tradeVolume").value(25486598))
                .andExpect(jsonPath("$.data[0].change").value("+5.00"))
                .andExpect(jsonPath("$.data[0].transaction").value(39847));

        verify(twseService).queryStockData("2330", "20241201", "20241205");
    }

    @Test
    void testGetStockData_Success_MediaTek() throws Exception {
        // Arrange - Test different stock with realistic MediaTek data
        List<StockDailyData> mockData = Arrays.asList(
                new StockDailyData(
                        LocalDate.of(2024, 12, 2),
                        5234567L,
                        4123456789L,
                        new BigDecimal("788.00"),
                        new BigDecimal("795.00"),
                        new BigDecimal("785.00"),
                        new BigDecimal("792.00"),
                        "+4.00",
                        12456
                )
        );

        when(twseService.queryStockData("2454", "20241202", "20241202"))
                .thenReturn(mockData);

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/2454")
                        .param("startDate", "20241202")
                        .param("endDate", "20241202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockCode").value("2454"))
                .andExpect(jsonPath("$.stockName").value("聯發科"))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].closingPrice").value(792.00));

        verify(twseService).queryStockData("2454", "20241202", "20241202");
    }

    @Test
    void testGetStockData_Success_EmptyResult() throws Exception {
        // Arrange - Test when no data is available (e.g., holiday period)
        when(twseService.queryStockData("2330", "20241225", "20241225"))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("startDate", "20241225")
                        .param("endDate", "20241225"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockCode").value("2330"))
                .andExpect(jsonPath("$.stockName").value("台積電"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.message").value("Data retrieved successfully"));
    }

    @Test
    void testGetStockData_InvalidStockCode() throws Exception {
        // Arrange - Mock validation error
        when(twseService.queryStockData("123", "20241201", "20241205"))
                .thenThrow(new IllegalArgumentException("Stock code must be 4 digits"));

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/123")
                        .param("startDate", "20241201")
                        .param("endDate", "20241205"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid Input"))
                .andExpect(jsonPath("$.message").value("Stock code must be 4 digits"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGetStockData_InvalidDateFormat() throws Exception {
        // Arrange - Mock date format error
        when(twseService.queryStockData("2330", "2024-12-01", "20241205"))
                .thenThrow(new IllegalArgumentException("Start date must be in YYYYMMDD format"));

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("startDate", "2024-12-01")
                        .param("endDate", "20241205"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Input"))
                .andExpect(jsonPath("$.message").value("Start date must be in YYYYMMDD format"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testGetStockData_DateRangeError() throws Exception {
        // Arrange - Mock date range validation error
        when(twseService.queryStockData("2330", "20241205", "20241201"))
                .thenThrow(new IllegalArgumentException("Start date must be before end date"));

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("startDate", "20241205")
                        .param("endDate", "20241201"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Input"))
                .andExpect(jsonPath("$.message").value("Start date must be before end date"));
    }

    @Test
    void testGetStockData_TwseApiError() throws Exception {
        // Arrange - Mock TWSE API failure (realistic scenario)
        when(twseService.queryStockData("2330", "20241201", "20241205"))
                .thenThrow(new RuntimeException("Failed to fetch stock data from TWSE API: Connection timeout"));

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("startDate", "20241201")
                        .param("endDate", "20241205"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("External API Error"))
                .andExpect(jsonPath("$.message").value("Failed to fetch stock data from TWSE API: Connection timeout"))
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void testGetStockData_TwseApiInvalidResponse() throws Exception {
        // Arrange - Mock invalid API response
        when(twseService.queryStockData("9999", "20241201", "20241205"))
                .thenThrow(new RuntimeException("Invalid response from TWSE API"));

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/9999")
                        .param("startDate", "20241201")
                        .param("endDate", "20241205"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("External API Error"))
                .andExpect(jsonPath("$.message").value("Invalid response from TWSE API"));
    }

    @Test
    void testGetStockData_UnexpectedException() throws Exception {
        // Arrange - Mock unexpected system error
        when(twseService.queryStockData("2330", "20241201", "20241205"))
                .thenThrow(new RuntimeException("Unexpected null value"));

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("startDate", "20241201")
                        .param("endDate", "20241205"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("External API Error"))
                .andExpect(jsonPath("$.message").value("Unexpected null value"))
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void testGetStockData_MissingParameters() throws Exception {
        // Act & Assert - Test missing startDate parameter
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("endDate", "20241205"))
                .andExpect(status().isBadRequest());

        // Act & Assert - Test missing endDate parameter
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("startDate", "20241201"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetStockData_StockNameMapping() throws Exception {
        // Test stock name mapping for various stocks
        List<StockDailyData> mockData = Arrays.asList(
                new StockDailyData(LocalDate.of(2024, 12, 2), 1000000L, 5000000000L,
                        new BigDecimal("100.00"), new BigDecimal("105.00"), 
                        new BigDecimal("98.00"), new BigDecimal("102.00"), "+2.00", 5000)
        );

        // Test different stock codes and their corresponding names
        String[][] stockTests = {
                {"2317", "鴻海"},
                {"2454", "聯發科"},
                {"2382", "廣達"},
                {"3008", "大立光"},
                {"2303", "聯電"},
                {"1301", "台塑"},
                {"2881", "富邦金"},
                {"2002", "中鋼"},
                {"1216", "統一"},
                {"9999", "股票代碼 9999"} // Unknown stock
        };

        for (String[] stockTest : stockTests) {
            String stockCode = stockTest[0];
            String expectedName = stockTest[1];
            
            when(twseService.queryStockData(eq(stockCode), anyString(), anyString()))
                    .thenReturn(mockData);

            mockMvc.perform(get("/api/twse/stock/" + stockCode)
                            .param("startDate", "20241201")
                            .param("endDate", "20241205"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.stockCode").value(stockCode))
                    .andExpect(jsonPath("$.stockName").value(expectedName));
        }
    }

    @Test
    void testHealthCheck() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/twse/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("TWSE API service is running"));
    }

    @Test
    void testGetStockData_RealWorldScenario_WeekendQuery() throws Exception {
        // Arrange - Simulate querying weekend dates (no trading data)
        when(twseService.queryStockData("2330", "20241207", "20241208"))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("startDate", "20241207") // Saturday
                        .param("endDate", "20241208"))  // Sunday
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.message").value("Data retrieved successfully"));
    }

    @Test
    void testGetStockData_RealWorldScenario_SingleDayQuery() throws Exception {
        // Arrange - Single day query (common use case)
        List<StockDailyData> singleDayData = Arrays.asList(
                new StockDailyData(
                        LocalDate.of(2024, 12, 2),
                        25486598L,
                        28945678142L,
                        new BigDecimal("1135.00"),
                        new BigDecimal("1145.00"),
                        new BigDecimal("1125.00"),
                        new BigDecimal("1140.00"),
                        "+5.00",
                        39847
                )
        );

        when(twseService.queryStockData("2330", "20241202", "20241202"))
                .thenReturn(singleDayData);

        // Act & Assert
        mockMvc.perform(get("/api/twse/stock/2330")
                        .param("startDate", "20241202")
                        .param("endDate", "20241202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queryPeriod").value("20241202 to 20241202"))
                .andExpect(jsonPath("$.data.length()").value(1));
    }
}