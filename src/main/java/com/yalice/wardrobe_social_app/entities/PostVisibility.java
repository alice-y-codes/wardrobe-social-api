package com.yalice.wardrobe_social_app.entities;

/**
 * Enum representing the visibility options for posts in the social feed.
 */
public enum PostVisibility {
    /**
     * Visible to everyone
     */
    PUBLIC,

    /**
     * Visible only to the post owner
     */
    PRIVATE,

    /**
     * Visible to the post owner and their friends
     */
    FRIENDS_ONLY
}