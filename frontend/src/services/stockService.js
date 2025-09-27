/**
 * 股票 API 服務模組
 * 整合 Spring Boot 後端 API 串接
 */
class StockService {
  constructor() {
    // API 基礎路徑，指向本地後端 Spring Boot API
    this.baseURL = 'http://localhost:8080/api/twse';
  }

  /**
   * 根據股票代碼查詢股票資訊
   * @param {string} stockCode - 股票代碼 (例如: 2330)
   * @returns {Promise<Object>} 股票資訊物件
   */
  async getStockInfo(stockCode) {
    try {
      const response = await fetch(`${this.baseURL}/stocks/${stockCode}`);
      
      if (!response.ok) {
        // 處理 HTTP 錯誤狀態
        if (response.status === 404) {
          throw {
            success: false,
            message: `找不到股票代碼 ${stockCode} 的資訊`,
            code: 'STOCK_NOT_FOUND'
          };
        } else if (response.status === 400) {
          throw {
            success: false,
            message: `股票代碼格式無效: ${stockCode}`,
            code: 'INVALID_STOCK_CODE'
          };
        } else if (response.status >= 500) {
          throw {
            success: false,
            message: '伺服器暫時無法處理請求，請稍後再試',
            code: 'SERVER_ERROR'
          };
        } else {
          throw {
            success: false,
            message: `API 請求失敗 (${response.status})`,
            code: 'API_ERROR'
          };
        }
      }

      const backendData = await response.json();
      
      // 將後端資料格式轉換為前端期望的格式
      const mappedData = this.mapBackendDataToFrontendFormat(backendData);
      
      return {
        success: true,
        data: mappedData,
        timestamp: new Date().toISOString()
      };
      
    } catch (error) {
      // 如果是網路錯誤或其他非預期錯誤
      if (error.success === false) {
        // 已經是我們格式化的錯誤，直接拋出
        throw error;
      } else {
        // 網路或其他錯誤
        throw {
          success: false,
          message: `無法連接到伺服器，請檢查網路連線或確認後端服務是否啟動`,
          code: 'NETWORK_ERROR'
        };
      }
    }
  }

  /**
   * 將後端 API 資料格式轉換為前端期望的格式
   * @param {Object} backendData - 後端 API 回傳的資料
   * @returns {Object} 轉換後的前端格式資料
   */
  mapBackendDataToFrontendFormat(backendData) {
    // 計算漲跌幅百分比
    let changePercent = 0;
    if (backendData.change && backendData.closingPrice) {
      const change = parseFloat(backendData.change);
      const closingPrice = parseFloat(backendData.closingPrice);
      const previousPrice = closingPrice - change;
      if (previousPrice > 0) {
        changePercent = (change / previousPrice) * 100;
      }
    }

    return {
      code: backendData.stockCode,
      name: backendData.stockName,
      price: parseFloat(backendData.closingPrice) || 0,
      change: parseFloat(backendData.change) || 0,
      changePercent: changePercent,
      volume: backendData.tradeVolume || 0,
      high: parseFloat(backendData.highestPrice) || 0,
      low: parseFloat(backendData.lowestPrice) || 0,
      open: parseFloat(backendData.openingPrice) || 0,
      date: backendData.date
    };
  }

  /**
   * 批量查詢多支股票資訊（使用後端 API）
   * @param {string[]} stockCodes - 股票代碼陣列
   * @returns {Promise<Object[]>} 股票資訊陣列
   */
  async getMultipleStocks(stockCodes) {
    try {
      const promises = stockCodes.map(code => this.getStockInfo(code).catch(error => ({ error, code })));
      const results = await Promise.all(promises);
      
      const successResults = results
        .filter(result => !result.error)
        .map(result => result.data);
      
      return {
        success: true,
        data: successResults,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      throw {
        success: false,
        message: '批量查詢股票資訊失敗',
        code: 'BATCH_QUERY_ERROR'
      };
    }
  }
}

// 匯出單一實例
export const stockService = new StockService();
export default stockService;