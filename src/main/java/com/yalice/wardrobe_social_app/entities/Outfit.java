package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an outfit created by a user, which consists of a collection of
 * items.
 */
@Entity
@Table(name = "outfits")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Outfit extends BaseEntity {

    /**
     * The profile (user) associated with this outfit.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    /**
     * The name of the outfit.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The description of the outfit.
     */
    @Column(length = 1000)
    private String description;

    /**
     * The season associated with the outfit.
     */
    @Column
    private String season;

    /**
     * The category of the outfit (e.g., "Casual", "Formal").
     */
    @Column
    private String category;

    /**
     * The URL for an image of the outfit.
     */
    @Column
    private String imageUrl;

    /**
     * Indicates whether the outfit is marked as a favorite.
     */
    @Column
    private boolean favorite;

    /**
     * Indicates whether the outfit is public or private.
     */
    @Column
    private boolean isPublic;

    /**
     * The list of items that belong to this outfit.
     */
    @ManyToMany
    @JoinTable(name = "outfit_items", joinColumns = @JoinColumn(name = "outfit_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
    private Set<Item> items = new HashSet<>();

    /**
     * Adds an item to the outfit.
     * If the item is not already in the outfit, it is added.
     *
     * @param item the item to add to the outfit
     */
    public void addOutfitItem(Item item) {
        items.add(item);
    }

    /**
     * Removes an item from the outfit.
     *
     * @param item the item to remove from the outfit
     */
    public void removeOutfitItem(Item item) {
        items.remove(item);
    }
}
