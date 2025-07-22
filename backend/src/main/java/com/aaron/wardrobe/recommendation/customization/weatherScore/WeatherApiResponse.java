package com.aaron.wardrobe.recommendation.customization.weatherScore;

import lombok.Data;

/**
 * Data Transfer Object representing the weather data of the current city
 */
@Data
public class WeatherApiResponse {
    
    private Current current;

    /**
     * Each object has a double temperature in Farenheit and a weather condition
     */
    @Data
    public static class Current {
        private double temp_f;
        private WeatherCondition condition;
    }

    /**
     * The weather condition is represented as a String
     */
    @Data
    public static class WeatherCondition {
        private String text;
    }
}
