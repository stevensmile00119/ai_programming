package com.example.backend.service;

import org.junit.jupiter.api.Test;

/**
 * Documentation test for the new TWSE STOCK_DAY_ALL API
 * This test documents the actual API response structure and behavior
 * Based on the API endpoint: https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL
 */
class TwseApiDocumentationTest {

    @Test
    void documentActualAPIResponse() {
        // This test documents the actual TWSE STOCK_DAY_ALL API response format
        // provided in the GitHub comment
        
        System.out.println("=== TWSE STOCK_DAY_ALL API Documentation ===");
        System.out.println();
        
        System.out.println("API Endpoint: https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL");
        System.out.println("Method: GET");
        System.out.println("Description: Returns all stock data for the current trading day");
        System.out.println();
        
        System.out.println("Response Format:");
        System.out.println("- Content-Type: application/json");
        System.out.println("- Structure: JSON Array of stock objects");
        System.out.println();
        
        System.out.println("Example Response (from actual API):");
        String exampleResponse = """
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
                    }
                ]
                """;
        System.out.println(exampleResponse);
        
        System.out.println("Field Descriptions:");
        System.out.println("- Date: ROC date format YYYMMDD (e.g., '1140912' = 2025-09-12)");
        System.out.println("- Code: Stock code (4 digits, e.g., '0050', '2330')");
        System.out.println("- Name: Stock name in Traditional Chinese");
        System.out.println("- TradeVolume: Total trading volume (string)");
        System.out.println("- TradeValue: Total trading value in TWD (string)");
        System.out.println("- OpeningPrice: Opening price (string)");
        System.out.println("- HighestPrice: Highest price of the day (string)");
        System.out.println("- LowestPrice: Lowest price of the day (string)");
        System.out.println("- ClosingPrice: Closing price (string)");
        System.out.println("- Change: Price change from previous day (string, can be positive/negative)");
        System.out.println("- Transaction: Number of transactions (string)");
        System.out.println();
        
        System.out.println("Response Headers (from actual API):");
        System.out.println("- connection: keep-alive");
        System.out.println("- content-disposition: attachment;filename=STOCK_DAY_ALL.json");
        System.out.println("- content-encoding: gzip");
        System.out.println("- content-type: application/json");
        System.out.println("- server: nginx");
        System.out.println();
        
        System.out.println("ROC Calendar System:");
        System.out.println("- ROC (Republic of China) year = AD year - 1911");
        System.out.println("- Example: ROC 114 = AD 2025, ROC 113 = AD 2024");
        System.out.println("- Date format: YYYMMDD (3-digit year + 2-digit month + 2-digit day)");
        System.out.println();
        
        System.out.println("Key Differences from Previous API:");
        System.out.println("1. Returns ALL stocks for current day only (no date range queries)");
        System.out.println("2. Direct JSON array response (not wrapped in object)");
        System.out.println("3. Different field names and all values are strings");
        System.out.println("4. ROC date format is YYYMMDD instead of YYY/MM/DD");
        System.out.println("5. No need for monthly chunking - single API call gets all data");
    }

    @Test
    void documentNewAPIEndpoints() {
        System.out.println("=== New API Endpoints Documentation ===");
        System.out.println();
        
        System.out.println("1. GET /api/twse/stocks");
        System.out.println("   Description: Get all current day stock data");
        System.out.println("   Parameters: None");
        System.out.println("   Response: AllStocksResponse with list of all stocks");
        System.out.println();
        
        System.out.println("   Example Response:");
        String allStocksResponse = """
                {
                    "date": "2025-09-12",
                    "totalStocks": 1000,
                    "stocks": [
                        {
                            "stockCode": "0050",
                            "stockName": "元大台灣50",
                            "date": "2025-09-12",
                            "openingPrice": 55.75,
                            "highestPrice": 56.00,
                            "lowestPrice": 55.70,
                            "closingPrice": 56.00,
                            "change": "0.5500",
                            "tradeVolume": 59101728,
                            "tradeValue": 3303299908,
                            "transaction": 36831
                        }
                    ],
                    "message": "All stock data retrieved successfully"
                }
                """;
        System.out.println(allStocksResponse);
        
        System.out.println("2. GET /api/twse/stocks/{stockCode}");
        System.out.println("   Description: Get current day data for a specific stock");
        System.out.println("   Parameters: stockCode (path variable, 4 digits)");
        System.out.println("   Response: StockDataResponse for single stock");
        System.out.println();
        
        System.out.println("   Example Request: GET /api/twse/stocks/2330");
        System.out.println("   Example Response:");
        String singleStockResponse = """
                {
                    "stockCode": "2330",
                    "stockName": "台積電",
                    "date": "2025-09-12",
                    "openingPrice": 1135.00,
                    "highestPrice": 1145.00,
                    "lowestPrice": 1125.00,
                    "closingPrice": 1140.00,
                    "change": "+5.00",
                    "tradeVolume": 25486598,
                    "tradeValue": 29101234567,
                    "transaction": 8456
                }
                """;
        System.out.println(singleStockResponse);
        
        System.out.println("3. GET /api/twse/health");
        System.out.println("   Description: Health check endpoint");
        System.out.println("   Parameters: None");
        System.out.println("   Response: Simple text message");
        System.out.println();
        
        System.out.println("Error Responses:");
        System.out.println("- 400 Bad Request: Invalid stock code format");
        System.out.println("- 404 Not Found: Stock code not found in current day data");
        System.out.println("- 503 Service Unavailable: External TWSE API error");
        System.out.println("- 500 Internal Server Error: Unexpected server error");
    }

    @Test 
    void documentCommonStockCodes() {
        System.out.println("=== Common Taiwan Stock Codes ===");
        System.out.println();
        
        System.out.println("ETFs:");
        System.out.println("- 0050: 元大台灣50");
        System.out.println("- 0056: 元大高股息");
        System.out.println();
        
        System.out.println("Technology Stocks:");
        System.out.println("- 2330: 台積電 (TSMC)");
        System.out.println("- 2454: 聯發科 (MediaTek)");
        System.out.println("- 2317: 鴻海 (Foxconn)");
        System.out.println("- 2382: 廣達");
        System.out.println("- 3008: 大立光");
        System.out.println("- 2303: 聯電");
        System.out.println();
        
        System.out.println("Financial Stocks:");
        System.out.println("- 2881: 富邦金");
        System.out.println("- 2882: 國泰金");
        System.out.println("- 2883: 開發金");
        System.out.println();
        
        System.out.println("Traditional Industries:");
        System.out.println("- 1301: 台塑");
        System.out.println("- 2002: 中鋼");
        System.out.println("- 1216: 統一");
    }

    @Test
    void documentCurlExamples() {
        System.out.println("=== cURL Examples ===");
        System.out.println();
        
        System.out.println("1. Get all current day stock data:");
        System.out.println("curl -X GET \"http://localhost:8080/api/twse/stocks\"");
        System.out.println();
        
        System.out.println("2. Get TSMC (2330) current day data:");
        System.out.println("curl -X GET \"http://localhost:8080/api/twse/stocks/2330\"");
        System.out.println();
        
        System.out.println("3. Get ETF 0050 current day data:");
        System.out.println("curl -X GET \"http://localhost:8080/api/twse/stocks/0050\"");
        System.out.println();
        
        System.out.println("4. Health check:");
        System.out.println("curl -X GET \"http://localhost:8080/api/twse/health\"");
        System.out.println();
        
        System.out.println("5. Pretty print JSON response:");
        System.out.println("curl -X GET \"http://localhost:8080/api/twse/stocks/2330\" | python -m json.tool");
    }
}