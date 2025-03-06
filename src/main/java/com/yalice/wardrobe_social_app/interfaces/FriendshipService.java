package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendshipResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import java.util.List;

/**
 * Service interface for managing friendships and friend requests.
 */
public interface FriendshipService {
    /**
     * Sends a friend request to another user.
     *
     * @param senderId    the ID of the user sending the request
     * @param recipientId the ID of the user receiving the request
     * @return the created friend request
     */
    FriendRequestDto sendFriendRequest(Long senderId, Long recipientId);

    /**
     * Accepts a friend request.
     *
     * @param userId    the ID of the user accepting the request
     * @param requestId the ID of the friend request to accept
     * @return the created friendship
     */
    FriendshipResponseDto acceptFriendRequest(Long userId, Long requestId);

    /**
     * Rejects a friend request.
     *
     * @param userId    the ID of the user rejecting the request
     * @param requestId the ID of the friend request to reject
     */
    void rejectFriendRequest(Long userId, Long requestId);

    /**
     * Gets all pending friend requests for a user.
     *
     * @param userId the ID of the user
     * @return the list of pending friend requests
     */
    List<FriendRequestDto> getPendingFriendRequests(Long userId);

    /**
     * Gets all friends of a user.
     *
     * @param userId the ID of the user
     * @return the list of friendships
     */
    List<FriendshipResponseDto> getFriends(Long userId);

    /**
     * Checks if two users are friends.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @return true if the users are friends, false otherwise
     */
    boolean areFriends(Long userId1, Long userId2);
}
