package com.aaron.wardrobe.recommendation.customization.weatherScore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.aaron.wardrobe.clothingItem.ClothingItem;

import java.util.*;

/**
 * This class creates a mapping for each weather forecast and the ClothingItem subtypes that are ideal
 * for the weather and subtypes that are bad for the weather.
 * 
 * List of weather forecasts: clear, sunny, cloudy, partly-cloudy, rainy, snowy, windy
 */
public class ScoreByWeather {

    private final Map<String, List<List<String>>> weatherMap = new HashMap<>();

    /**
     * Loads the graph containing each weather forecast and their ideal/bad mappings from the CSV file
     * 
     * @throws IOException if reading the CSV file fails
     */
    public void loadFromCSV() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("weather.csv");
        if (inputStream == null) {
            throw new IOException("weather.csv not fount in classpath");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length != 3) continue;

                String weather = parts[0].trim();
                List<String> idealSubtypes = Arrays.asList(parts[2].split(","));
                List<String> badSubtypes = Arrays.asList(parts[1].split(","));

                weatherMap.put(weather, List.of(idealSubtypes, badSubtypes));
            }
        }
    }

    /**
     * Scores a ClothingItem based on whether it is ideal, bad, or acceptable for the current weather
     * forecast
     * 
     * @param item is the ClothingItem to be scored
     * @param weather is the current weather forecast
     * @return the integer score of the ClothingItem, with lower scores being more ideal
     */
    public int score(ClothingItem item, String weather) {
        if (weather == null || !weatherMap.containsKey(weather)) {
            return 0;
        }

        String subtype = item.getSubtype();
        List<String> idealSubtypes = weatherMap.get(weather).get(0);
        List<String> badSubtypes = weatherMap.get(weather).get(1);

        if (!idealSubtypes.isEmpty() && idealSubtypes.contains(subtype)) {
            return 0;
        }
        else if (!badSubtypes.isEmpty() && badSubtypes.contains(subtype)) {
            return 3;
        }
        else {
            return 1;
        }
    }
}
