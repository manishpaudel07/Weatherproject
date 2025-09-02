import React, { useState } from "react";
import axios from "axios";

function App() {
  const [city, setCity] = useState("");
  const [weather, setWeather] = useState(null);
  const [error, setError] = useState("");

  const fetchWeather = async () => {
    if (!city.trim()) {
      setWeather(null);
      setError("Please enter a city name.");
      return;
    }
    try {
      const response = await axios.get(`http://localhost:8081/weather/${city}`);
      setWeather(response.data);
      setError("");
    } catch (err) {
      setWeather(null);
      setError("Failed to fetch weather.");
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h1>Weather App</h1>
      <input
        type="text"
        placeholder="Enter city"
        value={city}
        onChange={e => setCity(e.target.value)}
      />
      <button onClick={fetchWeather}>Get Weather</button>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {weather && (
        <div>
          <h2>{weather.name}</h2>
          <p>
            Temperature:{" "}
            {weather.main && typeof weather.main.temp !== "undefined"
              ? `${weather.main.temp}°C`
              : "N/A"}
          </p>
          <p>Description: {weather.weather?.[0]?.description}</p>
          <p>TimeZone: {weather.timezone}</p>
          <p>Name of the city: {weather.name}</p>
        </div>
      )}
    </div>
  );
}

export default App;