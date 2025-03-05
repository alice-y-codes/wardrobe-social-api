package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // Renaming the foreign key column
    private User user; // Associated User

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wardrobe_id", nullable = false)  // Link to Wardrobe
    private Wardrobe wardrobe; // Associated Wardrobe

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

    @PrePersist
    protected void onCreate() {
        // Implement timestamp handling if needed
    }

    @PreUpdate
    protected void onUpdate() {
        // Implement timestamp handling if needed
    }
}
