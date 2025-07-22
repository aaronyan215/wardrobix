package com.aaron.wardrobe.clothingItem;

import com.aaron.wardrobe.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clothes")
public class ClothingItemController {
    @Autowired
    private ClothingItemService service;

    @PostMapping
    public ClothingItem addClothingItem(@RequestBody ClothingItem item, @AuthenticationPrincipal User user) {
        item.setUser(user);
        System.out.println("saving item for USER ID: " + user.getId());
        return service.addClothingItem(item);
    }

    @PutMapping("/{id}")
    public ClothingItem updateClothingItem(@PathVariable Long id, @RequestBody ClothingItem item, @AuthenticationPrincipal User user) {
        item.setUser(user);
        return service.updateClothingItem(id, item);
    }

    @GetMapping
    public List<ClothingItem> getAllClothes(@AuthenticationPrincipal User user) {
        return service.getAllClothesForUser(user);
    }

    @GetMapping("/{id}")
    public ClothingItem getClothingItem(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return service.getClothingItemForUser(id, user).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void removeClothingItem(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.removeClothingItemForUser(id, user);
    }
}
