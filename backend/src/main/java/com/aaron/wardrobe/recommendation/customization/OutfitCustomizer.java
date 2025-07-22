package com.aaron.wardrobe.recommendation.customization;

import com.aaron.wardrobe.clothingItem.ClothingItem;
import com.aaron.wardrobe.recommendation.customization.colorScore.ScoreByColor;
import com.aaron.wardrobe.recommendation.customization.compatabilityScore.ScoreByCompatibility;
import com.aaron.wardrobe.recommendation.customization.weatherScore.ScoreByTemperature;
import com.aaron.wardrobe.recommendation.customization.weatherScore.ScoreByWeather;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * This class recieves a filtered collection of ClothingItems based on temperature, occasion, and weather, and creates
 * a custom cohesive outfit. 
 */
@Component
public class OutfitCustomizer {

    private final ScoreByTemperature scoreByTemperature = new ScoreByTemperature();
    private final ScoreByCompatibility scoreByCompatibility = new ScoreByCompatibility();
    private final ScoreByColor scoreByColor = new ScoreByColor();
    private final ScoreByWeather scoreByWeather = new ScoreByWeather();

    private final List<String> optionalTypes = List.of("headwear", "outerwear");

    @PostConstruct
    public void init() {
        try {
            scoreByCompatibility.loadFromCSV();
            scoreByColor.loadFromCSV();
            scoreByWeather.loadFromCSV();
            scoreByTemperature.loadFromCSV();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load one or both CSV files", e);
        }
    }

    public OutfitCustomizer() {
    }

    /**
     * Given a mapOfOptions, iterate through the keys, representing clothing type, and pick up to one clothing 
     * item to add to the outfit. Picks items based on their score, with lower scores being more desireable.
     * For optional ClothingItem subtypes, only select the best item if a ClothingItem exists in the list with a 
     * passing score.
     * 
     * @param mapOfOptions is a map of all options for the next clothing item in the outfit. The keys are the 
     * four different clothing types, and the values they map to represent the filtered list of ClothingItems
     * that correspond to the given type.
     * @param temp is the current temperature in Farenheit
     * @param weather is the current weather condition
     * @return the list containing the full final outfit
     */
    public List<ClothingItem> customizeOutfit(Map<String, List<ClothingItem>> mapOfOptions, double temp, String weather) {
        List<ClothingItem> outfit = new ArrayList<>();

        for (String key : mapOfOptions.keySet()) {
            boolean validMapping = mapOfOptions.get(key) != null && !mapOfOptions.get(key).isEmpty();
            if (validMapping) {
                if (optionalTypes.contains(key)) {

                    boolean found = pickBestOptionalItem(mapOfOptions.get(key), temp, weather, outfit) != null;

                    if (found) { outfit.add(pickBestOptionalItem(mapOfOptions.get(key), temp, weather, outfit)); }
                }
                else {
                    outfit.add(pickBestRequiredItem(mapOfOptions.get(key), temp, weather, outfit));
                }
            }
        }
        return outfit;
    }


    /**
     * Based on the given list of ClothingItem options, select the best item to add to the outfit based on score.
     * 
     * @param items is the list of ClothingItems to choose from
     * @param temp is the current temperature in Farenheit
     * @param weather is the current weather condition
     * @param currentOutfit is the list of ClothingItems representing the current outfit
     * @return the best ClothingItem to be added to the outfit
     */
    private ClothingItem pickBestRequiredItem(List<ClothingItem> items, double temp, String weather, List<ClothingItem> currentOutfit) {
        Map<ClothingItem, Integer> scores = new HashMap<>();
        for (ClothingItem item : items) {
            int score = tallyScore(item, temp, weather, currentOutfit);
            scores.put(item, score);
        }

        // Take the top 3 scoring items and order from best to worst
        List<Map.Entry<ClothingItem, Integer>> top3Entries = getTopOptions(scores);
        
        return weightedRandomSelector(top3Entries);
    }

