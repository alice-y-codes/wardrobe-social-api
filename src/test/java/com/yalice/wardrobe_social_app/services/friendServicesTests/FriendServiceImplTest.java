package com.yalice.wardrobe_social_app.services.friendServicesTests;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.FriendRepository;
import com.yalice.wardrobe_social_app.services.FriendServiceImpl;
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

    @InjectMocks
    private FriendServiceImpl friendService;

    private User sender;
    private User recipient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = new User();
        sender.setId(1L);
        sender.setUsername("senderUsername");

        recipient = new User();
        recipient.setId(2L);
        recipient.setUsername("recipientUsername");
    }

    @Test
    void sendFriendRequest_success() {
        // Given
        when(friendRepository.existsBySenderIdAndRecipientId(sender.getId(), recipient.getId())).thenReturn(false);
        when(userSearchService.getUserEntityById(sender.getId())).thenReturn(sender);
        when(userSearchService.getUserEntityById(recipient.getId())).thenReturn(recipient);
        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);

        when(friendRepository.save(any(Friendship.class))).thenReturn(friendship);

        // When
        FriendRequestDto friendRequestDto = friendService.sendFriendRequest(sender.getId(), recipient.getId());

        // Then
        assertNotNull(friendRequestDto);
        assertEquals(sender.getId(), friendRequestDto.getSenderId());
        assertEquals(recipient.getId(), friendRequestDto.getRecipientId());
        assertEquals(FriendshipStatus.PENDING.name(), friendRequestDto.getStatus());
    }

    @Test
    void sendFriendRequest_alreadyExists() {
        // Given
        when(friendRepository.existsBySenderIdAndRecipientId(sender.getId(), recipient.getId())).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            friendService.sendFriendRequest(sender.getId(), recipient.getId());
        });
        assertEquals("Friend request already exists", exception.getMessage());
    }

    @Test
    void acceptFriendRequest_success() {
        // Given
        Long requestId = 1L;
        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        // When
        FriendResponseDto friendResponseDto = friendService.acceptFriendRequest(recipient.getId(), requestId);

        // Then
        assertNotNull(friendResponseDto);
        assertEquals(FriendshipStatus.ACCEPTED.name(), friendResponseDto.getStatus());
        assertEquals(sender.getId(), friendResponseDto.getUserId());
    }

    @Test
    void acceptFriendRequest_notRecipient() {
        // Given
        Long requestId = 1L;
        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            friendService.acceptFriendRequest(3L, requestId); // wrong userId
        });
        assertEquals("User is not the recipient of this friend request", exception.getMessage());
    }

    @Test
    void rejectFriendRequest_success() {
        // Given
        Long requestId = 1L;
        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        // When
        friendService.rejectFriendRequest(recipient.getId(), requestId);

        // Then
        assertEquals(FriendshipStatus.REJECTED, friendship.getStatus());
        verify(friendRepository, times(1)).save(friendship);
    }

    @Test
    void rejectFriendRequest_notRecipient() {
        // Given
        Long requestId = 1L;
        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            friendService.rejectFriendRequest(3L, requestId); // wrong userId
        });
        assertEquals("User is not the recipient of this friend request", exception.getMessage());
    }

    @Test
    void getPendingFriendRequests_success() {
        // Given
        when(friendRepository.findByRecipientIdAndStatus(recipient.getId(), FriendshipStatus.PENDING))
                .thenReturn(List.of(new Friendship()));

        // When
        var pendingRequests = friendService.getPendingFriendRequests(recipient.getId());

        // Then
        assertNotNull(pendingRequests);
        assertFalse(pendingRequests.isEmpty());
    }

    @Test
    void areFriends_true() {
        // Given
        when(friendRepository.findFriendshipBetweenUsers(sender.getId(), recipient.getId()))
                .thenReturn(Optional.of(new Friendship()));

        // When
        boolean areFriends = friendService.areFriends(sender.getId(), recipient.getId());

        // Then
        assertTrue(areFriends);
    }

    @Test
    void areFriends_false() {
        // Given
        when(friendRepository.findFriendshipBetweenUsers(sender.getId(), recipient.getId()))
                .thenReturn(Optional.empty());

        // When
        boolean areFriends = friendService.areFriends(sender.getId(), recipient.getId());

        // Then
        assertFalse(areFriends);
    }
}
