package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents a like on a post by a user.
 */
@Entity
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "post_id", "profile_id" }) // Prevents duplicate likes
})
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Like extends BaseEntity {

    /**
     * The post that was liked.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Post post;

    /**
     * The profile that created the like.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false) // Profile instead of User
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;
}
