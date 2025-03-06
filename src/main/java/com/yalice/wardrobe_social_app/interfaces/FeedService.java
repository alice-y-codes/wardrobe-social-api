package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemDto;
import com.yalice.wardrobe_social_app.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing feed-related operations.
 */
public interface FeedService {
    /**
     * Gets the user's feed with pagination.
     *
     * @param userId the ID of the user
     * @param page   the page number (zero-based)
     * @param size   the number of items per page
     * @return the list of feed items
     */
    List<FeedItemDto> getFeed(Long userId, int page, int size);

    /**
     * Gets the user's feed filtered by season.
     *
     * @param userId the ID of the user
     * @param season the season to filter by
     * @param page   the page number (zero-based)
     * @param size   the number of items per page
     * @return the list of feed items
     */
    List<FeedItemDto> getFeedBySeason(Long userId, String season, int page, int size);

    /**
     * Gets the user's feed filtered by category.
     *
     * @param userId   the ID of the user
     * @param category the category to filter by
     * @param page     the page number (zero-based)
     * @param size     the number of items per page
     * @return the list of feed items
     */
    List<FeedItemDto> getFeedByCategory(Long userId, String category, int page, int size);

    /**
     * Gets posts for a specific user.
     *
     * @param userId   the ID of the user whose posts to retrieve
     * @param viewerId the ID of the user viewing the posts
     * @param pageable the pagination information
     * @return the page of posts
     */
    Page<Post> getUserPosts(Long userId, Long viewerId, Pageable pageable);
}