package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents a comment on a post in the wardrobe social app.
 * The comment is linked to a post and a user profile, with a content field
 * representing the text of the comment.
 */
@Entity
@Table(name = "comments")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Comment extends BaseEntity {

    /**
     * The post that this comment is associated with.
     * This field represents the relationship between the comment and a post.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Post post;

    /**
     * The profile of the user who made the comment.
     * This field represents the relationship between the comment and a user
     * profile.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    /**
     * The content of the comment.
     * This field contains the actual text of the comment made by the user.
     */
    @Column(nullable = false, length = 1000)
    private String content;
}
