package com.example.backend.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for TwseStockData model and parsing methods
 * Tests JSON serialization/deserialization and ROC date conversion
 */
class TwseStockDataModelTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void parseDate_ValidROCDate_ReturnsCorrectDate() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setDate("1140912"); // ROC 114/09/12

        // When
        LocalDate result = stockData.parseDate();

        // Then
        assertThat(result).isEqualTo(LocalDate.of(2025, 9, 12)); // 114 + 1911 = 2025
    }

    @Test
    void parseDate_AnotherROCDate_ReturnsCorrectDate() {
        // Given - Test with different ROC year
        TwseStockData stockData = new TwseStockData();
        stockData.setDate("1131201"); // ROC 113/12/01

        // When
        LocalDate result = stockData.parseDate();

        // Then
        assertThat(result).isEqualTo(LocalDate.of(2024, 12, 1)); // 113 + 1911 = 2024
    }

    @Test
    void parseDate_InvalidFormat_ThrowsException() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setDate("202412"); // Wrong format

        // When & Then
        assertThatThrownBy(() -> stockData.parseDate())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid ROC date format");
    }

    @Test
    void parseTradeVolume_ValidNumber_ReturnsCorrectValue() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setTradeVolume("59101728");

        // When
        Long result = stockData.parseTradeVolume();

        // Then
        assertThat(result).isEqualTo(59101728L);
    }

    @Test
    void parseTradeVolume_NumberWithCommas_ReturnsCorrectValue() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setTradeVolume("59,101,728");

        // When
        Long result = stockData.parseTradeVolume();

        // Then
        assertThat(result).isEqualTo(59101728L);
    }

    @Test
    void parseTradeVolume_EmptyValue_ReturnsZero() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setTradeVolume("");

        // When
        Long result = stockData.parseTradeVolume();

        // Then
        assertThat(result).isEqualTo(0L);
    }

    @Test
    void parseTradeVolume_DashValue_ReturnsZero() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setTradeVolume("--");

        // When
        Long result = stockData.parseTradeVolume();

        // Then
        assertThat(result).isEqualTo(0L);
    }

    @Test
    void parseOpeningPrice_ValidPrice_ReturnsCorrectValue() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setOpeningPrice("55.75");

        // When
        BigDecimal result = stockData.parseOpeningPrice();

        // Then
        assertThat(result).isEqualByComparingTo(new BigDecimal("55.75"));
    }

    @Test
    void parseOpeningPrice_PriceWithCommas_ReturnsCorrectValue() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setOpeningPrice("1,140.00");

        // When
        BigDecimal result = stockData.parseOpeningPrice();

        // Then
        assertThat(result).isEqualByComparingTo(new BigDecimal("1140.00"));
    }

    @Test
    void parseOpeningPrice_EmptyValue_ReturnsZero() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setOpeningPrice("");

        // When
        BigDecimal result = stockData.parseOpeningPrice();

        // Then
        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void parseTransaction_ValidNumber_ReturnsCorrectValue() {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setTransaction("36831");

        // When
        Integer result = stockData.parseTransaction();

        // Then
        assertThat(result).isEqualTo(36831);
    }

    @Test
    void jsonDeserialization_RealAPIResponse_WorksCorrectly() throws Exception {
        // Given - JSON response in the format provided in the comment
        String jsonResponse = """
                {
                    "Date": "1140912",
                    "Code": "0050",
                    "Name": "元大台灣50",
                    "TradeVolume": "59101728",
                    "TradeValue": "3303299908",
                    "OpeningPrice": "55.75",
                    "HighestPrice": "56.00",
                    "LowestPrice": "55.70",
                    "ClosingPrice": "56.00",
                    "Change": "0.5500",
                    "Transaction": "36831"
                }
                """;

        // When
        TwseStockData result = objectMapper.readValue(jsonResponse, TwseStockData.class);

        // Then
        assertThat(result.getDate()).isEqualTo("1140912");
        assertThat(result.getCode()).isEqualTo("0050");
        assertThat(result.getName()).isEqualTo("元大台灣50");
        assertThat(result.getTradeVolume()).isEqualTo("59101728");
        assertThat(result.getTradeValue()).isEqualTo("3303299908");
        assertThat(result.getOpeningPrice()).isEqualTo("55.75");
        assertThat(result.getHighestPrice()).isEqualTo("56.00");
        assertThat(result.getLowestPrice()).isEqualTo("55.70");
        assertThat(result.getClosingPrice()).isEqualTo("56.00");
        assertThat(result.getChange()).isEqualTo("0.5500");
        assertThat(result.getTransaction()).isEqualTo("36831");

        // Test parsing methods
        assertThat(result.parseDate()).isEqualTo(LocalDate.of(2025, 9, 12));
        assertThat(result.parseTradeVolume()).isEqualTo(59101728L);
        assertThat(result.parseClosingPrice()).isEqualByComparingTo(new BigDecimal("56.00"));
    }

    @Test
    void jsonDeserialization_ArrayResponse_WorksCorrectly() throws Exception {
        // Given - Array response format from TWSE API
        String jsonArrayResponse = """
                [
                    {
                        "Date": "1140912",
                        "Code": "0050",
                        "Name": "元大台灣50",
                        "TradeVolume": "59101728",
                        "TradeValue": "3303299908",
                        "OpeningPrice": "55.75",
                        "HighestPrice": "56.00",
                        "LowestPrice": "55.70",
                        "ClosingPrice": "56.00",
                        "Change": "0.5500",
                        "Transaction": "36831"
                    },
                    {
                        "Date": "1140912",
                        "Code": "2330",
                        "Name": "台積電",
                        "TradeVolume": "25486598",
                        "TradeValue": "29101234567",
                        "OpeningPrice": "1135.00",
                        "HighestPrice": "1145.00",
                        "LowestPrice": "1125.00",
                        "ClosingPrice": "1140.00",
                        "Change": "+5.00",
                        "Transaction": "8456"
                    }
                ]
                """;

        // When
        TwseStockData[] result = objectMapper.readValue(jsonArrayResponse, TwseStockData[].class);

        // Then
        assertThat(result).hasSize(2);
        
        // First stock (ETF)
        TwseStockData etf = result[0];
        assertThat(etf.getCode()).isEqualTo("0050");
        assertThat(etf.getName()).isEqualTo("元大台灣50");
        assertThat(etf.parseClosingPrice()).isEqualByComparingTo(new BigDecimal("56.00"));

        // Second stock (TSMC)
        TwseStockData tsmc = result[1];
        assertThat(tsmc.getCode()).isEqualTo("2330");
        assertThat(tsmc.getName()).isEqualTo("台積電");
        assertThat(tsmc.parseClosingPrice()).isEqualByComparingTo(new BigDecimal("1140.00"));
        assertThat(tsmc.parseTradeVolume()).isEqualTo(25486598L);
    }

    @Test
    void jsonSerialization_ValidObject_ProducesCorrectJSON() throws Exception {
        // Given
        TwseStockData stockData = new TwseStockData();
        stockData.setDate("1140912");
        stockData.setCode("2330");
        stockData.setName("台積電");
        stockData.setTradeVolume("25486598");
        stockData.setTradeValue("29101234567");
        stockData.setOpeningPrice("1135.00");
        stockData.setHighestPrice("1145.00");
        stockData.setLowestPrice("1125.00");
        stockData.setClosingPrice("1140.00");
        stockData.setChange("+5.00");
        stockData.setTransaction("8456");

        // When
        String json = objectMapper.writeValueAsString(stockData);

        // Then
        assertThat(json).contains("\"Date\":\"1140912\"");
        assertThat(json).contains("\"Code\":\"2330\"");
        assertThat(json).contains("\"Name\":\"台積電\"");
        assertThat(json).contains("\"ClosingPrice\":\"1140.00\"");
        assertThat(json).contains("\"Change\":\"+5.00\"");
    }
}