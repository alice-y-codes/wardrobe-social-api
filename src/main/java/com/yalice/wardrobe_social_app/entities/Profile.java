package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity {

    /** Reference to the associated user */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Bio of the profile. */
    @Column(length = 500)
    private String bio;

    /** Location of the user. */
    @Column
    private String location;

    /** Style preferences of the user. */
    @Column
    private String stylePreferences;

    /** Favorite brands of the user. */
    @Column
    private String favoriteBrands;

    /** Fashion inspirations for the user. */
    @Column
    private String fashionInspirations;

    /** Profile image URL (now in the Profile entity). */
    @Column
    private String profileImageUrl;

    /** Visibility of the profile (public, private, friends only). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileVisibility visibility;

    /** Wardrobe associated with the profile. */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Wardrobe> wardrobes = new HashSet<>();

    /** Outfits associated with the profile. */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Outfit> outfits = new HashSet<>();

    /** Posts created by the profile. */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Post> posts = new HashSet<>();

    /** Items associated with the profile. */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    public enum ProfileVisibility {
        PUBLIC,
        PRIVATE,
        FRIENDS_ONLY
    }
}
