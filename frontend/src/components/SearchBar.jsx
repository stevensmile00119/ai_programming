import { useState } from 'react';
import { popularStocks } from '../data/mockStocks.js';
import './SearchBar.css';

const SearchBar = ({ onSearch, loading }) => {
  const [stockCode, setStockCode] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (stockCode.trim()) {
      onSearch(stockCode.trim());
    }
  };

  const handleQuickSelect = (code) => {
    setStockCode(code);
    onSearch(code);
  };

  return (
    <div className="search-bar">
      <form onSubmit={handleSubmit} className="search-form">
        <div className="input-group">
          <input
            type="text"
            value={stockCode}
            onChange={(e) => setStockCode(e.target.value)}
            placeholder="請輸入股票代碼（例如：2330）"
            className="stock-input"
            disabled={loading}
          />
          <button 
            type="submit" 
            className="search-button"
            disabled={loading || !stockCode.trim()}
          >
            {loading ? '查詢中...' : '查詢'}
          </button>
        </div>
      </form>

      <div className="popular-stocks">
        <h3>熱門股票</h3>
        <div className="stock-buttons">
          {popularStocks.map((stock) => (
            <button
              key={stock.code}
              onClick={() => handleQuickSelect(stock.code)}
              className="stock-quick-button"
              disabled={loading}
            >
              {stock.code} {stock.name}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};

export default SearchBar;