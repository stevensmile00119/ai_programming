// 模擬台灣股票資料
export const mockStocks = {
  '2330': {
    code: '2330',
    name: '台積電',
    price: 592.00,
    change: 8.00,
    changePercent: 1.37,
    volume: 45620,
    high: 595.00,
    low: 584.00,
    open: 587.00
  },
  '2317': {
    code: '2317',
    name: '鴻海',
    price: 108.50,
    change: -2.50,
    changePercent: -2.25,
    volume: 98753,
    high: 111.50,
    low: 107.00,
    open: 110.00
  },
  '2454': {
    code: '2454',
    name: '聯發科',
    price: 1025.00,
    change: 15.00,
    changePercent: 1.49,
    volume: 12387,
    high: 1030.00,
    low: 1010.00,
    open: 1015.00
  },
  '2412': {
    code: '2412',
    name: '中華電',
    price: 126.50,
    change: 0.50,
    changePercent: 0.40,
    volume: 8642,
    high: 127.00,
    low: 125.50,
    open: 126.00
  },
  '1301': {
    code: '1301',
    name: '台塑',
    price: 95.80,
    change: -1.20,
    changePercent: -1.24,
    volume: 15432,
    high: 97.50,
    low: 95.20,
    open: 96.80
  }
};

// 常用的台灣股票代碼清單
export const popularStocks = [
  { code: '2330', name: '台積電' },
  { code: '2317', name: '鴻海' },
  { code: '2454', name: '聯發科' },
  { code: '2412', name: '中華電' },
  { code: '1301', name: '台塑' },
  { code: '2881', name: '富邦金' },
  { code: '2882', name: '國泰金' },
  { code: '2303', name: '聯電' }
];