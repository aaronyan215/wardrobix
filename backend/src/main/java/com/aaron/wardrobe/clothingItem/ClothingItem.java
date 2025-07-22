package com.aaron.wardrobe.clothingItem;

import com.aaron.wardrobe.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClothingItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // top, bottom, outer, footwear, headwear
    private String subtype; // t-shirt, shorts, jeans, etc.
    private String color;
    private String formality;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