    /**
     * Based on the given list of optional ClothingItem options, select the best item and add it to the outfit only if 
     * its score is desireable.
     * 
     * @param items is the list of ClothingItem options
     * @param temp is the current temperature in Farenheit
     * @param weather is the current weather condition
     * @param currentOutfit is the list of ClothingItems representing the current outfit
     * @return the best ClothingItem to be added to the outfit only if its score < 2, otherwise return null
     */
    private ClothingItem pickBestOptionalItem(List<ClothingItem> items, double temp, String weather, List<ClothingItem> currentOutfit) {
         Map<ClothingItem, Integer> scores = new HashMap<>();
        for (ClothingItem item : items) {
            int score = tallyScore(item, temp, weather, currentOutfit);
            scores.put(item, score);
        }

        // Take the top 3 scoring items and order from best to worst
        List<Map.Entry<ClothingItem, Integer>> top3Entries = getTopOptions(scores);
        
        // Only choose the ClothingItem if its score is desireable, otherwise do not include the subtype in the final outfit
        ClothingItem prospect = weightedRandomSelector(top3Entries);
        if (temp <= 40 && !prospect.getSubtype().equals("cap")) {
            return prospect;
        }
        else if (scores.get(prospect) < 4) {
            return prospect;
        }
        else return null;
    }

    private List<Map.Entry<ClothingItem, Integer>> getTopOptions(Map<ClothingItem, Integer> scores) {
        // Sort entries by score ascending
        List<Map.Entry<ClothingItem, Integer>> sorted = scores.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toList());

        int bestScore = sorted.get(0).getValue();

        // Collect all entries with best score
        List<Map.Entry<ClothingItem, Integer>> topScoreEntries = sorted.stream()
            .filter(e -> e.getValue() == bestScore)
            .collect(Collectors.toList());

        // If fewer than 5, add second best score entries
        if (topScoreEntries.size() < 5) {
            // Find second best score (first score > bestScore)
            OptionalInt secondBestOpt = sorted.stream()
                .mapToInt(Map.Entry::getValue)
                .filter(score -> score > bestScore)
                .findFirst();

            if (secondBestOpt.isPresent()) {
                int secondBestScore = secondBestOpt.getAsInt();

                List<Map.Entry<ClothingItem, Integer>> secondBestEntries = sorted.stream()
                    .filter(e -> e.getValue() == secondBestScore)
                    .limit(5 - topScoreEntries.size())
                    .collect(Collectors.toList());

                topScoreEntries.addAll(secondBestEntries);
            }
        }

        // Shuffle combined list
        Collections.shuffle(topScoreEntries);

        // Return top 3 from combined shuffled list (or fewer if less available)
        return topScoreEntries.stream().limit(3).collect(Collectors.toList());
    }

    /**
     * Helper method to calculate the score of a given ClothingItem based on season, color, and its compatibility with 
     * the current outfit.
     * 
     * @param item is the given ClothingItem
     * @param temp is the current temperature in Farenheit
     * @param weather is the current weather condition
     * @param currentOutfit is the list of ClothingItems representing the current outfit
     * @return an integer that represents the total score of the ClothingItem
     */
    private int tallyScore(ClothingItem item, double temp, String weather, List<ClothingItem> currentOutfit) {
        int temperatureScore = scoreByTemperature.score(item, temp);
        int colorScore = scoreByColor.score(item, currentOutfit);
        int compatibilityScore = scoreByCompatibility.score(item, currentOutfit);
        int weatherScore = scoreByWeather.score(item, weather);
        
        return temperatureScore + colorScore + compatibilityScore + weatherScore;
    }

    /**
     * Take up to the top 3 highest scoring ClothingItems and randomly select one with for the outfit using
     * a weighted selection algorithm.
     *
     * @param top3Entries is a sorted list of up to 3 of the highest scoring items
     * @return the chosen ClothingItem
     */
    private ClothingItem weightedRandomSelector(List<Map.Entry<ClothingItem, Integer>> top3Entries) {
        // count the total combined score of the prospective ClothingItems
        // start totalScore at 1 for the edge case where totalScore - entry.getValue() = 0
        int totalScore = 1;
        for (Map.Entry<ClothingItem, Integer> entry : top3Entries) {
            totalScore += entry.getValue() ;
        }
        // count the total weights to be the size of the array, with each item's weight being totalScore subtract
        // their score
        int totalWeights = 0;
        for (Map.Entry<ClothingItem, Integer> entry : top3Entries) {
            totalWeights += (totalScore - entry.getValue());
        }

        // add the item to the array, repeat for the size of its weight
        ClothingItem[] weightedOptions = new ClothingItem[totalWeights];
        int index = 0;
        for (Map.Entry<ClothingItem, Integer> entry : top3Entries) {
            for (int i = 0; i < (totalScore - entry.getValue()); i++) {
                weightedOptions[index] = entry.getKey();
                index++;
            }
        }

        // select a random index from the array and return the ClothingItem stored at that index
        Random random = new Random();
        return weightedOptions[random.nextInt(totalWeights)];
    }
}
