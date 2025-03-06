package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user in the wardrobe social application.
 * Stores user authentication details and relationships with other entities.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    /** Unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Username of the user (must be unique). */
    @Column(nullable = false, unique = true)
    private String username;

    /** Email address of the user (must be unique). */
    @Column(nullable = false, unique = true)
    private String email;

    /** Encrypted password of the user. */
    @Column(nullable = false)
    private String password;

    /** Authentication provider used by the user. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    /** User's profile (manages wardrobe, outfits, and items). */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    /** Friend requests sent by the user. */
    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Friendship> sentFriendRequests = new ArrayList<>();

    /** Friend requests received by the user. */
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Friendship> receivedFriendRequests = new ArrayList<>();

    /** List of likes given by the user. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Like> likes = new ArrayList<>();

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
