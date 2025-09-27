# 台灣股票價格查詢系統 (Taiwan Stock Query System)

這是一個使用 React + Spring Boot 構建的台灣股票價格查詢系統。前端使用 React 19 + Vite，後端使用 Spring Boot 3，**已完成前後端整合**。

## 🎯 專案特色

- ✅ **前後端完全整合**: React 前端已串接 Spring Boot 後端 API
- ✅ **即時股票資料**: 後端串接台灣證券交易所公開 API
- ✅ **完善錯誤處理**: 處理各種 API 錯誤情況並顯示友善訊息
- ✅ **CORS 支援**: 已配置跨域請求支援
- ✅ **響應式設計**: 支援手機、平板、桌面裝置

## 🚀 快速啟動

### 1. 啟動後端服務

```bash
# 在專案根目錄執行
mvn spring-boot:run
```

後端將在 http://localhost:8080 啟動

### 2. 啟動前端應用

```bash
# 切換到 frontend 目錄
cd frontend

# 安裝依賴 (首次執行)
npm install

# 啟動開發伺服器
npm run dev
```

前端將在 http://localhost:5173 啟動

### 3. 測試整合功能

1. 開啟瀏覽器訪問 http://localhost:5173
2. 點選熱門股票按鈕（如：2330 台積電）
3. 或手動輸入股票代碼查詢
4. 系統會實際呼叫後端 API 並顯示結果

## 📡 API 端點

後端提供以下 RESTful API 端點：

- `GET /api/twse/health` - 服務健康檢查
- `GET /api/twse/stocks` - 取得所有股票當日資料  
- `GET /api/twse/stocks/{stockCode}` - 取得指定股票當日資料

## 🔧 技術架構

### 前端 (React)
- React 19 + Vite
- 實際 HTTP API 呼叫 (已移除 mock 資料)
- 完整錯誤處理與使用者體驗

### 後端 (Spring Boot)  
- Spring Boot 3.4.1 + Java 17
- 串接台灣證券交易所公開 API
- CORS 跨域支援配置
- 完善的錯誤回應機制

## 🧪 測試指令

```bash
# 測試後端健康狀態
curl http://localhost:8080/api/twse/health

# 測試股票查詢 API  
curl http://localhost:8080/api/twse/stocks/2330

# 前端程式碼檢查
cd frontend && npm run lint

# 前端建構測試
cd frontend && npm run build
```

---

## 專案結構

```
ai_programming/
├── README.md              # 專案說明文件
├── pom.xml               # Spring Boot 後端配置
├── src/                  # Spring Boot 後端程式碼
└── frontend/             # React 前端專案
    ├── src/
    │   ├── components/   # React 元件
    │   │   ├── SearchBar.jsx     # 搜尋列元件
    │   │   ├── StockInfo.jsx     # 股票資訊顯示元件
    │   │   └── Loader.jsx        # 載入動畫元件
    │   ├── services/     # API 服務模組
    │   │   └── stockService.js   # 股票資料服務
    │   ├── data/         # 模擬資料
    │   │   └── mockStocks.js     # 模擬股票資料
    │   ├── App.jsx       # 主要應用程式元件
    │   └── main.jsx      # 應用程式入口點
    ├── package.json      # 前端依賴配置
    └── vite.config.js    # Vite 構建配置
```

## 功能特色

### 🎯 已實現功能
- ✅ 基礎 React 專案架構 (使用 Vite)
- ✅ 股票代碼查詢表單
- ✅ 股票資訊顯示 (名稱、現價、漲跌、成交量等)
- ✅ 載入動畫效果
- ✅ 熱門股票快速查詢按鈕
- ✅ 響應式設計 (支援手機、平板、桌面)
- ✅ **與 Spring Boot 後端 API 完整整合**
- ✅ **實際台股資料串接 (已移除 mock 資料)**
- ✅ **完善錯誤處理與友善提示**
- ✅ **CORS 跨域請求支援**

### 🔮 未來規劃
- [ ] 即時股價更新
- [ ] 股票圖表顯示
- [ ] 收藏股票功能
- [ ] 股票搜尋歷史
- [ ] 價格提醒功能

## 快速開始

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
### 前置需求
- Node.js 20.19+ 或 22.12+ (Vite 需求)
- npm 8.0+
- Java 17+ (用於後端開發)
- Maven 3.6+ (用於後端開發)

