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
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile; // Associated Profile

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wardrobe_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Wardrobe wardrobe; // Associated Wardrobe

    @Column(nullable = false)
    private String name;

    @Column(nullable = true) // Optional brand
    private String brand;

    @Column(nullable = false)
    private String category; // Required (e.g., "Shoes", "Jacket", "Dress")

    @Column(nullable = true) // Optional size
    private String size;

    @Column(nullable = true) // Optional color
    private String color;

    @Column(nullable = true) // Optional image URL (can be empty if user hasn't uploaded one yet)
    private String imageUrl;

    @PrePersist
    protected void onCreate() {
        // Timestamp logic can be added here
    }

    @PreUpdate
    protected void onUpdate() {
        // Timestamp logic can be added here
    }
}
