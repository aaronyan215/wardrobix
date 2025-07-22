package com.aaron.wardrobe.recommendation;

import com.aaron.wardrobe.user.User;
import com.aaron.wardrobe.clothingItem.ClothingItem;
import com.aaron.wardrobe.clothingItem.ClothingItemService;
import com.aaron.wardrobe.recommendation.customization.OutfitCustomizer;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Creates a fully customized outfit with all provided ClothingItems in the wardrobe based on temperature,
 * cohesion, occasion, and weather.
 */
@Service
public class RecommendOutfitService {

    private final ClothingItemService service;
    private final OutfitCustomizer customizer;
    private final List<String> requiredTypes = List.of("top", "bottom", "footwear");

    public RecommendOutfitService(ClothingItemService service, OutfitCustomizer customizer) {
        this.service = service;
        this.customizer = customizer;
    }

    /**
     * Filters the wardrobe based on occasion/formality, then picks the best ClothingItem for each
     * requiredType.
     * 
     * @param temperature is the current temperature in Farenheit
     * @param formality is the required formality of the outfit
     * @param weather is the current weather
     * @return a list of ClothingItems representing the final curated outfit
     */
    public List<ClothingItem> recommendOutfit(User user, double temperature, String formality, String weather) {
        List<ClothingItem> allClothes = service.getAllClothesForUser(user);

        // Check to make sure user has at least one ClothingItem per requiredType
        checkForEmptyRequiredTypes(allClothes);

        // Filtering logic: filters out clothes that do not match the occasion
        List<ClothingItem> filteredClothes = allClothes
            .stream()
            .filter(item -> item.getFormality() != null && 
                (item.getFormality().equals(formality)) || item.getFormality().equals("any"))
            .collect(Collectors.toList());

        Map<String, List<ClothingItem>> mapOfOptions = filteredClothes.stream()
            .collect(Collectors.groupingBy(ClothingItem::getType));

        // Check if any required types have no options after filtering. If yes, use unfiltered options instead
        for (String requiredType : requiredTypes) {
            List<ClothingItem> items = mapOfOptions.get(requiredType);

            if (items == null || items.isEmpty()) {
                List<ClothingItem> fallbackItems = allClothes.stream()
                    .filter(item -> item.getType().equals(requiredType))
                    .collect(Collectors.toList());

                mapOfOptions.put(requiredType, fallbackItems);
            }
        }

        return customizer.customizeOutfit(mapOfOptions, temperature, weather);
    }

    /**
     * Checks to make sure at least one ClothingItem exists in wardrobe for each requiredType.
     * 
     * @param allItems is the list of all items in wardrobe before filtering
     */
    private void checkForEmptyRequiredTypes(List<ClothingItem> allItems) {
        for (String requiredType : requiredTypes) {
            boolean exists = allItems.stream()
                .anyMatch(item -> item.getType().equals(requiredType));
            if (!exists) {
                throw new IllegalStateException("You cannot make an outfit without any " + requiredType);
            }
        }
    }

}
