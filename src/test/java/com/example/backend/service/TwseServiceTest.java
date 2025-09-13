package com.example.backend.service;

import com.example.backend.dto.StockDataResponse;
import com.example.backend.model.TwseStockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TwseService with STOCK_DAY_ALL API
 * Using realistic mock responses based on the actual API response format provided
 */
@ExtendWith(MockitoExtension.class)
class TwseServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private TwseService twseService;

    @BeforeEach
    void setUp() {
        twseService = new TwseService();
        // Inject the mock RestTemplate using reflection
        try {
            java.lang.reflect.Field field = TwseService.class.getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(twseService, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAllStockData_Success_ReturnsValidData() {
        // Given - Mock successful TWSE STOCK_DAY_ALL API response
        TwseStockData[] mockResponse = createSuccessfulMockResponse();
        when(restTemplate.getForObject(any(String.class), eq(TwseStockData[].class)))
                .thenReturn(mockResponse);

        // When
        List<TwseStockData> result = twseService.getAllStockData();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3); // Three stocks in mock data
        
        TwseStockData first = result.get(0);
        assertThat(first.getCode()).isEqualTo("0050");
        assertThat(first.getName()).isEqualTo("元大台灣50");
        assertThat(first.getDate()).isEqualTo("1140912"); // ROC date format
        assertThat(first.getClosingPrice()).isEqualTo("56.00");
    }

    @Test
    void getStockData_ValidStockCode_ReturnsStockData() {
        // Given
        TwseStockData[] mockResponse = createSuccessfulMockResponse();
        when(restTemplate.getForObject(any(String.class), eq(TwseStockData[].class)))
                .thenReturn(mockResponse);

        // When
        TwseStockData result = twseService.getStockData("2330");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("2330");
        assertThat(result.getName()).isEqualTo("台積電");
        assertThat(result.getClosingPrice()).isEqualTo("1140.00");
    }

    @Test
    void getStockData_InvalidStockCode_ReturnsNull() {
        // Given
        TwseStockData[] mockResponse = createSuccessfulMockResponse();
        when(restTemplate.getForObject(any(String.class), eq(TwseStockData[].class)))
                .thenReturn(mockResponse);

        // When
        TwseStockData result = twseService.getStockData("9999");

        // Then
        assertThat(result).isNull();
    }

    @Test
    void getStockData_NetworkError_ThrowsException() {
        // Given
        when(restTemplate.getForObject(any(String.class), eq(TwseStockData[].class)))
                .thenThrow(new RestClientException("Connection timeout"));

        // When & Then
        assertThatThrownBy(() -> twseService.getStockData("2330"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch stock data from TWSE API");
    }

    @Test
    void getStockData_EmptyResponse_ThrowsException() {
        // Given - Empty response
        when(restTemplate.getForObject(any(String.class), eq(TwseStockData[].class)))
                .thenReturn(new TwseStockData[0]);

        // When & Then
        assertThatThrownBy(() -> twseService.getAllStockData())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No data returned from TWSE API");
    }

    @Test
    void getStockData_NullResponse_ThrowsException() {
        // Given - Null response
        when(restTemplate.getForObject(any(String.class), eq(TwseStockData[].class)))
                .thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> twseService.getAllStockData())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No data returned from TWSE API");
    }

    @Test
    void validateStockCode_InvalidInputs_ThrowsException() {
        // When & Then - Test various invalid stock codes
        assertThatThrownBy(() -> twseService.getStockData(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock code cannot be empty");

        assertThatThrownBy(() -> twseService.getStockData(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock code cannot be empty");

        assertThatThrownBy(() -> twseService.getStockData("23"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock code must be 4 digits");

        assertThatThrownBy(() -> twseService.getStockData("TSMC"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock code must be 4 digits");
    }

    @Test
    void convertToStockDataResponse_ValidData_ReturnsResponse() {
        // Given
        TwseStockData stockData = createSingleStockData();

        // When
        StockDataResponse result = twseService.convertToStockDataResponse(stockData);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStockCode()).isEqualTo("2330");
        assertThat(result.getStockName()).isEqualTo("台積電");
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 9, 12)); // ROC 114/09/12
        assertThat(result.getClosingPrice()).isEqualByComparingTo(new BigDecimal("1140.00"));
        assertThat(result.getTradeVolume()).isEqualTo(25486598L);
        assertThat(result.getChange()).isEqualTo("+5.00");
    }

    @Test
    void convertToStockDataResponse_NullInput_ReturnsNull() {
        // When & Then
        StockDataResponse result = twseService.convertToStockDataResponse(null);
        assertThat(result).isNull();
    }

    @Test
    void convertToStockDataResponseList_ValidList_ReturnsResponseList() {
        // Given
        TwseStockData[] mockResponse = createSuccessfulMockResponse();
        List<TwseStockData> stockDataList = List.of(mockResponse);

        // When
        List<StockDataResponse> result = twseService.convertToStockDataResponseList(stockDataList);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getStockCode()).isEqualTo("0050");
        assertThat(result.get(1).getStockCode()).isEqualTo("2330");
        assertThat(result.get(2).getStockCode()).isEqualTo("2454");
    }

    @Test
    void getTradingDate_ValidData_ReturnsDate() {
        // Given
        TwseStockData[] mockResponse = createSuccessfulMockResponse();
        when(restTemplate.getForObject(any(String.class), eq(TwseStockData[].class)))
                .thenReturn(mockResponse);

        // When
        LocalDate result = twseService.getTradingDate();

        // Then
        assertThat(result).isEqualTo(LocalDate.of(2025, 9, 12)); // ROC 1140912 -> AD 2025-09-12
    }

    // Helper methods to create mock responses based on the actual API format

    private TwseStockData[] createSuccessfulMockResponse() {
        // Based on the actual response format provided in the comment:
        // [{"Date":"1140912","Code":"0050","Name":"元大台灣50","TradeVolume":"59101728",...}]
        
        TwseStockData stock1 = new TwseStockData();
        stock1.setDate("1140912"); // ROC date format
        stock1.setCode("0050");
        stock1.setName("元大台灣50");
        stock1.setTradeVolume("59101728");
        stock1.setTradeValue("3303299908");
        stock1.setOpeningPrice("55.75");
        stock1.setHighestPrice("56.00");
        stock1.setLowestPrice("55.70");
        stock1.setClosingPrice("56.00");
        stock1.setChange("0.5500");
        stock1.setTransaction("36831");

        TwseStockData stock2 = new TwseStockData();
        stock2.setDate("1140912");
        stock2.setCode("2330");
        stock2.setName("台積電");
        stock2.setTradeVolume("25486598");
        stock2.setTradeValue("29101234567");
        stock2.setOpeningPrice("1135.00");
        stock2.setHighestPrice("1145.00");
        stock2.setLowestPrice("1125.00");
        stock2.setClosingPrice("1140.00");
        stock2.setChange("+5.00");
        stock2.setTransaction("8456");

        TwseStockData stock3 = new TwseStockData();
        stock3.setDate("1140912");
        stock3.setCode("2454");
        stock3.setName("聯發科");
        stock3.setTradeVolume("18567234");
        stock3.setTradeValue("18567890123");
        stock3.setOpeningPrice("890.00");
        stock3.setHighestPrice("910.00");
        stock3.setLowestPrice("885.00");
        stock3.setClosingPrice("905.00");
        stock3.setChange("+15.00");
        stock3.setTransaction("6789");

        return new TwseStockData[]{stock1, stock2, stock3};
    }

    private TwseStockData createSingleStockData() {
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
}