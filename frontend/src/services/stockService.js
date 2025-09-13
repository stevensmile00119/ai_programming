import { mockStocks } from '../data/mockStocks.js';

/**
 * 股票 API 服務模組
 * 目前使用模擬資料，未來可替換成實際的 Spring Boot API 串接
 */
class StockService {
  constructor() {
    // API 基礎路徑，未來替換成實際的後端 API 網址
    this.baseURL = 'http://localhost:8080/api';
  }

  /**
   * 根據股票代碼查詢股票資訊
   * @param {string} stockCode - 股票代碼 (例如: 2330)
   * @returns {Promise<Object>} 股票資訊物件
   */
  async getStockInfo(stockCode) {
    // 模擬 API 請求延遲
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const stock = mockStocks[stockCode];
        if (stock) {
          resolve({
            success: true,
            data: stock,
            timestamp: new Date().toISOString()
          });
        } else {
          reject({
            success: false,
            message: `找不到股票代碼 ${stockCode} 的資訊`,
            code: 'STOCK_NOT_FOUND'
          });
        }
      }, 800); // 模擬網路延遲
    });
  }

  /**
   * 批量查詢多支股票資訊
   * @param {string[]} stockCodes - 股票代碼陣列
   * @returns {Promise<Object[]>} 股票資訊陣列
   */
  async getMultipleStocks(stockCodes) {
    return new Promise((resolve) => {
      setTimeout(() => {
        const results = stockCodes
          .map(code => mockStocks[code])
          .filter(stock => stock !== undefined);
        
        resolve({
          success: true,
          data: results,
          timestamp: new Date().toISOString()
        });
      }, 1000);
    });
  }

  /**
   * 未來實際 API 串接的方法範例
   * TODO: 替換成實際的 HTTP 請求
   */
  async fetchFromRealAPI(/* stockCode */) {
    /*
    try {
      const response = await fetch(`${this.baseURL}/stocks/${stockCode}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      return {
        success: true,
        data: data,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      throw {
        success: false,
        message: `API 請求失敗: ${error.message}`,
        code: 'API_ERROR'
      };
    }
    */
  }
}

// 匯出單一實例
export const stockService = new StockService();
export default stockService;