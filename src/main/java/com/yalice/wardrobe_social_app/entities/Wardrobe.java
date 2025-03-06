package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a wardrobe associated with a profile. It contains a collection of items and is
 * managed by the profile owner.
 */
@Entity
@Table(name = "wardrobes")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wardrobe extends BaseEntity {

    /**
     * The profile associated with the wardrobe.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    /**
     * The name of the wardrobe.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The items contained within the wardrobe.
     */
    @OneToMany(mappedBy = "wardrobe", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Item> items = new HashSet<>();
}
