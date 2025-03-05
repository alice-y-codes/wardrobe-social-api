package com.yalice.wardrobe_social_app.services.friendshipServicesTests;

import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.FriendshipRepository;
import com.yalice.wardrobe_social_app.services.FriendshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserSearchService userSearchService;

    @InjectMocks
    private FriendshipServiceImpl friendshipService;

    private User user1;
    private User user2;
    private Friendship pendingFriendship;
    private Friendship acceptedFriendship;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .build();

        pendingFriendship = Friendship.builder()
                .id(1L)
                .requester(user1)
                .recipient(user2)
                .status(FriendshipStatus.PENDING)
                .build();

        acceptedFriendship = Friendship.builder()
                .id(2L)
                .requester(user1)
                .recipient(user2)
                .status(FriendshipStatus.ACCEPTED)
                .build();
    }

    @Test
    void sendFriendRequest_createsAndReturnsFriendship() {
        // Arrange
        when(userSearchService.findById(1L)).thenReturn(Optional.of(user1));
        when(userSearchService.findById(2L)).thenReturn(Optional.of(user2));
        when(friendshipRepository.findByRequesterAndRecipient(any(User.class), any(User.class)))
                .thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(pendingFriendship);

        // Act
        Friendship result = friendshipService.sendFriendRequest(1L, 2L);

        // Assert
        assertNotNull(result);
        assertEquals(FriendshipStatus.PENDING, result.getStatus());
        assertEquals(user1, result.getRequester());
        assertEquals(user2, result.getRecipient());
        verify(friendshipRepository).save(any(Friendship.class));
    }

    @Test
    void acceptFriendRequest_whenRequestExists_updatesStatusAndReturnsFriendship() {
        // Arrange
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(pendingFriendship));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(acceptedFriendship);

        // Act
        Friendship result = friendshipService.acceptFriendRequest(1L, 2L);

        // Assert
        assertNotNull(result);
        assertEquals(FriendshipStatus.ACCEPTED, result.getStatus());
        verify(friendshipRepository).save(pendingFriendship);
    }

    @Test
    void rejectFriendRequest_whenRequestExists_updatesStatusAndSaves() {
        // Arrange
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(pendingFriendship));

        // Act
        friendshipService.rejectFriendRequest(1L, 2L);

        // Assert
        assertEquals(FriendshipStatus.REJECTED, pendingFriendship.getStatus());
        verify(friendshipRepository).save(pendingFriendship);
    }

    @Test
    void removeFriend_whenFriendshipExists_deletesFriendship() {
        // Arrange
        when(userSearchService.findById(1L)).thenReturn(Optional.of(user1));
        when(userSearchService.findById(2L)).thenReturn(Optional.of(user2));
        when(friendshipRepository.findFriendshipBetweenUsers(user1, user2)).thenReturn(Optional.of(acceptedFriendship));

        // Act
        friendshipService.removeFriend(1L, 2L);

        // Assert
        verify(friendshipRepository).delete(acceptedFriendship);
    }

    @Test
    void getFriends_returnsListOfFriends() {
        // Arrange
        List<User> friends = Arrays.asList(user2);
        when(friendshipRepository.findFriendsWhoAcceptedRequestFromUser(1L)).thenReturn(friends);
        when(friendshipRepository.findFriendsWhoSentAcceptedRequestToUser(1L)).thenReturn(List.of());

        // Act
        List<User> result = friendshipService.getFriends(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(user2, result.get(0));
    }

    @Test
    void getPendingFriendRequests_returnsListOfPendingRequests() {
        // Arrange
        when(userSearchService.findById(1L)).thenReturn(Optional.of(user1));
        when(friendshipRepository.findByRecipientAndStatus(user1, FriendshipStatus.PENDING))
                .thenReturn(List.of(pendingFriendship));

        // Act
        List<Friendship> result = friendshipService.getPendingFriendRequests(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(pendingFriendship, result.get(0));
    }

    @Test
    void areFriends_whenFriendshipExists_returnsTrue() {
        // Arrange
        when(friendshipRepository.findAcceptedFriendshipsForUser(1L))
                .thenReturn(List.of(acceptedFriendship));
        when(friendshipRepository.findAcceptedFriendshipsForUser(2L))
                .thenReturn(List.of(acceptedFriendship));

        // Act
        boolean result = friendshipService.areFriends(1L, 2L);

        // Assert
        assertTrue(result);
    }

    @Test
    void areFriends_whenFriendshipDoesNotExist_returnsFalse() {
        // Arrange
        when(friendshipRepository.findAcceptedFriendshipsForUser(anyLong()))
                .thenReturn(List.of());

        // Act
        boolean result = friendshipService.areFriends(1L, 2L);

        // Assert
        assertFalse(result);
    }

    @Test
    void getFriendshipBetweenUsers_whenFriendshipExists_returnsFriendship() {
        // Arrange
        when(userSearchService.findById(1L)).thenReturn(Optional.of(user1));
        when(userSearchService.findById(2L)).thenReturn(Optional.of(user2));
        when(friendshipRepository.findFriendshipBetweenUsers(user1, user2))
                .thenReturn(Optional.of(acceptedFriendship));

        // Act
        Optional<Friendship> result = friendshipService.getFriendshipBetweenUsers(1L, 2L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(acceptedFriendship, result.get());
    }
}