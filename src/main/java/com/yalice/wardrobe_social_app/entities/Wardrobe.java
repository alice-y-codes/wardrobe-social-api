package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wardrobe that contains clothing items.
 */
@Entity
@Table(name = "wardrobes")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Wardrobe extends BaseEntity {

    /**
     * The name of the wardrobe.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The description of the wardrobe.
     */
    @Column(length = 500)
    private String description;

    /**
     * The profile that owns this wardrobe.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    /**
     * The items contained in this wardrobe.
     */
    @OneToMany(mappedBy = "wardrobe", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Item> items = new ArrayList<>();
}
