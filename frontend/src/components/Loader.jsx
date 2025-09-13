import './Loader.css';

const Loader = ({ message = "載入中..." }) => {
  return (
    <div className="loader-container">
      <div className="loader-spinner">
        <div className="spinner-ring"></div>
        <div className="spinner-ring"></div>
        <div className="spinner-ring"></div>
        <div className="spinner-ring"></div>
      </div>
      <p className="loader-message">{message}</p>
    </div>
  );
};

export default Loader;