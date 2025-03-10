package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FriendshipControllerTest {

    @Mock
    private FriendService friendService;

    @Mock
    private AuthUtils authUtils;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;

    @InjectMocks
    private FriendshipController friendshipController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(friendshipController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        testUser = User.builder().id(1L).build();
    }

    @Test
    void sendFriendRequest() throws Exception {
        FriendRequestDto responseDto = createTestFriendRequestDto();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(friendService.sendFriendRequest(any(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/friendships/requests")
                .param("recipientId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void sendFriendRequest_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(post("/api/friendships/requests")
                .param("recipientId", "2"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void acceptFriendRequest() throws Exception {
        FriendResponseDto responseDto = createTestFriendResponseDto();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(friendService.acceptFriendRequest(any(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/friendships/requests/1/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void acceptFriendRequest_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(post("/api/friendships/requests/1/accept"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void acceptFriendRequest_NotFound() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(friendService.acceptFriendRequest(any(), any()))
                .thenThrow(new ResourceNotFoundException("Friend request not found"));

        mockMvc.perform(post("/api/friendships/requests/999/accept"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Friend request not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void rejectFriendRequest() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        doNothing().when(friendService).rejectFriendRequest(any(), any());

        mockMvc.perform(post("/api/friendships/requests/1/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void rejectFriendRequest_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(post("/api/friendships/requests/1/reject"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void rejectFriendRequest_NotFound() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        doThrow(new ResourceNotFoundException("Friend request not found"))
                .when(friendService).rejectFriendRequest(any(), any());

        mockMvc.perform(post("/api/friendships/requests/999/reject"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Friend request not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getPendingFriendRequests() throws Exception {
        List<FriendRequestDto> requests = List.of(createTestFriendRequestDto());
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(friendService.getPendingFriendRequests(any())).thenReturn(requests);

        mockMvc.perform(get("/api/friendships/requests/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    @Test
    void getPendingFriendRequests_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(get("/api/friendships/requests/pending"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getFriends() throws Exception {
        List<FriendResponseDto> friends = List.of(createTestFriendResponseDto());
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(friendService.getFriends(any())).thenReturn(friends);

        mockMvc.perform(get("/api/friendships/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    @Test
    void getFriends_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(get("/api/friendships/friends"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    private FriendRequestDto createTestFriendRequestDto() {
        return FriendRequestDto.builder()
                .id(1L)
                .senderId(1L)
                .senderUsername("testuser")
                .recipientId(2L)
                .recipientUsername("otheruser")
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private FriendResponseDto createTestFriendResponseDto() {
        return FriendResponseDto.builder()
                .id(1L)
                .userId(2L)
                .username("otheruser")
                .status("ACCEPTED")
                .createdAt(LocalDateTime.now())
                .build();
    }
}