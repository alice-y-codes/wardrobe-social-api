package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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
    private String brand;
    private String category; // (e.g., "Shoes", "Jacket", "Dress")
    private String imageUrl;
    private String color;
    private String size;

    // Additional features
//    private String visibility; // e.g., "public", "private", "friends"
//    private LocalDateTime createdDate;
//    private LocalDateTime lastUpdatedDate;
//    @ElementCollection
//    private List<String> tags; // A list of tags that describe the outfit for better categorization and searching.

}
