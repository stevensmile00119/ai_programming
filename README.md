# Taiwan Stock Exchange (TWSE) RESTful API

A comprehensive Spring Boot RESTful API for querying Taiwan Stock Exchange (TWSE) current day stock data using the official TWSE OpenAPI.

## Features

- **Current Day Stock Data**: Access real-time data for all listed Taiwan stocks
- **ETF Support**: Includes Exchange Traded Funds data (e.g., 0050, 0056)
- **Comprehensive Data**: Opening, highest, lowest, closing prices, trade volume, value, and transaction count
- **ROC Date Support**: Automatically converts Republic of China (ROC) calendar dates to standard format
- **RESTful Design**: Clean and intuitive API endpoints following REST principles
- **Comprehensive Error Handling**: Detailed error responses with appropriate HTTP status codes

## API Endpoints

### 1. Get All Current Day Stock Data

```http
GET /api/twse/stocks
```

Returns comprehensive data for all stocks traded on the current trading day.

**Response Example:**
```json
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
```

### 2. Get Specific Stock Data

```http
GET /api/twse/stocks/{stockCode}
```

Returns current day data for a specific stock by stock code.

**Path Parameters:**
- `stockCode` (string): 4-digit Taiwan stock code (e.g., "2330", "0050")

**Example Request:**
```bash
curl "http://localhost:8080/api/twse/stocks/2330"
```

**Response Example:**
```json
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
```

### 3. Health Check

```http
GET /api/twse/health
```

Simple health check endpoint to verify API service status.

**Response:**
```
TWSE API service is running
```

## Common Taiwan Stock Codes

### ETFs (Exchange Traded Funds)
- **0050** - 元大台灣50 (Yuanta Taiwan 50)
- **0056** - 元大高股息 (Yuanta Taiwan High Dividend Yield)

### Technology Stocks
- **2330** - 台積電 (TSMC - Taiwan Semiconductor)
- **2454** - 聯發科 (MediaTek)
- **2317** - 鴻海 (Foxconn)
- **2382** - 廣達 (Quanta Computer)
- **3008** - 大立光 (Largan Precision)
- **2303** - 聯電 (United Microelectronics)

### Financial Stocks
- **2881** - 富邦金 (Fubon Financial)
- **2882** - 國泰金 (Cathay Financial)
- **2883** - 開發金 (China Development Financial)

### Traditional Industries
- **1301** - 台塑 (Formosa Plastics)
- **2002** - 中鋼 (China Steel)
- **1216** - 統一 (Uni-President Enterprises)

## Response Fields

| Field | Type | Description |
|-------|------|-------------|
| `stockCode` | String | 4-digit Taiwan stock code |
| `stockName` | String | Stock name in Traditional Chinese |
| `date` | String | Trading date (YYYY-MM-DD format) |
| `openingPrice` | Number | Opening price in TWD |
| `highestPrice` | Number | Highest price of the day in TWD |
| `lowestPrice` | Number | Lowest price of the day in TWD |
| `closingPrice` | Number | Closing price in TWD |
| `change` | String | Price change from previous trading day |
| `tradeVolume` | Number | Total trading volume (shares) |
| `tradeValue` | Number | Total trading value in TWD |
| `transaction` | Number | Number of transactions |

## Error Handling

The API provides comprehensive error handling with appropriate HTTP status codes:

### 400 Bad Request
Invalid input parameters (e.g., incorrect stock code format).

```json
{
  "error": "Invalid Input",
  "message": "Stock code must be 4 digits",
  "timestamp": "2025-09-12T10:30:00",
  "status": 400
}
```

### 404 Not Found
Stock code not found in current day trading data.

```json
{
  "error": "Stock Not Found",
  "message": "No data found for stock code: 9999",
  "timestamp": "2025-09-12T10:30:00",
  "status": 404
}
```

### 503 Service Unavailable
External TWSE API is temporarily unavailable.

```json
{
  "error": "External API Error",
  "message": "Failed to fetch stock data from TWSE API: Connection timeout",
  "timestamp": "2025-09-12T10:30:00",
  "status": 503
}
```

### 500 Internal Server Error
Unexpected server error.

```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "timestamp": "2025-09-12T10:30:00",
  "status": 500
}
```

## Usage Examples

### cURL Examples

```bash
# Get all current day stock data
curl -X GET "http://localhost:8080/api/twse/stocks"

# Get TSMC (2330) current day data
curl -X GET "http://localhost:8080/api/twse/stocks/2330"

# Get ETF 0050 current day data
curl -X GET "http://localhost:8080/api/twse/stocks/0050"

# Pretty print JSON response
curl -X GET "http://localhost:8080/api/twse/stocks/2330" | python -m json.tool
```

