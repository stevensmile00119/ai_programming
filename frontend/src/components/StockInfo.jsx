import './StockInfo.css';

const StockInfo = ({ stockData, error }) => {
  if (error) {
    return (
      <div className="stock-info error">
        <div className="error-message">
          <h3>❌ 查詢失敗</h3>
          <p>{error.message}</p>
        </div>
      </div>
    );
  }

  if (!stockData) {
    return null;
  }

  const { name, code, price, change, changePercent, volume, high, low, open } = stockData;
  const isPositive = change >= 0;

  return (
    <div className="stock-info">
      <div className="stock-header">
        <h2 className="stock-title">
          {name} ({code})
        </h2>
        <div className="last-update">
          最後更新: {new Date().toLocaleString('zh-TW')}
        </div>
      </div>

      <div className="price-section">
        <div className="current-price">
          <span className="price-value">NT$ {price.toFixed(2)}</span>
          <div className={`price-change ${isPositive ? 'positive' : 'negative'}`}>
            <span className="change-icon">{isPositive ? '▲' : '▼'}</span>
            <span className="change-value">
              {isPositive ? '+' : ''}{change.toFixed(2)}
            </span>
            <span className="change-percent">
              ({isPositive ? '+' : ''}{changePercent.toFixed(2)}%)
            </span>
          </div>
        </div>
      </div>

      <div className="stock-details">
        <div className="detail-grid">
          <div className="detail-item">
            <span className="detail-label">開盤價</span>
            <span className="detail-value">NT$ {open.toFixed(2)}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">最高價</span>
            <span className="detail-value">NT$ {high.toFixed(2)}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">最低價</span>
            <span className="detail-value">NT$ {low.toFixed(2)}</span>
          </div>
          <div className="detail-item">
            <span className="detail-label">成交量</span>
            <span className="detail-value">{volume.toLocaleString()}</span>
          </div>
        </div>
      </div>

      <div className="stock-note">
        <p>* 以上資料為模擬數據，僅供參考</p>
      </div>
    </div>
  );
};

export default StockInfo;