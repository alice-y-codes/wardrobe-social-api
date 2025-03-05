package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a like on a post by a profile (user).
 */
@Entity
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "post_id", "profile_id" })  // Prevents duplicate likes
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The post that is liked. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Post post;

    /** The profile (user) who liked the post. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)  // Changed from user_id to profile_id
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;  // Changed from user to profile

    /** Timestamp when the like was created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
