package com.aaron.wardrobe.recommendation.customization.colorScore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import com.aaron.wardrobe.clothingItem.ClothingItem;

/**
 * This class creates a mapping for each ClothingItem color and the colors they do NOT match with, and 
 * uses the map to score a given ClothingItem based on its color compatibility.
 * 
 * List of possible colors: black, white, grey, brown, pink, maroon, cyan, magenta, olive, cream, navy-blue, 
 * sky-blue, lavender, red, blue, yellow, gold, orange, green, purple
 */
public class ScoreByColor {

    private final Map<String, List<String>> uncompatibleColorsMap = new HashMap<>();

    /**
     * Loads the graph containing each clothing subtype and their compatibility mappings from the CSV file
     * 
     * @throws IOException if reading the CSV file fails
     */
    public void loadFromCSV() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("colors.csv");
        if (inputStream == null) {
            throw new IOException("colors.csv not found in classpath");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length != 2) continue;

                String color = parts[0].trim();
                List<String> badColors = Arrays.asList(parts[1].split(","));

                uncompatibleColorsMap.put(color, badColors);
            }
        }
    }

    /**
     * Scores a ClothingItem based on how well it's color complements the rest of the outfit.
     * 
     * @param item is the ClothingItem to be scored
     * @param currentOutfit is a list of ClothingItems representing the current outfit
     * @return the integer score of the ClothingItem, with lower scores being more ideal
     */
    public int score(ClothingItem item, List<ClothingItem> currentOutfit) {
        int score = 0;
        if (currentOutfit.isEmpty()) {
            return score;
        }
        String itemColor = item.getColor();
        for (ClothingItem outfitItem : currentOutfit) {
            String outfitItemColor = outfitItem.getColor();
            if (uncompatibleColorsMap.containsKey(itemColor) && 
                uncompatibleColorsMap.get(itemColor).contains(outfitItemColor)) {
                score += 1;
            }
        }
        return score;
    }
}
