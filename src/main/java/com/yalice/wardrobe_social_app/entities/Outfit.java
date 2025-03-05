package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "outfits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false) // Link to Profile
    private Profile profile; // Associated Profile

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "season")
    private String season;

    @Column(name = "is_favorite")
    @Builder.Default
    private boolean isFavorite = false;

    @Column(name = "is_public")
    @Builder.Default
    private boolean isPublic = false;

    @ManyToMany
    @JoinTable(name = "outfit_items", joinColumns = @JoinColumn(name = "outfit_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
    @Builder.Default
    private Set<Item> items = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        // Implement timestamp handling if needed
    }

    @PreUpdate
    protected void onUpdate() {
        // Implement timestamp handling if needed
    }
}
