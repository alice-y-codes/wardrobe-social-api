package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "outfits")
public class Outfit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;
    private String description;

    @ManyToMany
    @JoinTable(
            name = "outfit_items",
            joinColumns = @JoinColumn(name = "outfit_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items; // Outfit consists of multiple items

    private String imageUrl; // Thumbnail of outfit
}
