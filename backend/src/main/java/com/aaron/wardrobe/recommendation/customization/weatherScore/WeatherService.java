package com.aaron.wardrobe.recommendation.customization.weatherScore;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This class queries data from WeatherAPI, retrieving real-time, simplified weather conditions and 
 * current temperatures. 
 * Supports converting raw weather descriptions into standardized categories (clear, 
 * sunny, cloudy, rainy, snowy, windy) and fetching the current temperature in Fahrenheit.
 */
@Service
public class WeatherService {

    private final String API_KEY = "8cdd7c5eb69e4d58991215644251807";
    private final String BASE_URL = "http://api.weatherapi.com/v1/current.json";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches the current temperature in Farenheit for the specified city
     * 
     * @param location is the city name to fetch temperature for (e.g. "Eugene")
     * @return the current temperature in Farenheit of the given city
     */
    @SuppressWarnings("null")
    public double getCurrentTemperature(String location) {
        String uri = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("key", API_KEY)
                .queryParam("q", location)
                .toUriString();

        WeatherApiResponse response = restTemplate.getForObject(uri, WeatherApiResponse.class);
        return response.getCurrent().getTemp_f();
    }

    /**
     * Fetches the current weather condition of the specified city and simplifies it to
     * one of: clear, sunny, cloudy, rainy, snowy, windy
     * 
     * @param location is the city name to fetch weather conditions for (e.g. "Eugene")
     * @return the simplified weather condition of the given city
     */
    @SuppressWarnings("null")
    public String getSimplifiedWeatherCondition(String location) {
        String uri = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("key", API_KEY)
                .queryParam("q", location)
                .toUriString();

        WeatherApiResponse response = restTemplate.getForObject(uri, WeatherApiResponse.class);
        String original = response.getCurrent().getCondition().getText().toLowerCase();

        if (original.contains("clear")) return "clear";
        if (original.contains("sunny")) return "sunny";
        if (original.contains("cloud")) return "cloudy";
        if (original.contains("rain") || original.contains("drizzle")) return "rainy";
        if (original.contains("snow") || original.contains("ice") || original.contains("sleet")) return "snowy";
        if (original.contains("wind")) return "windy";

        return "clear";
    }
}
