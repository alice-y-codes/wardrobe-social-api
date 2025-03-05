package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.User;

import java.util.List;

public interface FriendshipService {
    Friendship sendFriendRequest(Long requesterId, Long recipientId);

    Friendship acceptFriendRequest(Long requestId, Long userId);

    void rejectFriendRequest(Long requestId, Long userId);

    void removeFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<Friendship> getPendingFriendRequests(Long userId);

    boolean areFriends(Long userId1, Long userId2);

    Friendship getFriendshipBetweenUsers(Long userId1, Long userId2);
}
