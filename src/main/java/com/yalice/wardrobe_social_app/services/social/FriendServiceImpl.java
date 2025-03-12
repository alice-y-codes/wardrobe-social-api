package com.yalice.wardrobe_social_app.services.social;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.FriendMapper;
import com.yalice.wardrobe_social_app.repositories.FriendRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendServiceImpl extends BaseService<Friendship, Long> implements FriendService {

    private final FriendRepository friendRepository;
    private final UserSearchService userSearchService;
    private final FriendMapper friendMapper;

    public FriendServiceImpl(
            FriendRepository friendRepository,
            UserSearchService userSearchService,
            FriendMapper friendMapper) {
        this.friendRepository = friendRepository;
        this.userSearchService = userSearchService;
        this.friendMapper = friendMapper;
    }

    @Override
    protected JpaRepository<Friendship, Long> getRepository() {
        return friendRepository;
    }

    @Override
    protected String getEntityName() {
        return "Friendship";
    }

    @Override
    @Transactional
    public FriendRequestDto sendFriendRequest(Long senderId, Long recipientId) {
        logger.info("Sending friend request from {} to {}", senderId, recipientId);

        validationService.validateNotNull(senderId, "Sender ID");
        validationService.validateNotNull(recipientId, "Recipient ID");
        validateSelfFriendRequest(senderId, recipientId);

        validationService.validateExists(!friendRepository.existsBySenderIdAndRecipientId(senderId, recipientId),
                "Friend request already exists");

        User sender = userSearchService.getUserEntityById(senderId);
        User recipient = userSearchService.getUserEntityById(recipientId);

        validationService.validateNotNull(sender, "Sender");
        validationService.validateNotNull(recipient, "Recipient");

        Friendship friendship = Friendship.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendshipStatus.PENDING)
                .build();

        return mapEntity(save(friendship), friendMapper::toRequestDto);
    }

    @Override
    @Transactional
    public FriendResponseDto acceptFriendRequest(Long userId, Long requestId) {
        logger.info("Accepting friend request {} by user {}", requestId, userId);

        validationService.validateNotNull(userId, "User ID");
        validationService.validateNotNull(requestId, "Request ID");

        Friendship friendship = findById(requestId);
        validateFriendRequestRecipient(userId, friendship);

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return mapEntity(save(friendship), friendMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void rejectFriendRequest(Long userId, Long requestId) {
        logger.info("Rejecting friend request {} by user {}", requestId, userId);

        validationService.validateNotNull(userId, "User ID");
        validationService.validateNotNull(requestId, "Request ID");

        Friendship friendship = findById(requestId);
        validateFriendRequestRecipient(userId, friendship);

        friendship.setStatus(FriendshipStatus.REJECTED);
        save(friendship);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getPendingFriendRequests(Long userId) {
        logger.info("Getting pending friend requests for user {}", userId);

        validationService.validateNotNull(userId, "User ID");
        return mapEntityList(
                friendRepository.findByRecipientIdAndStatus(userId, FriendshipStatus.PENDING),
                friendMapper::toRequestDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponseDto> getFriends(Long userId) {
        logger.info("Getting friends for user {}", userId);

        validationService.validateNotNull(userId, "User ID");
        return mapEntityList(
                friendRepository.findAllByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED),
                friendMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areFriends(Long userId1, Long userId2) {
        logger.info("Checking friendship between users {} and {}", userId1, userId2);

        validationService.validateNotNull(userId1, "User ID 1");
        validationService.validateNotNull(userId2, "User ID 2");

        return friendRepository.findFriendshipBetweenUsers(userId1, userId2)
                .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElse(false);
    }

    private void validateSelfFriendRequest(Long senderId, Long recipientId) {
        validationService.validateExists(!senderId.equals(recipientId),
                "Cannot send friend request to yourself");
    }

    private void validateFriendRequestRecipient(Long userId, Friendship friendship) {
        validationService.validateExists(
                friendship.getRecipient().getId().equals(userId) &&
                        friendship.getStatus() == FriendshipStatus.PENDING,
                "Invalid friend request action");
    }
}