### JavaScript Example

```javascript
// Fetch TSMC stock data
fetch('http://localhost:8080/api/twse/stocks/2330')
  .then(response => response.json())
  .then(data => {
    console.log(`${data.stockName} (${data.stockCode})`);
    console.log(`Closing Price: ${data.closingPrice} TWD`);
    console.log(`Change: ${data.change}`);
    console.log(`Volume: ${data.tradeVolume.toLocaleString()}`);
  })
  .catch(error => console.error('Error:', error));

// Fetch all stocks data
fetch('http://localhost:8080/api/twse/stocks')
  .then(response => response.json())
  .then(data => {
    console.log(`Trading Date: ${data.date}`);
    console.log(`Total Stocks: ${data.totalStocks}`);
    data.stocks.forEach(stock => {
      console.log(`${stock.stockName}: ${stock.closingPrice} TWD`);
    });
  });
```

### Python Example

```python
import requests
import json

# Get specific stock data
def get_stock_data(stock_code):
    url = f"http://localhost:8080/api/twse/stocks/{stock_code}"
    try:
        response = requests.get(url)
        if response.status_code == 200:
            data = response.json()
            print(f"{data['stockName']} ({data['stockCode']})")
            print(f"Closing Price: {data['closingPrice']} TWD")
            print(f"Change: {data['change']}")
            print(f"Volume: {data['tradeVolume']:,}")
        else:
            print(f"Error: {response.status_code}")
            print(response.json())
    except Exception as e:
        print(f"Request failed: {e}")

# Get all stocks data
def get_all_stocks():
    url = "http://localhost:8080/api/twse/stocks"
    try:
        response = requests.get(url)
        if response.status_code == 200:
            data = response.json()
            print(f"Trading Date: {data['date']}")
            print(f"Total Stocks: {data['totalStocks']}")
            
            # Display top 10 by volume
            stocks = sorted(data['stocks'], key=lambda x: x['tradeVolume'], reverse=True)
            print("\nTop 10 by Volume:")
            for stock in stocks[:10]:
                print(f"{stock['stockName']}: {stock['tradeVolume']:,}")
        else:
            print(f"Error: {response.status_code}")
    except Exception as e:
        print(f"Request failed: {e}")

# Usage
get_stock_data("2330")  # TSMC
get_stock_data("0050")  # ETF
get_all_stocks()
```

## Technical Details

### Data Source
- **API Endpoint**: https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL
- **Update Frequency**: Real-time during trading hours
- **Data Format**: JSON
- **Calendar System**: ROC (Republic of China) calendar converted to standard format

### ROC Calendar Conversion
The TWSE API uses the Republic of China (ROC) calendar system:
- ROC Year = Western Year - 1911
- Example: ROC 114 = 2025, ROC 113 = 2024
- The API automatically converts ROC dates to standard YYYY-MM-DD format

### Architecture
- **Framework**: Spring Boot 3.4.1
- **Java Version**: 17
- **HTTP Client**: Spring RestTemplate
- **JSON Processing**: Jackson with JSR310 support
- **Testing**: JUnit 5, Mockito, AssertJ

### Performance Considerations
- Single API call retrieves all current day stock data
- No rate limiting from TWSE API (as of implementation date)
- Efficient filtering for individual stock queries
- Caching can be implemented at service level if needed

## Development

### Requirements
- Java 17 or higher
- Maven 3.6 or higher
- Spring Boot 3.4.1

### Building
```bash
mvn clean compile
```

### Testing
```bash
mvn test
```

### Running Locally
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Project Structure
```
src/
├── main/java/com/example/backend/
│   ├── controller/          # REST controllers
│   ├── service/             # Business logic
│   ├── model/               # Domain models
│   ├── dto/                 # Data transfer objects
│   └── BackendApplication.java
└── test/java/com/example/backend/
    ├── controller/          # Controller integration tests
    ├── service/             # Service unit tests
    └── model/               # Model tests
```

## Notes

- This API provides **current trading day data only** - it does not support historical date range queries
- All monetary values are in Taiwan Dollars (TWD)
- Trading data is only available during Taiwan stock exchange business hours
- The API automatically handles market holidays and weekends (returns empty data)
- Stock names are provided in Traditional Chinese as returned by the official TWSE API

## License

This project is developed for educational and informational purposes. Please ensure compliance with TWSE API terms of service when using in production.