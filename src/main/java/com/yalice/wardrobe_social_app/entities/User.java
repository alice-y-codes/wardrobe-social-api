package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user in the wardrobe social application, storing user
 * authentication details
 * and relationships with other entities such as profile, friend requests, and
 * likes.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    /**
     * The username of the user. This field must be unique.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * The email address of the user. This field must be unique.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The encrypted password of the user.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The authentication provider used by the user (e.g., Facebook, Google, Apple,
     * or Local).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    /**
     * The profile associated with the user, which manages the wardrobe, outfits,
     * and items.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    /**
     * The friend requests sent by the user.
     */
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Friendship> sentFriendRequests = new HashSet<>();

    /**
     * The friend requests received by the user.
     */
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Friendship> receivedFriendRequests = new HashSet<>();

    /**
     * Enumeration of supported authentication providers.
     */
    public enum Provider {
        FACEBOOK,
        GOOGLE,
        APPLE,
        LOCAL
    }
}
