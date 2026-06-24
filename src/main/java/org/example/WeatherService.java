package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WeatherService {
    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    @Value("${weather.api.key}")
    private String API_KEY;

    @Value("${weather.api.url}")
    private String WEATHER_API_URL;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "weather", key = "#city.toLowerCase()")
    public WeatherResponse getWeather(String city) throws IOException, InterruptedException {
        log.info("CACHE MISS - fetching from OpenWeatherMap API for city: {}", city);
        String url = buildUrl(city);
        String responseBody = fetchWeatherJson(url);
        return parseWeatherResponse(responseBody);
    }

    private String buildUrl(String city) {
        return String.format(WEATHER_API_URL, city, API_KEY);
    }

    private String fetchWeatherJson(String url) throws IOException, InterruptedException {
        log.info("Calling external API: {}", url.replaceAll("appid=[^&]+", "appid=***"));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("API responded with status: {}", response.statusCode());
        return response.body();
    }

    private WeatherResponse parseWeatherResponse(String json) throws IOException {
        return objectMapper.readValue(json, WeatherResponse.class);
    }
}