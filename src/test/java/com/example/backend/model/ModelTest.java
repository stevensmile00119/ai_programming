package com.example.backend.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for model classes serialization/deserialization
 * Based on realistic TWSE API response structures
 */
@SpringBootTest
public class ModelTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testStockDailyDataSerialization() throws Exception {
        // Arrange - Create realistic stock data
        StockDailyData stockData = new StockDailyData(
                LocalDate.of(2024, 12, 2),
                25486598L,
                28945678142L,
                new BigDecimal("1135.00"),
                new BigDecimal("1145.00"),
                new BigDecimal("1125.00"),
                new BigDecimal("1140.00"),
                "+5.00",
                39847
        );

        // Act - Serialize to JSON
        String json = objectMapper.writeValueAsString(stockData);

        // Assert - Verify JSON structure
        assertTrue(json.contains("\"date\":\"2024/12/02\""));
        assertTrue(json.contains("\"tradeVolume\":25486598"));
        assertTrue(json.contains("\"tradeValue\":28945678142"));
        assertTrue(json.contains("\"openingPrice\":1135.00"));
        assertTrue(json.contains("\"highestPrice\":1145.00"));
        assertTrue(json.contains("\"lowestPrice\":1125.00"));
        assertTrue(json.contains("\"closingPrice\":1140.00"));
        assertTrue(json.contains("\"change\":\"+5.00\""));
        assertTrue(json.contains("\"transaction\":39847"));

        // Act - Deserialize back from JSON
        StockDailyData deserializedData = objectMapper.readValue(json, StockDailyData.class);

