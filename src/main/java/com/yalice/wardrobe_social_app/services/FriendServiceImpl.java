package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.FriendRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl extends BaseService implements FriendService {

    private final FriendRepository friendRepository;
    private final UserSearchService userSearchService;
    private final DtoConversionService dtoConversionService;

    @Autowired
    public FriendServiceImpl(FriendRepository friendRepository, UserSearchService userSearchService, DtoConversionService dtoConversionService) {
        this.friendRepository = friendRepository;
        this.userSearchService = userSearchService;
        this.dtoConversionService = dtoConversionService;
    }

    @Override
    @Transactional
    public FriendRequestDto sendFriendRequest(Long senderId, Long recipientId) {
        logger.info("Attempting to send friend request from user ID: {} to user ID: {}", senderId, recipientId);

        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        // Check if friendship already exists
        if (friendRepository.existsBySenderIdAndRecipientId(senderId, recipientId)) {
            throw new IllegalStateException("Friend request already exists");
        }

        User sender = userSearchService.getUserEntityById(senderId);
        User recipient = userSearchService.getUserEntityById(recipientId);

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);

        Friendship savedFriendship = friendRepository.save(friendship);
        logger.info("Friend request sent successfully from user ID: {} to user ID: {}", senderId, recipientId);

        return convertToFriendRequestDto(savedFriendship);
    }

    @Override
    @Transactional
    public FriendResponseDto acceptFriendRequest(Long userId, Long requestId) {
        logger.info("Attempting to accept friend request with ID: {} by user ID: {}", requestId, userId);

        Friendship friendship = friendRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found with ID: " + requestId));

        if (!friendship.getRecipient().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not the recipient of this friend request");
        }

        if (friendship.getStatus()!=FriendshipStatus.PENDING) {
            throw new IllegalStateException("Friend request is not in PENDING status");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        Friendship updatedFriendship = friendRepository.save(friendship);
        logger.info("Friend request accepted successfully with ID: {}", requestId);

        return dtoConversionService.convertToFriendshipResponseDto(updatedFriendship);
    }

    @Override
    @Transactional
    public void rejectFriendRequest(Long userId, Long requestId) {
        logger.info("Attempting to reject friend request with ID: {} by user ID: {}", requestId, userId);

        Friendship friendship = friendRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found with ID: " + requestId));

        if (!friendship.getRecipient().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not the recipient of this friend request");
        }

        if (friendship.getStatus()!=FriendshipStatus.PENDING) {
            throw new IllegalStateException("Friend request is not in PENDING status");
        }

        friendship.setStatus(FriendshipStatus.REJECTED);
        friendRepository.save(friendship);
        logger.info("Friend request rejected successfully with ID: {}", requestId);
    }

    @Override
    public List<FriendRequestDto> getPendingFriendRequests(Long userId) {
        logger.info("Retrieving pending friend requests for user ID: {}", userId);

        List<Friendship> pendingRequests = friendRepository.findByRecipientIdAndStatus(userId,
                FriendshipStatus.PENDING);
        logger.info("Found {} pending friend requests for user ID: {}", pendingRequests.size(), userId);

        return pendingRequests.stream()
                .map(this::convertToFriendRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendResponseDto> getFriends(Long userId) {
        logger.info("Retrieving friends for user ID: {}", userId);

        List<Friendship> friendships = friendRepository.findAllByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED);
        logger.info("Found {} friends for user ID: {}", friendships.size(), userId);

        return friendships.stream()
                .map(dtoConversionService::convertToFriendshipResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean areFriends(Long userId1, Long userId2) {
        logger.info("Checking if users {} and {} are friends", userId1, userId2);

        Optional<Friendship> friendship = friendRepository.findFriendshipBetweenUsers(userId1, userId2);
        boolean areFriends = friendship.isPresent() && friendship.get().getStatus()==FriendshipStatus.ACCEPTED;

        logger.info("Users {} and {} are{} friends", userId1, userId2, areFriends ? "":" not");
        return areFriends;
    }

    private FriendRequestDto convertToFriendRequestDto(Friendship friendship) {
        return FriendRequestDto.builder()
                .id(friendship.getId())
                .senderId(friendship.getSender().getId())
                .senderUsername(friendship.getSender().getUsername())
                .recipientId(friendship.getRecipient().getId())
                .recipientUsername(friendship.getRecipient().getUsername())
                .status(friendship.getStatus().name())
                .createdAt(friendship.getCreatedAt())
                .build();
    }


}
