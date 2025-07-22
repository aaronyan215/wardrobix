package com.aaron.wardrobe.recommendation.customization.compatabilityScore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aaron.wardrobe.clothingItem.ClothingItem;

import java.util.HashMap;

/**
 * A weighted graph representing how compatible each ClothingItem subtype is with each other subtype.
 * The heigher weight of an edge between two subtypes represents less compatibility, and two subtypes
 * without a connecting edge are uncompatible.
 */
public class ScoreByCompatibility {

    Map<String, Map<String, Integer>> graph = new HashMap<>();

    /**
     * Loads the graph containing each clothing subtype and their compatibility mappings from
     * the CSV file
     * 
     * @throws IOException if reading the CSV file fails
     */
    public void loadFromCSV() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("compatibility.csv");
        if (inputStream == null) {
            throw new IOException("compatibility.csv not found in classpath");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                String predecessor = parts[0].trim();
                String successor = parts[1].trim();
                int compatibilityScore = Integer.parseInt(parts[2].trim());

                addEdge(predecessor, successor, compatibilityScore);
            }
        }
    }

    /**
     * Scores a ClothinItem based on how compatible it is with the rest of the outfit
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
        String subtype = item.getSubtype();
        for (ClothingItem outfitItem : currentOutfit) {
            String outfitItemSubtype = outfitItem.getSubtype();
            score += graph.get(subtype).getOrDefault(outfitItemSubtype, 3);
        }
        return score;
    }

    /**
     * Creates an edge between two clothing subtypes with how compatible they are
     * 
     * @param predecessor is the current subtype
     * @param successor is the subtype being compared with predecessor
     * @param compatabilityScore is a numeric rating of how compatible the items are, with a 
     */
    public void addEdge(String predecessor, String successor, int compatabilityScore) {
        graph.computeIfAbsent(predecessor, k -> new HashMap<>()).put(successor, compatabilityScore);
        graph.computeIfAbsent(successor, k -> new HashMap<>()).put(predecessor, compatabilityScore);
    }

}