> **💡 提示**：專案包含 `.nvmrc` 檔案，如使用 nvm 可執行 `nvm use` 自動切換至建議版本 (20.19.5)

### 安裝與執行前端

1. **進入前端目錄**
   ```bash
   cd frontend
   ```

2. **安裝依賴**
   ```bash
   npm install
   ```

3. **啟動開發伺服器**
   ```bash
   npm run dev
   ```

4. **訪問應用程式**
   
   開啟瀏覽器訪問：http://localhost:5173

### 前端可用指令

- `npm run dev` - 啟動開發伺服器
- `npm run build` - 建構生產版本
- `npm run preview` - 預覽建構結果
- `npm run lint` - 執行程式碼檢查

### 後端開發 (未來)

1. **啟動 Spring Boot 應用**
   ```bash
   mvn spring-boot:run
   ```

2. **API 端點 (規劃中)**
   - `GET /api/stocks/{code}` - 查詢單一股票資訊
   - `GET /api/stocks/batch` - 批量查詢股票資訊
   - `GET /api/stocks/popular` - 取得熱門股票清單

## API 串接指南

### 目前模擬資料系統

前端目前使用 `src/services/stockService.js` 提供模擬資料。該服務包含：

```javascript
// 查詢單一股票
const response = await stockService.getStockInfo('2330');

// 批量查詢股票
const response = await stockService.getMultipleStocks(['2330', '2317']);
```

### 未來 API 整合步驟

1. **更新 API 基礎路徑**
   
   在 `src/services/stockService.js` 中更新 `baseURL`：
   ```javascript
   this.baseURL = 'http://localhost:8080/api'; // 改為實際 API 地址
   ```

2. **實現實際 HTTP 請求**
   
   取消註解 `fetchFromRealAPI` 方法中的程式碼，並替換模擬邏輯。

3. **錯誤處理**
   
   根據後端 API 規格調整錯誤處理邏輯。

4. **資料格式對應**
   
   確保前端元件與後端 API 回傳的資料格式一致。

## 技術棧

### 前端
- **Framework**: React 18
- **Build Tool**: Vite 5
- **Language**: JavaScript (ES6+)
- **Styling**: CSS3 (with Flexbox & Grid)
- **Package Manager**: npm

### 後端 (規劃中)
- **Framework**: Spring Boot 3
- **Language**: Java 17
- **Build Tool**: Maven
- **API Style**: RESTful API

## 開發指南

### 新增股票資料

在 `src/data/mockStocks.js` 中新增股票資料：

```javascript
export const mockStocks = {
  '股票代碼': {
    code: '股票代碼',
    name: '股票名稱',
    price: 現價,
    change: 漲跌金額,
    changePercent: 漲跌百分比,
    volume: 成交量,
    high: 最高價,
    low: 最低價,
    open: 開盤價
  }
};
```

### 新增元件

1. 在 `src/components/` 目錄下建立新的 `.jsx` 檔案
2. 建立對應的 `.css` 檔案用於樣式
3. 在 `App.jsx` 中引入並使用新元件

### 樣式指南

- 使用 CSS Modules 或一般 CSS 檔案
- 遵循響應式設計原則
- 支援手機、平板、桌面三種尺寸
- 使用台灣股票慣用的紅漲綠跌色彩規範

## 部署

### 前端部署

1. **建構生產版本**
   ```bash
   cd frontend
   npm run build
   ```

2. **部署 `dist/` 目錄** 到靜態網站服務 (如 Nginx、Apache、Vercel、Netlify 等)

### 全端部署 (未來)

1. 後端：將 Spring Boot 應用部署到雲端平台 (如 AWS、Google Cloud、Azure)
2. 前端：更新 API 基礎路徑為正式環境地址
3. 資料庫：配置 PostgreSQL 或 MySQL 資料庫
4. 反向代理：使用 Nginx 處理靜態資源與 API 路由

## 貢獻指南

1. Fork 專案
2. 建立功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交變更 (`git commit -m 'Add some amazing feature'`)
4. 推送分支 (`git push origin feature/amazing-feature`)
5. 開啟 Pull Request

## 授權

© 2024 台灣股票查詢系統 | 僅供學習與參考用途

---

## 免責聲明

⚠️ **重要提醒**：
- 本系統目前使用模擬資料，**非即時股價**
- 所有資料僅供參考，不構成投資建議
- 投資有風險，請謹慎評估後再做決策
- 實際投資前請參考官方證券交易所資料