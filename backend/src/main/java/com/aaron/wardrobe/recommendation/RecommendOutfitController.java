package com.aaron.wardrobe.recommendation;

import org.springframework.web.bind.annotation.RestController;

import com.aaron.wardrobe.clothingItem.ClothingItem;
import com.aaron.wardrobe.recommendation.customization.weatherScore.WeatherService;
import com.aaron.wardrobe.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/recommend")
public class RecommendOutfitController {

    @Autowired
    private RecommendOutfitService service;

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/{formality}/{city}")
    public List<ClothingItem> recommendOutfit(
        @PathVariable String formality, 
        @PathVariable String city,
        @AuthenticationPrincipal User user
    ) {
        double temperature = weatherService.getCurrentTemperature(city);
        String weatherCondition = weatherService.getSimplifiedWeatherCondition(city);
        return service.recommendOutfit(user, temperature, formality, weatherCondition);
    }

}
