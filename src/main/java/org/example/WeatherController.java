package org.example;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/weather")
@CrossOrigin(origins = "*")
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    private ObjectMapper mapper;

    @GetMapping("/{city}")
    public ResponseEntity<?> getWeather(@PathVariable("city") String city) {
        try {
            WeatherResponse weather = (weatherService.getWeather(city));
            return ResponseEntity.ok(weather);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to fetch weather.");
        }
    }

}
