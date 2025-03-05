package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "outfits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outfit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String season;

    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private boolean isFavorite = false;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = false;

    @ManyToMany
    @JoinTable(
            name = "outfit_items",
            joinColumns = @JoinColumn(name = "outfit_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    @Builder.Default
    private Set<Item> items = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addOutfitItem(Item item) {
        if (item != null) {
            this.items.add(item);
        }
    }

    public void removeOutfitItem(Item item) {
        if (item != null) {
            this.items.remove(item);
        }
    }
}
