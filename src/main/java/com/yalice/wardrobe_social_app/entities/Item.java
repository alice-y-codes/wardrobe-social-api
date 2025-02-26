package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Owner of the item

    private String name;
    private String category; // (e.g., "Shoes", "Jacket", "Dress")
    private String imageUrl;
}
