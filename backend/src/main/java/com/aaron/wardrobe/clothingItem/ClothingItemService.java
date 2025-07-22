package com.aaron.wardrobe.clothingItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aaron.wardrobe.user.User;

import java.util.List;
import java.util.Optional;

@Service
public class ClothingItemService {
    @Autowired
    private ClothingItemRepository clothingItemRepository;

    public ClothingItem addClothingItem(ClothingItem item) {
        return clothingItemRepository.save(item);
    }

    public ClothingItem updateClothingItem(Long id, ClothingItem newItem) {
        ClothingItem targetItem = clothingItemRepository.findById(id).orElse(null);
        
        targetItem.setName(newItem.getName());
        targetItem.setType(newItem.getType());
        targetItem.setSubtype(newItem.getSubtype());
        targetItem.setColor(newItem.getColor());
        targetItem.setFormality(newItem.getFormality());

        return clothingItemRepository.save(targetItem);
    }

    public List<ClothingItem> getAllClothesForUser(User user) {
        return clothingItemRepository.findByUser(user);
    }

    public Optional<ClothingItem> getClothingItemForUser(Long id, User user) {
        return clothingItemRepository.findById(id)
            .filter(item -> item.getUser().getId().equals(user.getId()));
    }

    public void removeClothingItemForUser(Long id, User user) {
        clothingItemRepository.findById(id).ifPresent(item -> {
            if (item.getUser().getId().equals(user.getId())) {
                clothingItemRepository.deleteById(id);
            }
        });
    }
}
