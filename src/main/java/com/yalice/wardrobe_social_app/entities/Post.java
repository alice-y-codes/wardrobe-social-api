package com.yalice.wardrobe_social_app.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a post created by a user, which may include an outfit and user
 * interactions such as likes and comments.
 */
@Entity
@Table(name = "posts")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Post extends BaseEntity {

    /**
     * The profile (user) associated with this post.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Profile profile;

    /**
     * The title of the post.
     */
    @Column(nullable = false)
    private String title;

    /**
     * The content of the post.
     */
    @Column(length = 2000)
    private String content;

    /**
     * The outfit associated with this post.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outfit_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Outfit outfit;

    /**
     * The visibility status of the post (e.g., Public, Private).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostVisibility visibility;

    /**
     * The URL of the featured image for the post.
     */
    private String featureImage;

    /**
     * The number of likes this post has received.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    /**
     * The set of users who liked the post.
     */
    @ManyToMany
    @JoinTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private Set<User> likes = new HashSet<>();

    /**
     * The list of comments associated with this post.
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    /**
     * Adds a like from a user to the post.
     *
     * @param user the user who liked the post
     */
    public void addLike(User user) {
        likes.add(user);
    }

    /**
     * Removes a like from a user from the post.
     *
     * @param user the user whose like is being removed
     */
    public void removeLike(User user) {
        likes.remove(user);
    }

    /**
     *
     * @return likes count
     */
    public int getLikesCount() {
        return likes.size();
    }
    /**
     * Adds a comment to the post.
     *
     * @param comment the comment to add
     */
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    /**
     * Removes a comment from the post.
     *
     * @param comment the comment to remove
     */
    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    /**
     *
     * @return comment count
     */
    public int getCommentsCount() {
        return comments.size();
    }

    /**
     * Enum representing the visibility of the post.
     */
    public enum PostVisibility {
        PUBLIC,
        PRIVATE,
        FRIENDS_ONLY
    }
}
