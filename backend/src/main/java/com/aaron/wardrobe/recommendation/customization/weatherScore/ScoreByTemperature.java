package com.aaron.wardrobe.recommendation.customization.weatherScore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import com.aaron.wardrobe.clothingItem.ClothingItem;

public class ScoreByTemperature {

    private final Map<String, List<Double>> temperatureRanges= new HashMap<>();

    /**
     * Loads the graph containing each ClothingItem subtype and their ideal temperature range from the CSV file
     * 
     * @throws IOException if reading the CSV file fails
     */
    public void loadFromCSV() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("temperature.csv");
        if (inputStream == null) {
            throw new IOException("temperature.csv not fount in classpath");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                String subtype = parts[0].trim();
                double minTemp = Double.parseDouble(parts[1].trim());
                double maxTemp = Double.parseDouble(parts[2].trim());


                temperatureRanges.put(subtype, List.of(minTemp, maxTemp));
            }
        }
    }

    public int score(ClothingItem item, double temp) {
        String subtype = item.getSubtype();
        List<Double> range = temperatureRanges.get(subtype);

        if (temp >= range.get(0) && temp <= range.get(1)) {
            return 0;
        }
        else if (temp < range.get(0) - 10 || temp > range.get(1) + 10) {
            return 5;
        }
        else return 2;
    }

    

    

}
