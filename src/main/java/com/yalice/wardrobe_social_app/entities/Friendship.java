package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a friendship connection between two users.
 */
@Entity
@Table(name = "friendships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who sent the friend request. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User requester;

    /** The user who received the friend request. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User recipient;

    /** Status of the friendship (Pending, Accepted, Rejected). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FriendshipStatus status = FriendshipStatus.PENDING;

    /** Timestamp when the friendship request was created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp when the friendship status was last updated. */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Enum representing the status of a friendship.
     */
    public enum FriendshipStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}
