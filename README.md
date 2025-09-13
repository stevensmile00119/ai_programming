# 台灣股票價格查詢系統 (Taiwan Stock Query System)

這是一個使用 React + Spring Boot 構建的台灣股票價格查詢系統。前端使用 React 18 + Vite，後端使用 Spring Boot 3。

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
- ✅ 模擬資料系統
- ✅ API 服務模組架構

### 🔮 未來規劃
- [ ] 與 Spring Boot 後端 API 串接
- [ ] 即時股價更新
- [ ] 股票圖表顯示
- [ ] 收藏股票功能
- [ ] 股票搜尋歷史
- [ ] 價格提醒功能

## 快速開始

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