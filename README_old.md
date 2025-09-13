# ai_programming
使用 AI 進行 coding 

## Taiwan Stock Exchange (TWSE) RESTful API

這是一個 Spring Boot RESTful API，用於查詢台灣上市股票的每日收盤資訊。

### 功能特色

- 查詢指定股票代碼在指定日期區間內的日線資料
- 支援開盤價、最高價、最低價、收盤價、成交量等資訊
- 從台灣證券交易所 OpenAPI 取得即時資料
- 完整的輸入驗證和錯誤處理
- RESTful API 設計

### 技術架構

- **Framework**: Spring Boot 3.4.1
- **Java Version**: Java 17
- **Build Tool**: Maven
- **外部資料源**: 台灣證券交易所 OpenAPI (https://openapi.twse.com.tw/)

### API 端點

#### 1. 查詢股票資料
```
GET /api/twse/stock/{stockNo}?startDate={YYYYMMDD}&endDate={YYYYMMDD}
```

**路徑參數:**
- `stockNo`: 股票代碼 (4位數字，例如: 2330)

**查詢參數:**
- `startDate`: 起始日期 (格式: YYYYMMDD，例如: 20241101)
- `endDate`: 結束日期 (格式: YYYYMMDD，例如: 20241130)

**回應範例 (成功):**
```json
{
  "stockCode": "2330",
  "stockName": "台積電",
  "queryPeriod": "20241101 to 20241130",
  "data": [
    {
      "date": "2024-11-01",
      "tradeVolume": 25486598,
      "tradeValue": 28751836910,
      "openingPrice": 1135.00,
      "highestPrice": 1145.00,
      "lowestPrice": 1125.00,
      "closingPrice": 1140.00,
      "change": "+5.00",
      "transaction": 15234
    }
  ],
  "message": "Data retrieved successfully"
}
```

**回應範例 (錯誤):**
```json
{
  "error": "Invalid Input",
  "message": "Stock code must be 4 digits",
  "timestamp": "2024-11-01T10:30:00",
  "status": 400
}
```

#### 2. 健康檢查
```
GET /api/twse/health
```

**回應:**
```
TWSE API service is running
```

### 常見股票代碼

| 股票代碼 | 公司名稱 |
|---------|---------|
| 2330    | 台積電   |
| 2317    | 鴻海     |
| 2454    | 聯發科   |
| 2382    | 廣達     |
| 3008    | 大立光   |
| 2303    | 聯電     |
| 1301    | 台塑     |
| 2881    | 富邦金   |
| 2002    | 中鋼     |
| 1216    | 統一     |

### 錯誤處理

API 提供完整的錯誤處理機制：

- **400 Bad Request**: 輸入參數錯誤
  - 股票代碼必須為4位數字
  - 日期格式必須為 YYYYMMDD
  - 起始日期必須早於結束日期
  
- **503 Service Unavailable**: 外部 API 服務不可用
  - TWSE API 連線失敗
  - API 回傳錯誤狀態
  
- **500 Internal Server Error**: 內部伺服器錯誤

### 建置與執行

#### 前置需求
- Java 17 或以上版本
- Maven 3.6 或以上版本

#### 編譯專案
```bash
mvn clean compile
```

#### 執行應用程式
```bash
mvn spring-boot:run
```

應用程式將在 `http://localhost:8080` 啟動。

#### 執行測試
```bash
mvn test
```

### 使用範例

#### 使用 curl 查詢台積電(2330)股價資料
```bash
# 查詢 2024年11月 台積電股價資料
curl "http://localhost:8080/api/twse/stock/2330?startDate=20241101&endDate=20241130"

# 查詢鴻海(2317)單日股價資料
curl "http://localhost:8080/api/twse/stock/2317?startDate=20241115&endDate=20241115"
```

#### 使用 JavaScript 呼叫 API
```javascript
const response = await fetch(
  'http://localhost:8080/api/twse/stock/2330?startDate=20241101&endDate=20241130'
);
const data = await response.json();
console.log(data);
```

#### 使用 Python 呼叫 API
```python
import requests

url = "http://localhost:8080/api/twse/stock/2330"
params = {
    "startDate": "20241101",
    "endDate": "20241130"
}

response = requests.get(url, params=params)
data = response.json()
print(data)
```

### 資料來源說明

本 API 透過台灣證券交易所 OpenAPI 取得資料：
- API 端點: `https://openapi.twse.com.tw/exchangeReport/STOCK_DAY`
- 資料更新頻率: 每日
- 支援範圍: 台灣上市股票 (不包含櫃買中心股票)
- 資料包含: 開盤價、最高價、最低價、收盤價、成交量、成交金額、成交筆數

### 限制與注意事項

1. **資料範圍**: 僅支援台灣上市股票，不包含櫃買中心 (OTC) 股票
2. **查詢限制**: 建議單次查詢不超過一年的資料範圍
3. **API 限制**: 受台灣證券交易所 API 使用限制約束
4. **假日處理**: 非交易日不會有資料回傳
5. **即時性**: 非即時行情，為每日收盤後更新的歷史資料

### 專案結構

```
src/main/java/com/example/backend/
├── BackendApplication.java          # Spring Boot 主程式
├── controller/
│   ├── HealthCheckController.java   # 健康檢查控制器
│   └── StockController.java         # 股票資料控制器
├── service/
│   └── TwseService.java            # TWSE API 服務層
├── model/
│   ├── StockDailyData.java         # 股票日資料模型
│   └── TwseApiResponse.java        # TWSE API 回應模型
└── dto/
    ├── StockQueryResponse.java      # 股票查詢回應 DTO
    └── ErrorResponse.java           # 錯誤回應 DTO
```

### 版本資訊

- v0.0.1-SNAPSHOT: 初始版本，支援基本股票資料查詢功能