        // Assert - Verify deserialized object
        assertEquals(stockData.getDate(), deserializedData.getDate());
        assertEquals(stockData.getTradeVolume(), deserializedData.getTradeVolume());
        assertEquals(stockData.getTradeValue(), deserializedData.getTradeValue());
        assertEquals(stockData.getOpeningPrice(), deserializedData.getOpeningPrice());
        assertEquals(stockData.getHighestPrice(), deserializedData.getHighestPrice());
        assertEquals(stockData.getLowestPrice(), deserializedData.getLowestPrice());
        assertEquals(stockData.getClosingPrice(), deserializedData.getClosingPrice());
        assertEquals(stockData.getChange(), deserializedData.getChange());
        assertEquals(stockData.getTransaction(), deserializedData.getTransaction());
    }

    @Test
    void testTwseApiResponseDeserialization() throws Exception {
        // Arrange - Create realistic TWSE API response JSON based on actual API structure
        String realTwseResponseJson = """
            {
                "stat": "OK",
                "date": "113年12月",
                "title": "113年12月 2330 台積電 各日成交資訊",
                "fields": ["日期", "成交股數", "成交金額", "開盤價", "最高價", "最低價", "收盤價", "漲跌價差", "成交筆數"],
                "data": [
                    ["113/12/02", "25,486,598", "28,945,678,142", "1135.00", "1145.00", "1125.00", "1140.00", "+5.00", "39,847"],
                    ["113/12/03", "22,345,123", "25,234,567,890", "1140.00", "1150.00", "1130.00", "1145.00", "+5.00", "35,678"]
                ],
                "notes": ["符號說明:+表示漲-表示跌 X表示不比價"]
            }
            """;

        // Act - Deserialize from JSON
        TwseApiResponse response = objectMapper.readValue(realTwseResponseJson, TwseApiResponse.class);

        // Assert - Verify deserialization
        assertNotNull(response);
        assertEquals("OK", response.getStat());
        assertEquals("113年12月", response.getDate());
        assertEquals("113年12月 2330 台積電 各日成交資訊", response.getTitle());
        
        assertNotNull(response.getFields());
        assertEquals(9, response.getFields().size());
        assertEquals("日期", response.getFields().get(0));
        assertEquals("成交筆數", response.getFields().get(8));
        
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        
        List<String> firstDataRow = response.getData().get(0);
        assertEquals("113/12/02", firstDataRow.get(0));
        assertEquals("25,486,598", firstDataRow.get(1));
        assertEquals("28,945,678,142", firstDataRow.get(2));
        assertEquals("1135.00", firstDataRow.get(3));
        assertEquals("1145.00", firstDataRow.get(4));
        assertEquals("1125.00", firstDataRow.get(5));
        assertEquals("1140.00", firstDataRow.get(6));
        assertEquals("+5.00", firstDataRow.get(7));
        assertEquals("39,847", firstDataRow.get(8));
        
        assertNotNull(response.getNotes());
        assertEquals(1, response.getNotes().size());
        assertEquals("符號說明:+表示漲-表示跌 X表示不比價", response.getNotes().get(0));
    }

    @Test
    void testTwseApiResponseErrorFormat() throws Exception {
        // Arrange - Create error response JSON based on real TWSE API error format
        String errorResponseJson = """
            {
                "stat": "查詢錯誤",
                "date": "",
                "title": "",
                "fields": [],
                "data": [],
                "notes": ["找不到符合條件的資料"]
            }
            """;

        // Act - Deserialize error response
        TwseApiResponse response = objectMapper.readValue(errorResponseJson, TwseApiResponse.class);

        // Assert - Verify error response structure
        assertNotNull(response);
        assertEquals("查詢錯誤", response.getStat());
        assertEquals("", response.getDate());
        assertEquals("", response.getTitle());
        assertTrue(response.getFields().isEmpty());
        assertTrue(response.getData().isEmpty());
        assertEquals("找不到符合條件的資料", response.getNotes().get(0));
    }

    @Test
    void testStockDailyDataWithSpecialValues() throws Exception {
        // Arrange - Test data with special values like zero and empty strings
        StockDailyData stockData = new StockDailyData(
                LocalDate.of(2024, 12, 25), // Christmas day (no trading)
                0L,                          // No volume
                0L,                          // No value
                BigDecimal.ZERO,            // No opening price
                BigDecimal.ZERO,            // No highest price
                BigDecimal.ZERO,            // No lowest price
                BigDecimal.ZERO,            // No closing price
                "X0.00",                    // No comparison
                0                           // No transactions
        );

        // Act - Serialize and deserialize
        String json = objectMapper.writeValueAsString(stockData);
        StockDailyData deserializedData = objectMapper.readValue(json, StockDailyData.class);

        // Assert - Verify handling of special values
        assertEquals(0L, deserializedData.getTradeVolume());
        assertEquals(0L, deserializedData.getTradeValue());
        assertEquals(BigDecimal.ZERO, deserializedData.getOpeningPrice());
        assertEquals("X0.00", deserializedData.getChange());
        assertEquals(Integer.valueOf(0), deserializedData.getTransaction());
    }

    @Test
    void testTwseApiResponseWithEmptyData() throws Exception {
        // Arrange - API response with no data (e.g., holiday period)
        String noDataResponseJson = """
            {
                "stat": "OK",
                "date": "113年12月",
                "title": "113年12月 2330 台積電 各日成交資訊",
                "fields": ["日期", "成交股數", "成交金額", "開盤價", "最高價", "最低價", "收盤價", "漲跌價差", "成交筆數"],
                "data": [],
                "notes": ["符號說明:+表示漲-表示跌 X表示不比價"]
            }
            """;

        // Act
        TwseApiResponse response = objectMapper.readValue(noDataResponseJson, TwseApiResponse.class);

        // Assert
        assertEquals("OK", response.getStat());
        assertTrue(response.getData().isEmpty());
        assertNotNull(response.getFields());
        assertFalse(response.getFields().isEmpty());
    }

    @Test
    void testTwseApiResponseWithIncompleteData() throws Exception {
        // Arrange - API response with incomplete data rows (real-world scenario)
        String incompleteDataJson = """
            {
                "stat": "OK",
                "date": "113年12月",
                "title": "113年12月 2330 台積電 各日成交資訊",
                "fields": ["日期", "成交股數", "成交金額", "開盤價", "最高價", "最低價", "收盤價", "漲跌價差", "成交筆數"],
                "data": [
                    ["113/12/02", "25,486,598", "28,945,678,142", "1135.00", "1145.00", "1125.00", "1140.00", "+5.00"],
                    ["113/12/03", "22,345,123", "25,234,567,890"]
                ],
                "notes": ["符號說明:+表示漲-表示跌 X表示不比價"]
            }
            """;

        // Act
        TwseApiResponse response = objectMapper.readValue(incompleteDataJson, TwseApiResponse.class);

        // Assert - Should handle incomplete data gracefully
        assertEquals("OK", response.getStat());
        assertEquals(2, response.getData().size());
        assertEquals(8, response.getData().get(0).size()); // First row missing transaction count
        assertEquals(3, response.getData().get(1).size()); // Second row severely incomplete
    }

    @Test
    void testStockDailyDataDefaultConstructor() {
        // Act
        StockDailyData stockData = new StockDailyData();

        // Assert - Verify default values
        assertNull(stockData.getDate());
        assertNull(stockData.getTradeVolume());
        assertNull(stockData.getTradeValue());
        assertNull(stockData.getOpeningPrice());
        assertNull(stockData.getHighestPrice());
        assertNull(stockData.getLowestPrice());
        assertNull(stockData.getClosingPrice());
        assertNull(stockData.getChange());
        assertNull(stockData.getTransaction());
    }

    @Test
    void testTwseApiResponseDefaultConstructor() {
        // Act
        TwseApiResponse response = new TwseApiResponse();

        // Assert - Verify default values
        assertNull(response.getStat());
        assertNull(response.getDate());
        assertNull(response.getTitle());
        assertNull(response.getFields());
        assertNull(response.getData());
        assertNull(response.getNotes());
    }
}