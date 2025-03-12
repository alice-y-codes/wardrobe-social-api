package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.FriendMapper;
import com.yalice.wardrobe_social_app.repositories.FriendRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl extends BaseService implements FriendService {

    private final FriendRepository friendRepository;
    private final UserSearchService userSearchService;
    private final FriendMapper friendMapper;

    @Override
    @Transactional
    public FriendRequestDto sendFriendRequest(Long senderId, Long recipientId) {
        validateSelfFriendRequest(senderId, recipientId);

        if (friendRepository.existsBySenderIdAndRecipientId(senderId, recipientId)) {
            throw new IllegalStateException("Friend request already exists");
        }

        User sender = userSearchService.getUserEntityById(senderId);
        User recipient = userSearchService.getUserEntityById(recipientId);

        Friendship friendship = Friendship.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendshipStatus.PENDING)
                .build();

        friendRepository.save(friendship);

        log.info("Friend request sent: {} -> {}", senderId, recipientId);
        return friendMapper.toRequestDto(friendship);
    }

    @Override
    @Transactional
    public FriendResponseDto acceptFriendRequest(Long userId, Long requestId) {
        Friendship friendship = findFriendshipById(requestId);
        validateFriendRequestRecipient(userId, friendship);

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendRepository.save(friendship);

        log.info("Friend request accepted: {}", requestId);
        return friendMapper.toResponseDto(friendship);
    }

    @Override
    @Transactional
    public void rejectFriendRequest(Long userId, Long requestId) {
        Friendship friendship = findFriendshipById(requestId);
        validateFriendRequestRecipient(userId, friendship);

        friendship.setStatus(FriendshipStatus.REJECTED);
        friendRepository.save(friendship);

        log.info("Friend request rejected: {}", requestId);
    }

    @Override
    public List<FriendRequestDto> getPendingFriendRequests(Long userId) {
        List<Friendship> pendingRequests = friendRepository.findByRecipientIdAndStatus(userId, FriendshipStatus.PENDING);
        log.info("Retrieved {} pending friend requests for user ID: {}", pendingRequests.size(), userId);
        return pendingRequests.stream().map(friendMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<FriendResponseDto> getFriends(Long userId) {
        List<Friendship> friendships = friendRepository.findAllByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED);
        log.info("Retrieved {} friends for user ID: {}", friendships.size(), userId);
        return friendships.stream().map(friendMapper::toResponseDto).collect(Collectors.toList());
    }

    @Override
    public boolean areFriends(Long userId1, Long userId2) {
        return friendRepository.findFriendshipBetweenUsers(userId1, userId2)
                .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElse(false);
    }

    private void validateSelfFriendRequest(Long senderId, Long recipientId) {
        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }
    }

    private void validateFriendRequestRecipient(Long userId, Friendship friendship) {
        if (!friendship.getRecipient().getId().equals(userId) || friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Invalid friend request action");
        }
    }

    private Friendship findFriendshipById(Long requestId) {
        return friendRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found with ID: " + requestId));
    }
}
