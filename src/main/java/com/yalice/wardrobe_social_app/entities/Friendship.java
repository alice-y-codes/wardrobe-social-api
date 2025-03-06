package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Represents a friendship connection between two users.
 * This entity captures the relationship between two users, including the status
 * of their friendship and timestamps for when the request was created and
 * updated.
 */
@Entity
@Table(name = "friendships")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Friendship extends BaseEntity {

    /**
     * The user who sent the friend request.
     * This field represents the sender in the friendship request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User sender;

    /**
     * The user who received the friend request.
     * This field represents the recipient in the friendship request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User recipient;

    /**
     * The status of the friendship.
     * Indicates whether the friendship is Pending, Accepted, or Rejected.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    /**
     * The timestamp when the friendship request was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the friendship status was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Sets the created and updated timestamps before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the timestamp for the last updated time before updating the entity.
     */
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
        REJECTED,
        BLOCKED
    }
}
