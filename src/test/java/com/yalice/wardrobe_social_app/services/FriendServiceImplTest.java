package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.FriendMapper;
import com.yalice.wardrobe_social_app.repositories.FriendRepository;
import com.yalice.wardrobe_social_app.services.social.FriendServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendServiceImplTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserSearchService userSearchService;

    @Mock
    private FriendMapper friendMapper;

    @InjectMocks
    private FriendServiceImpl friendService;

    private User sender;
    private User recipient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Ensure proper mock initialization

        sender = createUser(1L, "senderUsername");
        recipient = createUser(2L, "recipientUsername");
    }

    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private Friendship createFriendship(User sender, User recipient, FriendshipStatus status) {
        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(status);
        return friendship;
    }

    @Test
    void sendFriendRequest_success() {
        FriendRequestDto mockFriendRequestDto = FriendRequestDto.builder()
                        .id(1L)
                        .recipientId(2L)
                        .senderId(1L)
                        .status("PENDING")
                        .build();

        when(friendRepository.existsBySenderIdAndRecipientId(sender.getId(), recipient.getId())).thenReturn(false);
        when(userSearchService.getUserEntityById(sender.getId())).thenReturn(sender);
        when(userSearchService.getUserEntityById(recipient.getId())).thenReturn(recipient);

        Friendship friendship = createFriendship(sender, recipient, FriendshipStatus.PENDING);
        friendship.setId(100L);

        when(friendRepository.save(any(Friendship.class))).thenReturn(friendship);
        when(friendMapper.toRequestDto(any(Friendship.class))).thenReturn(mockFriendRequestDto);

        FriendRequestDto friendRequestDto = friendService.sendFriendRequest(sender.getId(), recipient.getId());

        assertNotNull(friendRequestDto);
        assertEquals(sender.getId(), friendRequestDto.getSenderId());
        assertEquals(recipient.getId(), friendRequestDto.getRecipientId());
        assertEquals(FriendshipStatus.PENDING.name(), friendRequestDto.getStatus());
    }

    @Test
    void sendFriendRequest_alreadyExists() {
        when(friendRepository.existsBySenderIdAndRecipientId(sender.getId(), recipient.getId())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> friendService.sendFriendRequest(sender.getId(), recipient.getId()));
        assertEquals("Friend request already exists", exception.getMessage());
    }

    @Test
    void acceptFriendRequest_success() {
        Long requestId = 1L;
        Long userId = recipient.getId(); // recipient accepts the request.

        Friendship friendship = createFriendship(sender, recipient, FriendshipStatus.PENDING);
        friendship.setId(requestId);

        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));
        when(friendRepository.save(any(Friendship.class))).thenReturn(friendship);

        FriendResponseDto mockResponseDto = new FriendResponseDto();
        mockResponseDto.setStatus(FriendshipStatus.ACCEPTED.name());
        mockResponseDto.setUserId(sender.getId());

        // Use FriendMapper instead of DtoConversionService
        when(friendMapper.toResponseDto(any(Friendship.class))).thenReturn(mockResponseDto);

        FriendResponseDto friendResponseDto = friendService.acceptFriendRequest(userId, requestId);

        assertNotNull(friendResponseDto);
        assertEquals(FriendshipStatus.ACCEPTED.name(), friendResponseDto.getStatus());
        assertEquals(sender.getId(), friendResponseDto.getUserId());

        verify(friendRepository, times(1)).findById(requestId);
        verify(friendRepository, times(1)).save(friendship);

        assertEquals(FriendshipStatus.ACCEPTED, friendship.getStatus());
    }

    @Test
    void acceptFriendRequest_notRecipient() {
        Long requestId = 1L;
        Friendship friendship = createFriendship(sender, recipient, FriendshipStatus.PENDING);
        friendship.setId(requestId);

        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            friendService.acceptFriendRequest(3L, requestId); // wrong user
        });
        assertEquals("Invalid friend request action", exception.getMessage());
    }

    @Test
    void rejectFriendRequest_success() {
        Long requestId = 1L;
        Friendship friendship = createFriendship(sender, recipient, FriendshipStatus.PENDING);
        friendship.setId(requestId);

        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        friendService.rejectFriendRequest(recipient.getId(), requestId);

        assertEquals(FriendshipStatus.REJECTED, friendship.getStatus());
        verify(friendRepository, times(1)).save(friendship);
    }

    @Test
    void rejectFriendRequest_notRecipient() {
        Long requestId = 1L;
        Friendship friendship = createFriendship(sender, recipient, FriendshipStatus.PENDING);
        friendship.setId(requestId);

        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            friendService.rejectFriendRequest(3L, requestId); // wrong user
        });
        assertEquals("Invalid friend request action", exception.getMessage());
    }

    @Test
    void getPendingFriendRequests_success() {
        Friendship pendingFriendship = createFriendship(sender, recipient, FriendshipStatus.PENDING);
        pendingFriendship.setId(99L);

        when(friendRepository.findByRecipientIdAndStatus(recipient.getId(), FriendshipStatus.PENDING))
                .thenReturn(List.of(pendingFriendship));

        var pendingRequests = friendService.getPendingFriendRequests(recipient.getId());

        assertNotNull(pendingRequests);
        assertFalse(pendingRequests.isEmpty());
    }

    @Test
    void areFriends_true() {
        Friendship friendship = createFriendship(sender, recipient, FriendshipStatus.ACCEPTED);
        friendship.setId(100L);

        when(friendRepository.findFriendshipBetweenUsers(sender.getId(), recipient.getId()))
                .thenReturn(Optional.of(friendship));

        boolean areFriends = friendService.areFriends(sender.getId(), recipient.getId());

        assertTrue(areFriends);
    }

    @Test
    void areFriends_false() {
        when(friendRepository.findFriendshipBetweenUsers(sender.getId(), recipient.getId()))
                .thenReturn(Optional.empty());

        boolean areFriends = friendService.areFriends(sender.getId(), recipient.getId());

        assertFalse(areFriends);
    }
}
