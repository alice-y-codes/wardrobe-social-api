package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.FriendshipRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendshipServiceImpl extends BaseService implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserSearchService userSearchService;

    @Autowired
    public FriendshipServiceImpl(FriendshipRepository friendshipRepository, UserSearchService userSearchService) {
        this.friendshipRepository = friendshipRepository;
        this.userSearchService = userSearchService;
    }

    @Override
    public Friendship sendFriendRequest(Long requesterId, Long recipientId) {
        if (requesterId.equals(recipientId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        // Fetch users using the correct service method
        User requester = userSearchService.getUserEntityById(requesterId);
        User recipient = userSearchService.getUserEntityById(recipientId);

        // Check if a friendship already exists
        if (friendshipRepository.existsByRequesterAndRecipient(requester, recipient)) {
            throw new IllegalStateException("Friend request already exists");
        }

        // Check if the recipient has already sent a request to the requester
        if (friendshipRepository.existsByRequesterAndRecipient(recipient, requester)) {
            throw new IllegalStateException("There is already a pending request from the recipient");
        }

        Friendship friendship = Friendship.builder()
                .requester(requester)
                .recipient(recipient)
                .status(FriendshipStatus.PENDING)
                .build();

        return friendshipRepository.save(friendship);
    }

    @Override
    public Friendship acceptFriendRequest(Long requestId, Long userId) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        // Verify that the user is the recipient of the request
        if (!friendship.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the recipient can accept the friend request");
        }

        // Verify that the request is pending
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    @Override
    public void rejectFriendRequest(Long requestId, Long userId) {
        Friendship friendship = friendshipRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        // Verify that the user is the recipient of the request
        if (!friendship.getRecipient().getId().equals(userId)) {
            throw new IllegalArgumentException("Only the recipient can reject the friend request");
        }

        // Verify that the request is pending
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Friend request is not pending");
        }

        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = userSearchService.getUserEntityById(userId);
        User friend = userSearchService.getUserEntityById(friendId);

        Friendship friendship = friendshipRepository.findFriendshipBetweenUsers(user, friend)
                .orElseThrow(() -> new IllegalStateException("Friendship does not exist or is not accepted"));

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new IllegalStateException("Friendship is not accepted");
        }

        friendshipRepository.delete(friendship);
    }

    @Override
    public List<User> getFriends(Long userId) {
        List<User> friends = new ArrayList<>();

        // Get friends where the user is the requester
        friends.addAll(friendshipRepository.findFriendsWhoAcceptedRequestFromUser(userId));

        // Get friends where the user is the recipient
        friends.addAll(friendshipRepository.findFriendsWhoSentAcceptedRequestToUser(userId));

        return friends;
    }

    @Override
    public List<Friendship> getPendingFriendRequests(Long userId) {
        User user = userSearchService.getUserEntityById(userId);
        return friendshipRepository.findByRecipientAndStatus(user, FriendshipStatus.PENDING);
    }

    @Override
    public boolean areFriends(Long userId1, Long userId2) {
        // Get all accepted friendships for both users
        List<Friendship> user1Friendships = friendshipRepository.findAcceptedFriendshipsForUser(userId1);
        List<Friendship> user2Friendships = friendshipRepository.findAcceptedFriendshipsForUser(userId2);

        // Check if there's a common friendship
        for (Friendship f1 : user1Friendships) {
            for (Friendship f2 : user2Friendships) {
                if (f1.getId().equals(f2.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Friendship getFriendshipBetweenUsers(Long userId1, Long userId2) {
        User user1 = userSearchService.getUserEntityById(userId1);
        User user2 = userSearchService.getUserEntityById(userId2);

        return friendshipRepository.findFriendshipBetweenUsers(user1, user2)
                .orElseThrow(() -> new IllegalArgumentException("Friendship not found"));
    }
}
