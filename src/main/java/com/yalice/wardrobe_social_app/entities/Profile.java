package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a user profile that includes personal details, preferences, and
 * associated collections like wardrobes, outfits, and posts.
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Profile extends BaseEntity {

    /**
     * The associated user for this profile.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    /**
     * The bio of the profile.
     */
    @Column(length = 500)
    private String bio;

    /**
     * The location of the user.
     */
    @Column
    private String location;

    /**
     * The style preferences of the user.
     */
    @Column
    private String stylePreferences;

    /**
     * The favorite brands of the user.
     */
    @Column
    private String favoriteBrands;

    /**
     * Fashion inspirations for the user.
     */
    @Column
    private String fashionInspirations;

    /**
     * The profile image URL.
     */
    @Column
    private String profileImageUrl;

    /**
     * The visibility of the profile (public, private, friends only).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileVisibility visibility;

    /**
     * The wardrobes associated with this profile.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Wardrobe> wardrobes = new HashSet<>();

    /**
     * The outfits associated with this profile.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Outfit> outfits = new HashSet<>();

    /**
     * The posts created by this profile.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Post> posts = new HashSet<>();

    /**
     * The items associated with this profile.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    /**
     * The likes on this profile's content (posts, outfits, etc.).
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Like> likes = new HashSet<>();

    /**
     * Enum representing the visibility of the profile.
     */
    public enum ProfileVisibility {
        PUBLIC,
        PRIVATE,
        FRIENDS_ONLY
    }
}
