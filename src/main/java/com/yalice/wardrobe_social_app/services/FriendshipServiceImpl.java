package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.FriendshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserService userService;

    @Autowired
    public FriendshipServiceImpl(FriendshipRepository friendshipRepository, UserService userService) {
        this.friendshipRepository = friendshipRepository;
        this.userService = userService;
    }

    @Override
    public Friendship sendFriendRequest(Long requesterId, Long recipientId) {
        if (requesterId.equals(recipientId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        Optional<User> requesterOptional = userService.findById(requesterId);
        Optional<User> recipientOptional = userService.findById(recipientId);

        if (requesterOptional.isEmpty() || recipientOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User requester = requesterOptional.get();
        User recipient = recipientOptional.get();

        // Check if a friendship already exists
        Optional<Friendship> existingFriendship = friendshipRepository.findByRequesterAndRecipient(requester,
                recipient);
        if (existingFriendship.isPresent()) {
            throw new IllegalStateException("Friend request already exists");
        }

        // Check if the recipient has already sent a request to the requester
        Optional<Friendship> reverseRequest = friendshipRepository.findByRequesterAndRecipient(recipient, requester);
        if (reverseRequest.isPresent()) {
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
        Optional<Friendship> friendshipOptional = friendshipRepository.findById(requestId);
        if (friendshipOptional.isEmpty()) {
            throw new IllegalArgumentException("Friend request not found");
        }

        Friendship friendship = friendshipOptional.get();

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
        Optional<Friendship> friendshipOptional = friendshipRepository.findById(requestId);
        if (friendshipOptional.isEmpty()) {
            throw new IllegalArgumentException("Friend request not found");
        }

        Friendship friendship = friendshipOptional.get();

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
        Optional<User> userOptional = userService.findById(userId);
        Optional<User> friendOptional = userService.findById(friendId);

        if (userOptional.isEmpty() || friendOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        User friend = friendOptional.get();

        Optional<Friendship> friendshipOptional = friendshipRepository.findFriendshipBetweenUsers(user, friend);
        if (friendshipOptional.isEmpty() || friendshipOptional.get().getStatus() != FriendshipStatus.ACCEPTED) {
            throw new IllegalStateException("Friendship does not exist or is not accepted");
        }

        friendshipRepository.delete(friendshipOptional.get());
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
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
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
    public Optional<Friendship> getFriendshipBetweenUsers(Long userId1, Long userId2) {
        Optional<User> user1Optional = userService.findById(userId1);
        Optional<User> user2Optional = userService.findById(userId2);

        if (user1Optional.isEmpty() || user2Optional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user1 = user1Optional.get();
        User user2 = user2Optional.get();

        return friendshipRepository.findFriendshipBetweenUsers(user1, user2);
    }
}