package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    private Long userId; // Owner of the item

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String brand;

    @Column(nullable = false)
    private String category; // (e.g., "Shoes", "Jacket", "Dress")

    @Column(nullable = true)
    private String size;

    @Column(nullable = true)
    private String color;

    @Column(nullable = false)
    private String imageUrl;

    // Additional features
//    private String visibility; // e.g., "public", "private", "friends"
//    private LocalDateTime createdDate;
//    private LocalDateTime lastUpdatedDate;
//    @ElementCollection
//    private List<String> tags; // A list of tags that describe the outfit for better categorization and searching.


}
