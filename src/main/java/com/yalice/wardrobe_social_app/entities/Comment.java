package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a comment on a post in the wardrobe social app.
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Post post;

    /** The profile who made the comment. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    /** The content of the comment. */
    @Column(nullable = false, length = 500)
    private String content;
}
