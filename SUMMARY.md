# TWSE Stock Query System — Repository Summary

## Overview

A full-stack web application that proxies the Taiwan Stock Exchange (TWSE) OpenAPI and presents real-time stock data in a React frontend. The backend handles ROC (Republic of China) calendar date conversion and numeric parsing; the frontend provides a responsive, Taiwan-convention UI (red = price up, green = price down).

- **Backend:** Spring Boot 3.4.1 / Java 17 / Maven
- **Frontend:** React 19 / Vite 7 / plain CSS
- **External dependency:** `https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_ALL`

---

## Repository Structure

```
ai_programming/
├── src/                          # Spring Boot backend
│   ├── main/java/com/example/backend/
│   │   ├── BackendApplication.java
│   │   ├── controller/
│   │   │   └── StockController.java
│   │   ├── service/
│   │   │   └── TwseService.java
│   │   ├── model/
│   │   │   ├── TwseStockData.java
│   │   │   ├── TwseApiResponse.java
│   │   │   └── StockDailyData.java
│   │   └── dto/
│   │       ├── StockDataResponse.java
│   │       ├── AllStocksResponse.java
│   │       ├── ErrorResponse.java
│   │       └── StockQueryResponse.java
│   └── test/java/com/example/backend/   # 7 test classes
├── frontend/                     # React + Vite frontend
│   └── src/
│       ├── App.jsx
│       ├── components/
│       │   ├── SearchBar.jsx
│       │   ├── StockInfo.jsx
│       │   └── Loader.jsx
│       ├── services/
│       │   └── stockService.js
│       └── data/
│           └── mockStocks.js
├── pom.xml
├── mvnw / mvnw.cmd
├── CLAUDE.md
└── README.md
```

---

## Data Flow

```
User types stock code (4 digits)
        ↓
SearchBar.jsx  →  App.jsx (state)
        ↓
stockService.js  →  GET /api/twse/stocks/{code}  (localhost:8080)
        ↓
StockController.java  →  TwseService.java
        ↓
RestTemplate  →  TWSE STOCK_DAY_ALL API (external)
        ↓
Filter by code → ROC date conversion → numeric parsing
        ↓
StockDataResponse DTO  →  JSON response
        ↓
stockService.js maps fields  →  StockInfo.jsx renders
```

---

## Backend

### `StockController.java`

REST controller mounted at `/api/twse`. CORS is open to all origins.

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/twse/stocks` | GET | Returns all stocks for the current trading day |
| `/api/twse/stocks/{stockCode}` | GET | Returns a single stock by 4-digit code |
| `/api/twse/health` | GET | Liveness check — returns plain text |

HTTP status codes used: `200`, `400` (invalid code), `404` (code not found), `503` (TWSE unreachable), `500` (unexpected error).

### `TwseService.java`

All TWSE integration logic lives here:

- Calls the TWSE `STOCK_DAY_ALL` endpoint via `RestTemplate`
- Filters the full dataset down to the requested stock code
- Validates codes: must be exactly 4 numeric digits
- Converts ROC dates: `YYYMMDD` → `LocalDate` by adding 1911 to the year (e.g. `1140101` → `2025-01-01`)
- Parses string prices/volumes to `BigDecimal` / `Long`, stripping commas and treating `"--"` as null

### Models & DTOs

| Class | Role |
|-------|------|
| `TwseStockData` | Jackson model for the raw TWSE JSON array — all fields are strings; owns parsing logic |
| `TwseApiResponse` | Wrapper for the outer TWSE response envelope |
| `StockDailyData` | Typed daily data (prices as `BigDecimal`, volumes as `Long`) |
| `StockDataResponse` | DTO returned for single-stock queries |
| `AllStocksResponse` | DTO returned for all-stocks queries |
| `ErrorResponse` | Standard error shape: `error`, `message`, `timestamp`, `status` |

---

## Frontend

### `App.jsx`

Root component — owns all state (`stockData`, `loading`, `error`). Renders the page shell and orchestrates SearchBar → fetch → StockInfo.

### `SearchBar.jsx`

- Free-text input for any stock code
- 8 quick-select buttons for popular stocks (defined in `mockStocks.js`: 2330, 2317, 2454, 2412, 1301, 0050, 2881, 2882, 2303)
- On submit, calls the `onSearch` prop passed from App

### `StockInfo.jsx`

Renders the returned stock card:
- Stock name and code
- Closing price with change amount (red if up, green if down — Taiwan convention)
- Detail grid: opening / highest / lowest prices, trade volume, trade value, transactions

### `stockService.js`

Thin fetch wrapper at `http://localhost:8080/api/twse`. Maps backend DTO field names to the shape consumed by components and calculates `changePercent` from `change` and `closingPrice`.

### `Loader.jsx`

Animated CSS spinner with 4 rings, shown while fetch is in-flight.

---

## Tests

Located in `src/test/java/com/example/backend/`. Seven test classes covering:

| File | Scope |
|------|-------|
| `BackendApplicationTest` | Spring context loads |
| `StockControllerTest` | All endpoints via `@WebMvcTest` with mocked service |
| `TwseServiceTest` | Service logic with mocked `RestTemplate` |
| `TwseStockDataModelTest` | JSON parsing, ROC date conversion, comma handling |
| `ModelTest` | DTO serialization / deserialization |
| `TwseApiDocumentationTest` | Documents API specs in test output |
| `TwseApiRealResponseTest` | Patterns from real TWSE responses (disabled tests) |

Run all: `mvn test`  
Run one class: `mvn test -Dtest=TwseServiceTest`

---

## Configuration

| File | Purpose |
|------|---------|
| `pom.xml` | Spring Boot 3.4.1 parent, `spring-boot-starter-web`, `spring-boot-starter-test`, Java 17 |
| `.nvmrc` | Pins Node to `20.19.5` (both root and `frontend/`) |
| `frontend/vite.config.js` | Vite + React plugin, no proxy config (service layer hardcodes backend URL) |
| `frontend/eslint.config.js` | ESLint 9 flat config for React |

No `application.properties` — Spring Boot defaults are used (port 8080, no DB).

---

## Running Locally

Both servers must be running concurrently.

```bash
# Terminal 1 — backend (port 8080)
mvn spring-boot:run

# Terminal 2 — frontend (port 5173)
cd frontend
npm run dev
```

Open `http://localhost:5173` in a browser.

---

## Key Design Decisions

- **Single API call, client-side filter** — The backend fetches the full `STOCK_DAY_ALL` dataset (~1000 stocks) on every request and filters in memory. Simple and stateless; acceptable given the dataset size and TWSE's rate-limit tolerance.
- **Today's data only** — No historical query support. `STOCK_DAY_ALL` returns only the most recent trading day.
- **ROC calendar handled in service layer** — `TwseService` converts dates before they reach DTOs, so all downstream code uses standard `LocalDate`.
- **No `application.properties`** — Intentionally uses Spring Boot defaults to keep configuration minimal.
- **Frontend hardcodes backend URL** — `stockService.js` points to `localhost:8080`. A production deployment would require an environment variable or reverse proxy.
