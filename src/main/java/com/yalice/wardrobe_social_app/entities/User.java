package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a user in the wardrobe social application.
 * This class contains user information, authentication details, and
 * relationships with other entities such as items, outfits, and social connections.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    /** Unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Username of the user. */
    @Column
    private String username;

    /** Email address of the user. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Encrypted password of the user. */
    @Column(nullable = false)
    private String password;

    /** Authentication provider used by the user. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    /** URL to the user's profile picture. */
    @Column
    private String profilePicture;

    /** User's profile information. */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Profile profile; // Profile now manages the wardrobe, outfits, and items

    /** List of friend requests sent by the user. */
    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Friendship> sentFriendRequests = new ArrayList<>();

    /** List of friend requests received by the user. */
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Friendship> receivedFriendRequests = new ArrayList<>();

    /** List of posts created by the user. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    /** List of comments made by the user. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    /** List of likes given by the user. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    /**
     * Enumeration of supported authentication providers.
     */
    public enum Provider {
        /** Facebook authentication. */
        FACEBOOK,
        /** Google authentication. */
        GOOGLE,
        /** Apple authentication. */
        APPLE,
        /** Local authentication. */
        LOCAL
    }
}
