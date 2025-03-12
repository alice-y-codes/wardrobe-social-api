package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a clothing item in a user's wardrobe.
 * This entity captures details about an item, including its name, brand,
 * category, size, and other optional attributes.
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Item extends BaseEntity {

    /**
     * The profile associated with this item.
     * Represents the owner of the item.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    /**
     * The wardrobe to which this item belongs.
     * Represents the collection or wardrobe where the item is categorized.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wardrobe_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Wardrobe wardrobe;

    /**
     * The name of the item.
     * This field is required.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The brand of the item.
     * This field is optional.
     */
    @Column(nullable = true)
    private String brand;

    /**
     * The category of the item (e.g., "Shoes", "Jacket", "Dress").
     * This field is required.
     */
    @Column(nullable = false)
    private String category;

    /**
     * The size of the item.
     * This field is optional.
     */
    @Column(nullable = true)
    private String size;

    /**
     * The color of the item.
     * This field is optional.
     */
    @Column(nullable = true)
    private String color;

    /**
     * The URL for an image of the item.
     */
    @Column(nullable = false)
    private String imageUrl;

    /**
     * The description of the item.
     */
    @Column(length = 500)
    private String description;

    /**
     * The outfits that include this item.
     */
    @ManyToMany(mappedBy = "items")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Outfit> outfits = new ArrayList<>();
}
