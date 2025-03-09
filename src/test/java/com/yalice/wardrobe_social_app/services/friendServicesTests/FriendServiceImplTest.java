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
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
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
    private DtoConversionService dtoConversionService;

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
        when(friendRepository.existsBySenderIdAndRecipientId(sender.getId(), recipient.getId())).thenReturn(false);
        when(userSearchService.getUserEntityById(sender.getId())).thenReturn(sender);
        when(userSearchService.getUserEntityById(recipient.getId())).thenReturn(recipient);

        Friendship friendship = new Friendship();
        friendship.setId(100L);
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);

        when(friendRepository.save(any(Friendship.class))).thenReturn(friendship);

        FriendRequestDto friendRequestDto = friendService.sendFriendRequest(sender.getId(), recipient.getId());

        assertNotNull(friendRequestDto);
        assertEquals(sender.getId(), friendRequestDto.getSenderId());
        assertEquals(recipient.getId(), friendRequestDto.getRecipientId());
        assertEquals(FriendshipStatus.PENDING.name(), friendRequestDto.getStatus());
    }

    @Test
    void sendFriendRequest_alreadyExists() {
        when(friendRepository.existsBySenderIdAndRecipientId(sender.getId(), recipient.getId())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            friendService.sendFriendRequest(sender.getId(), recipient.getId());
        });
        assertEquals("Friend request already exists", exception.getMessage());
    }

    @Test
    void acceptFriendRequest_success() {
        Long requestId = 1L;
        Long userId = recipient.getId(); // Assuming recipient is the one accepting the request.

        // Create a valid Friendship object with PENDING status
        Friendship friendship = new Friendship();
        friendship.setId(requestId);
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING); // Ensure status is PENDING

        // Mock the repository to return the Friendship with PENDING status
        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));
        when(friendRepository.save(any(Friendship.class))).thenReturn(friendship);

        // Prepare the response DTO
        FriendResponseDto mockResponseDto = new FriendResponseDto();
        mockResponseDto.setStatus(FriendshipStatus.ACCEPTED.name());
        mockResponseDto.setUserId(sender.getId());

        // Mock the DTO conversion service to return the expected FriendResponseDto
        when(dtoConversionService.convertToFriendshipResponseDto(any(Friendship.class))).thenReturn(mockResponseDto);

        // Call the method under test
        FriendResponseDto friendResponseDto = friendService.acceptFriendRequest(userId, requestId);

        // Assert the result
        assertNotNull(friendResponseDto, "FriendResponseDto should not be null");
        assertEquals(FriendshipStatus.ACCEPTED.name(), friendResponseDto.getStatus(), "The status should be ACCEPTED");
        assertEquals(sender.getId(), friendResponseDto.getUserId(), "The user ID should match the sender's ID");

        // Verify interactions with the friendRepository
        verify(friendRepository, times(1)).findById(requestId); // Verify findById was called once
        verify(friendRepository, times(1)).save(friendship); // Verify save was called once

        // Verify the status change to ACCEPTED
        assertEquals(FriendshipStatus.ACCEPTED, friendship.getStatus(), "The friendship status should be ACCEPTED");
    }


    @Test
    void acceptFriendRequest_notRecipient() {
        Long requestId = 1L;
        Friendship friendship = new Friendship();
        friendship.setId(requestId);
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);

        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            friendService.acceptFriendRequest(3L, requestId);
        });
        assertEquals("User is not the recipient of this friend request", exception.getMessage());
    }

    @Test
    void rejectFriendRequest_success() {
        Long requestId = 1L;
        Friendship friendship = new Friendship();
        friendship.setId(requestId);
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);

        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        friendService.rejectFriendRequest(recipient.getId(), requestId);

        assertEquals(FriendshipStatus.REJECTED, friendship.getStatus());
        verify(friendRepository, times(1)).save(friendship);
    }

    @Test
    void rejectFriendRequest_notRecipient() {
        Long requestId = 1L;
        Friendship friendship = new Friendship();
        friendship.setId(requestId);
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);

        when(friendRepository.findById(requestId)).thenReturn(Optional.of(friendship));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            friendService.rejectFriendRequest(3L, requestId);
        });
        assertEquals("User is not the recipient of this friend request", exception.getMessage());
    }

    @Test
    void getPendingFriendRequests_success() {
        Friendship pendingFriendship = new Friendship();
        pendingFriendship.setId(99L);
        pendingFriendship.setSender(sender);
        pendingFriendship.setRecipient(recipient);
        pendingFriendship.setStatus(FriendshipStatus.PENDING);

        when(friendRepository.findByRecipientIdAndStatus(recipient.getId(), FriendshipStatus.PENDING))
                .thenReturn(List.of(pendingFriendship));

        var pendingRequests = friendService.getPendingFriendRequests(recipient.getId());

        assertNotNull(pendingRequests);
        assertFalse(pendingRequests.isEmpty());
    }

    @Test
    void areFriends_true() {
        Friendship friendship = new Friendship();
        friendship.setId(100L);
        friendship.setSender(sender);
        friendship.setRecipient(recipient);
        friendship.setStatus(FriendshipStatus.ACCEPTED);

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
