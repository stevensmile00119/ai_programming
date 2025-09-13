import { useState } from 'react'
import SearchBar from './components/SearchBar.jsx'
import StockInfo from './components/StockInfo.jsx'
import Loader from './components/Loader.jsx'
import { stockService } from './services/stockService.js'
import './App.css'

function App() {
  const [stockData, setStockData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleSearch = async (stockCode) => {
    setLoading(true)
    setError(null)
    setStockData(null)

    try {
      const response = await stockService.getStockInfo(stockCode)
      setStockData(response.data)
    } catch (err) {
      setError(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1 className="app-title">🏢 台灣股票價格查詢系統</h1>
        <p className="app-subtitle">快速查詢台股即時資訊</p>
      </header>

      <main className="app-main">
        <SearchBar onSearch={handleSearch} loading={loading} />
        
        {loading && <Loader message="正在查詢股票資訊..." />}
        
        <StockInfo stockData={stockData} error={error} />
      </main>

      <footer className="app-footer">
        <p>© 2024 台灣股票查詢系統 | 僅供參考，投資有風險</p>
      </footer>
    </div>
  )
}

export default App
