package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String API_KEY;

    @Value("${weather.api.url}")
    private String WEATHER_API_URL;

    public WeatherResponse getWeather(String city) throws IOException, InterruptedException {
        String url = String.format(WEATHER_API_URL, city, API_KEY);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response Body: " + response.body());

        ObjectMapper mapper = new ObjectMapper();
        WeatherResponse weatherResponse = mapper.readValue(response.body(), WeatherResponse.class);
        System.out.println("Weather Response: " + weatherResponse.toString());
        return weatherResponse;


    }


}
