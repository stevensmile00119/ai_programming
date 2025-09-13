package com.example.backend.service;

import com.example.backend.model.StockDailyData;
import com.example.backend.model.TwseApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TwseService based on real TWSE API response patterns
 */
@ExtendWith(MockitoExtension.class)
public class TwseServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private TwseService twseService;

    @BeforeEach
    void setUp() {
        twseService = new TwseService();
        // Use reflection to inject the mock RestTemplate
        try {
            java.lang.reflect.Field restTemplateField = TwseService.class.getDeclaredField("restTemplate");
            restTemplateField.setAccessible(true);
            restTemplateField.set(twseService, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testQueryStockData_SuccessfulResponse() {
        // Arrange - Create realistic TWSE API response based on actual API structure
        TwseApiResponse mockResponse = createSuccessfulMockResponse();
        when(restTemplate.getForObject(anyString(), eq(TwseApiResponse.class)))
                .thenReturn(mockResponse);

        // Act
        List<StockDailyData> result = twseService.queryStockData("2330", "20241201", "20241205");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verify first day data
        StockDailyData firstDay = result.get(0);
        assertEquals(LocalDate.of(2024, 12, 2), firstDay.getDate());
        assertEquals(new BigDecimal("1135.00"), firstDay.getOpeningPrice());
        assertEquals(new BigDecimal("1145.00"), firstDay.getHighestPrice());
        assertEquals(new BigDecimal("1125.00"), firstDay.getLowestPrice());
        assertEquals(new BigDecimal("1140.00"), firstDay.getClosingPrice());
        assertEquals(25486598L, firstDay.getTradeVolume());
        assertEquals("+5.00", firstDay.getChange());
        assertEquals(39847, firstDay.getTransaction());

        // Verify API was called with correct parameters
        verify(restTemplate).getForObject(
                contains("https://openapi.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=20241201&stockNo=2330"),
                eq(TwseApiResponse.class)
        );
    }

    @Test
    void testQueryStockData_MultipleMonths() {
        // Arrange - Test cross-month query
        TwseApiResponse mockResponse1 = createMockResponseForMonth("113/11");
        TwseApiResponse mockResponse2 = createMockResponseForMonth("113/12");
        
        when(restTemplate.getForObject(contains("date=20241101"), eq(TwseApiResponse.class)))
                .thenReturn(mockResponse1);
        when(restTemplate.getForObject(contains("date=20241201"), eq(TwseApiResponse.class)))
                .thenReturn(mockResponse2);

        // Act
        List<StockDailyData> result = twseService.queryStockData("2330", "20241129", "20241203");

        // Assert
        assertNotNull(result);
        // Should filter dates to only include those within the specified range
        assertTrue(result.size() > 0);
        
        // Verify all returned dates are within the specified range
        LocalDate startDate = LocalDate.of(2024, 11, 29);
        LocalDate endDate = LocalDate.of(2024, 12, 3);
        for (StockDailyData data : result) {
            assertFalse(data.getDate().isBefore(startDate), "Date should not be before start date");
            assertFalse(data.getDate().isAfter(endDate), "Date should not be after end date");
        }

        // Verify both API calls were made
        verify(restTemplate).getForObject(contains("date=20241101"), eq(TwseApiResponse.class));
        verify(restTemplate).getForObject(contains("date=20241201"), eq(TwseApiResponse.class));
    }

    @Test
    void testQueryStockData_ApiError() {
        // Arrange - Mock API failure
        TwseApiResponse errorResponse = new TwseApiResponse();
        errorResponse.setStat("查詢錯誤"); // Error status in Chinese as per TWSE API
        when(restTemplate.getForObject(anyString(), eq(TwseApiResponse.class)))
                .thenReturn(errorResponse);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            twseService.queryStockData("9999", "20241201", "20241205");
        });
        
        assertEquals("Invalid response from TWSE API", exception.getMessage());
    }

    @Test
    void testQueryStockData_RestClientException() {
        // Arrange - Mock network error
        when(restTemplate.getForObject(anyString(), eq(TwseApiResponse.class)))
                .thenThrow(new RestClientException("Connection timeout"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            twseService.queryStockData("2330", "20241201", "20241205");
        });
        
        assertTrue(exception.getMessage().contains("Failed to fetch stock data from TWSE API"));
        assertTrue(exception.getMessage().contains("Connection timeout"));
    }

    @Test
    void testQueryStockData_InvalidStockCode() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            twseService.queryStockData("123", "20241201", "20241205"); // Invalid 3-digit code
        });
        
        assertEquals("Stock code must be 4 digits", exception.getMessage());
    }

    @Test
    void testQueryStockData_InvalidDateFormat() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            twseService.queryStockData("2330", "2024-12-01", "20241205"); // Wrong date format
        });
        
        assertEquals("Start date must be in YYYYMMDD format", exception.getMessage());
    }

    @Test
    void testQueryStockData_StartDateAfterEndDate() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            twseService.queryStockData("2330", "20241205", "20241201"); // Start after end
        });
        
        assertEquals("Start date must be before end date", exception.getMessage());
    }

    @Test
    void testQueryStockData_EmptyDataResponse() {
        // Arrange - API returns empty data (e.g., no trading days)
        TwseApiResponse emptyResponse = new TwseApiResponse();
        emptyResponse.setStat("OK");
        emptyResponse.setData(Collections.emptyList());
        
        when(restTemplate.getForObject(anyString(), eq(TwseApiResponse.class)))
                .thenReturn(emptyResponse);

        // Act
        List<StockDailyData> result = twseService.queryStockData("2330", "20241201", "20241205");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testQueryStockData_NullApiResponse() {
        // Arrange - API returns null
        when(restTemplate.getForObject(anyString(), eq(TwseApiResponse.class)))
                .thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            twseService.queryStockData("2330", "20241201", "20241205");
        });
        
        assertEquals("Invalid response from TWSE API", exception.getMessage());
    }

    @Test
    void testRocDateConversion() {
        // Arrange - Test ROC (Republic of China) date conversion
        TwseApiResponse mockResponse = new TwseApiResponse();
        mockResponse.setStat("OK");
        mockResponse.setData(Arrays.asList(
                Arrays.asList("113/12/02", "25,486,598", "28,945,678,142", "1135.00", "1145.00", "1125.00", "1140.00", "+5.00", "39,847")
        ));
        
        when(restTemplate.getForObject(anyString(), eq(TwseApiResponse.class)))
                .thenReturn(mockResponse);

        // Act
        List<StockDailyData> result = twseService.queryStockData("2330", "20241201", "20241205");

        // Assert - ROC year 113 should convert to AD year 2024 (113 + 1911 = 2024)
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(LocalDate.of(2024, 12, 2), result.get(0).getDate());
    }

    @Test
    void testDataParsingWithCommasAndSpecialValues() {
        // Arrange - Test parsing of numbers with commas and special values like "--"
        TwseApiResponse mockResponse = new TwseApiResponse();
        mockResponse.setStat("OK");
        mockResponse.setData(Arrays.asList(
                Arrays.asList("113/12/02", "--", "28,945,678,142", "--", "1145.00", "1125.00", "1140.00", "X0.00", "39,847")
        ));
        
        when(restTemplate.getForObject(anyString(), eq(TwseApiResponse.class)))
                .thenReturn(mockResponse);

        // Act
        List<StockDailyData> result = twseService.queryStockData("2330", "20241201", "20241205");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        StockDailyData data = result.get(0);
        assertEquals(0L, data.getTradeVolume()); // "--" should convert to 0
        assertEquals(BigDecimal.ZERO, data.getOpeningPrice()); // "--" should convert to 0
        assertEquals(28945678142L, data.getTradeValue()); // Commas should be removed
        assertEquals("X0.00", data.getChange()); // Special change format should be preserved
    }

    // Helper methods to create mock responses based on real TWSE API response structure
    private TwseApiResponse createSuccessfulMockResponse() {
        TwseApiResponse response = new TwseApiResponse();
        response.setStat("OK");
        response.setDate("113年12月");
        response.setTitle("113年12月 2330 台積電 各日成交資訊");
        response.setFields(Arrays.asList("日期", "成交股數", "成交金額", "開盤價", "最高價", "最低價", "收盤價", "漲跌價差", "成交筆數"));
        response.setData(Arrays.asList(
                Arrays.asList("113/12/02", "25,486,598", "28,945,678,142", "1135.00", "1145.00", "1125.00", "1140.00", "+5.00", "39,847"),
                Arrays.asList("113/12/03", "22,345,123", "25,234,567,890", "1140.00", "1150.00", "1130.00", "1145.00", "+5.00", "35,678"),
                Arrays.asList("113/12/04", "28,567,234", "32,678,901,234", "1145.00", "1155.00", "1140.00", "1150.00", "+5.00", "42,345")
        ));
        response.setNotes(Arrays.asList("符號說明:+表示漲-表示跌 X表示不比價"));
        return response;
    }

    private TwseApiResponse createMockResponseForMonth(String rocMonth) {
        TwseApiResponse response = new TwseApiResponse();
        response.setStat("OK");
        response.setDate(rocMonth + "年");
        response.setTitle(rocMonth + " 2330 台積電 各日成交資訊");
        response.setFields(Arrays.asList("日期", "成交股數", "成交金額", "開盤價", "最高價", "最低價", "收盤價", "漲跌價差", "成交筆數"));
        
        if ("113/11".equals(rocMonth)) {
            response.setData(Arrays.asList(
                    Arrays.asList("113/11/29", "20,123,456", "22,345,678,901", "1110.00", "1120.00", "1105.00", "1115.00", "+3.00", "32,456"),
                    Arrays.asList("113/11/30", "21,234,567", "23,456,789,012", "1115.00", "1125.00", "1110.00", "1120.00", "+5.00", "33,567")
            ));
        } else if ("113/12".equals(rocMonth)) {
            response.setData(Arrays.asList(
                    Arrays.asList("113/12/01", "23,456,789", "26,789,012,345", "1120.00", "1130.00", "1115.00", "1125.00", "+5.00", "36,789"),
                    Arrays.asList("113/12/02", "25,486,598", "28,945,678,142", "1125.00", "1135.00", "1120.00", "1130.00", "+5.00", "39,847"),
                    Arrays.asList("113/12/03", "22,345,123", "25,234,567,890", "1130.00", "1140.00", "1125.00", "1135.00", "+5.00", "35,678")
            ));
        }
        
        response.setNotes(Arrays.asList("符號說明:+表示漲-表示跌 X表示不比價"));
        return response;
    }
}