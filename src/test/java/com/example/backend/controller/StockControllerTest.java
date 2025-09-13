package com.example.backend.controller;

import com.example.backend.dto.StockDataResponse;
import com.example.backend.dto.AllStocksResponse;
import com.example.backend.model.TwseStockData;
import com.example.backend.service.TwseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StockController with STOCK_DAY_ALL API
 * Tests the complete HTTP request/response flow using realistic data
 */
@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TwseService twseService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllStockData_Success_ReturnsAllStocks() throws Exception {
        // Given - Mock service responses
        List<TwseStockData> mockStockData = createMockStockDataList();
        List<StockDataResponse> mockResponseList = createMockResponseList();
        LocalDate tradingDate = LocalDate.of(2025, 9, 12);

        when(twseService.getAllStockData()).thenReturn(mockStockData);
        when(twseService.convertToStockDataResponseList(mockStockData)).thenReturn(mockResponseList);
        when(twseService.getTradingDate()).thenReturn(tradingDate);

        // When & Then
        MvcResult result = mockMvc.perform(get("/api/twse/stocks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        AllStocksResponse response = objectMapper.readValue(responseJson, AllStocksResponse.class);

        assertThat(response.getDate()).isEqualTo(tradingDate);
        assertThat(response.getTotalStocks()).isEqualTo(3);
        assertThat(response.getStocks()).hasSize(3);
        assertThat(response.getMessage()).isEqualTo("All stock data retrieved successfully");

        // Verify first stock
        StockDataResponse firstStock = response.getStocks().get(0);
        assertThat(firstStock.getStockCode()).isEqualTo("0050");
        assertThat(firstStock.getStockName()).isEqualTo("元大台灣50");
        assertThat(firstStock.getClosingPrice()).isEqualByComparingTo(new BigDecimal("56.00"));
    }

    @Test
    void getStockData_ValidStockCode_ReturnsStock() throws Exception {
        // Given - Mock single stock data for TSMC (2330)
        TwseStockData mockStockData = createTsmcMockData();
        StockDataResponse mockResponse = createTsmcMockResponse();

        when(twseService.getStockData("2330")).thenReturn(mockStockData);
        when(twseService.convertToStockDataResponse(mockStockData)).thenReturn(mockResponse);

        // When & Then
        MvcResult result = mockMvc.perform(get("/api/twse/stocks/2330"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        StockDataResponse response = objectMapper.readValue(responseJson, StockDataResponse.class);

        assertThat(response.getStockCode()).isEqualTo("2330");
        assertThat(response.getStockName()).isEqualTo("台積電");
        assertThat(response.getClosingPrice()).isEqualByComparingTo(new BigDecimal("1140.00"));
        assertThat(response.getTradeVolume()).isEqualTo(25486598L);
        assertThat(response.getChange()).isEqualTo("+5.00");
    }

    @Test
    void getStockData_ETF_ReturnsETFData() throws Exception {
        // Given - Mock ETF data for 0050
        TwseStockData mockStockData = create0050MockData();
        StockDataResponse mockResponse = create0050MockResponse();

        when(twseService.getStockData("0050")).thenReturn(mockStockData);
        when(twseService.convertToStockDataResponse(mockStockData)).thenReturn(mockResponse);

        // When & Then
        MvcResult result = mockMvc.perform(get("/api/twse/stocks/0050"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        StockDataResponse response = objectMapper.readValue(responseJson, StockDataResponse.class);

        assertThat(response.getStockCode()).isEqualTo("0050");
        assertThat(response.getStockName()).isEqualTo("元大台灣50");
        assertThat(response.getClosingPrice()).isEqualByComparingTo(new BigDecimal("56.00"));
    }

    @Test
    void getStockData_StockNotFound_Returns404() throws Exception {
        // Given - Stock not found
        when(twseService.getStockData("9999")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/twse/stocks/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Stock Not Found"))
                .andExpect(jsonPath("$.message").value("No data found for stock code: 9999"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getStockData_InvalidStockCode_Returns400() throws Exception {
        // Given - Invalid stock code
        when(twseService.getStockData("23")).thenThrow(
                new IllegalArgumentException("Stock code must be 4 digits"));

        // When & Then
        mockMvc.perform(get("/api/twse/stocks/23"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid Input"))
                .andExpect(jsonPath("$.message").value("Stock code must be 4 digits"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getStockData_ExternalAPIError_Returns503() throws Exception {
        // Given - External API error
        when(twseService.getStockData("2330")).thenThrow(
                new RuntimeException("Failed to fetch stock data from TWSE API: Connection timeout"));

        // When & Then
        mockMvc.perform(get("/api/twse/stocks/2330"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("External API Error"))
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void getAllStockData_ExternalAPIError_Returns503() throws Exception {
        // Given - External API error for all stocks
        when(twseService.getAllStockData()).thenThrow(
                new RuntimeException("Failed to fetch stock data from TWSE API: Network error"));

        // When & Then
        mockMvc.perform(get("/api/twse/stocks"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("External API Error"))
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void getStockData_UnexpectedError_Returns503() throws Exception {
        // Given - RuntimeException which should return 503 Service Unavailable
        when(twseService.convertToStockDataResponse(any())).thenThrow(
                new NullPointerException("Unexpected null value"));

        TwseStockData mockData = createTsmcMockData();
        when(twseService.getStockData("2330")).thenReturn(mockData);

        // When & Then
        mockMvc.perform(get("/api/twse/stocks/2330"))
                .andExpect(status().isServiceUnavailable()) // NullPointerException extends RuntimeException, so it should be 503
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("External API Error"))
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void healthCheck_Always_ReturnsOK() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/twse/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("TWSE API service is running"));
    }

    // Helper methods to create mock data based on actual API response format

    private List<TwseStockData> createMockStockDataList() {
        TwseStockData etf = create0050MockData();
        TwseStockData tsmc = createTsmcMockData();
        TwseStockData mediatek = createMediatekMockData();
        return List.of(etf, tsmc, mediatek);
    }

    private List<StockDataResponse> createMockResponseList() {
        StockDataResponse etf = create0050MockResponse();
        StockDataResponse tsmc = createTsmcMockResponse();
        StockDataResponse mediatek = createMediatekMockResponse();
        return List.of(etf, tsmc, mediatek);
    }

    private TwseStockData create0050MockData() {
        // Based on actual response: {"Date":"1140912","Code":"0050","Name":"元大台灣50",...}
        TwseStockData stock = new TwseStockData();
        stock.setDate("1140912");
        stock.setCode("0050");
        stock.setName("元大台灣50");
        stock.setTradeVolume("59101728");
        stock.setTradeValue("3303299908");
        stock.setOpeningPrice("55.75");
        stock.setHighestPrice("56.00");
        stock.setLowestPrice("55.70");
        stock.setClosingPrice("56.00");
        stock.setChange("0.5500");
        stock.setTransaction("36831");
        return stock;
    }

    private StockDataResponse create0050MockResponse() {
        return new StockDataResponse(
                "0050", "元大台灣50", LocalDate.of(2025, 9, 12),
                new BigDecimal("55.75"), new BigDecimal("56.00"), new BigDecimal("55.70"),
                new BigDecimal("56.00"), "0.5500", 59101728L, 3303299908L, 36831
        );
    }

    private TwseStockData createTsmcMockData() {
        TwseStockData stock = new TwseStockData();
        stock.setDate("1140912");
        stock.setCode("2330");
        stock.setName("台積電");
        stock.setTradeVolume("25486598");
        stock.setTradeValue("29101234567");
        stock.setOpeningPrice("1135.00");
        stock.setHighestPrice("1145.00");
        stock.setLowestPrice("1125.00");
        stock.setClosingPrice("1140.00");
        stock.setChange("+5.00");
        stock.setTransaction("8456");
        return stock;
    }

    private StockDataResponse createTsmcMockResponse() {
        return new StockDataResponse(
                "2330", "台積電", LocalDate.of(2025, 9, 12),
                new BigDecimal("1135.00"), new BigDecimal("1145.00"), new BigDecimal("1125.00"),
                new BigDecimal("1140.00"), "+5.00", 25486598L, 29101234567L, 8456
        );
    }

    private TwseStockData createMediatekMockData() {
        TwseStockData stock = new TwseStockData();
        stock.setDate("1140912");
        stock.setCode("2454");
        stock.setName("聯發科");
        stock.setTradeVolume("18567234");
        stock.setTradeValue("18567890123");
        stock.setOpeningPrice("890.00");
        stock.setHighestPrice("910.00");
        stock.setLowestPrice("885.00");
        stock.setClosingPrice("905.00");
        stock.setChange("+15.00");
        stock.setTransaction("6789");
        return stock;
    }

    private StockDataResponse createMediatekMockResponse() {
        return new StockDataResponse(
                "2454", "聯發科", LocalDate.of(2025, 9, 12),
                new BigDecimal("890.00"), new BigDecimal("910.00"), new BigDecimal("885.00"),
                new BigDecimal("905.00"), "+15.00", 18567234L, 18567890123L, 6789
        );
    }
}