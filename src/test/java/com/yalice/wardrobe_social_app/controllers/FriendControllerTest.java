package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FriendControllerTest {

        private MockMvc mockMvc;

        @Mock
        private FriendService friendService;

        @Mock
        private AuthUtils authUtils;

        @InjectMocks
        private FriendshipController friendshipController;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private User testUser;
        private FriendRequestDto testFriendRequestDto;
        private FriendResponseDto testFriendResponseDto;
        private List<FriendRequestDto> testFriendRequestList;
        private List<FriendResponseDto> testFriendshipList;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(friendshipController)
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();

                initializeTestData();
        }

        private void initializeTestData() {
                testUser = User.builder()
                        .id(1L)
                        .username("testuser")
                        .email("test@example.com")
                        .build();

                testFriendRequestDto = createFriendRequestDto(1L, "testuser", 2L, "otheruser", "PENDING");
                testFriendResponseDto = createFriendResponseDto(1L, "otheruser", "ACCEPTED");

                testFriendRequestList = Arrays.asList(
                        testFriendRequestDto,
                        createFriendRequestDto(2L, "thirduser", 1L, "testuser", "PENDING")
                );

                testFriendshipList = Arrays.asList(
                        testFriendResponseDto,
                        createFriendResponseDto(2L, "thirduser", "ACCEPTED")
                );
        }

        private FriendRequestDto createFriendRequestDto(Long id, String senderUsername, Long recipientId, String recipientUsername, String status) {
                return FriendRequestDto.builder()
                        .id(id)
                        .senderId(1L)
                        .senderUsername(senderUsername)
                        .recipientId(recipientId)
                        .recipientUsername(recipientUsername)
                        .status(status)
                        .createdAt(LocalDateTime.now())
                        .build();
        }

        private FriendResponseDto createFriendResponseDto(Long id, String username, String status) {
                return FriendResponseDto.builder()
                        .id(id)
                        .userId(2L)
                        .username(username)
                        .status(status)
                        .createdAt(LocalDateTime.now())
                        .build();
        }

        @Test
        void sendFriendRequest_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(friendService.sendFriendRequest(anyLong(), anyLong())).thenReturn(testFriendRequestDto);

                mockMvc.perform(post("/api/friendships/requests")
                                .param("recipientId", "2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Friend request sent successfully")))
                        .andExpect(jsonPath("$.data.id", is(1)))
                        .andExpect(jsonPath("$.data.senderUsername", is("testuser")))
                        .andExpect(jsonPath("$.data.recipientUsername", is("otheruser")))
                        .andExpect(jsonPath("$.data.status", is("PENDING")));

                verify(friendService).sendFriendRequest(anyLong(), anyLong());
        }

        @Test
        void sendFriendRequest_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(friendService.sendFriendRequest(anyLong(), anyLong())).thenThrow(new RuntimeException("Failed to send friend request"));

                mockMvc.perform(post("/api/friendships/requests")
                                .param("recipientId", "2"))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.message", is("Failed to send friend request")));

                verify(friendService).sendFriendRequest(anyLong(), anyLong());
        }

        @Test
        void acceptFriendRequest_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(friendService.acceptFriendRequest(anyLong(), anyLong())).thenReturn(testFriendResponseDto);

                mockMvc.perform(post("/api/friendships/requests/1/accept"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Friend request accepted successfully")))
                        .andExpect(jsonPath("$.data.id", is(1)))
                        .andExpect(jsonPath("$.data.username", is("otheruser")))
                        .andExpect(jsonPath("$.data.status", is("ACCEPTED")));

                verify(friendService).acceptFriendRequest(anyLong(), anyLong());
        }

        @Test
        void acceptFriendRequest_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(friendService.acceptFriendRequest(anyLong(), anyLong())).thenThrow(new RuntimeException("Failed to accept friend request"));

                mockMvc.perform(post("/api/friendships/requests/1/accept"))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.message", is("Failed to accept friend request")));

                verify(friendService).acceptFriendRequest(anyLong(), anyLong());
        }

        @Test
        void rejectFriendRequest_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                doNothing().when(friendService).rejectFriendRequest(anyLong(), anyLong());

                mockMvc.perform(post("/api/friendships/requests/1/reject"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Friend request rejected successfully")))
                        .andExpect(jsonPath("$.data", nullValue()));

                verify(friendService).rejectFriendRequest(anyLong(), anyLong());
        }

        @Test
        void rejectFriendRequest_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                doThrow(new RuntimeException("Failed to reject friend request")).when(friendService).rejectFriendRequest(anyLong(), anyLong());

                mockMvc.perform(post("/api/friendships/requests/1/reject"))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.message", is("Failed to reject friend request")));

                verify(friendService).rejectFriendRequest(anyLong(), anyLong());
        }

        @Test
        void getPendingFriendRequests_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(friendService.getPendingFriendRequests(anyLong())).thenReturn(testFriendRequestList);

                mockMvc.perform(get("/api/friendships/requests/pending"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Pending friend requests retrieved successfully")))
                        .andExpect(jsonPath("$.data", hasSize(2)))
                        .andExpect(jsonPath("$.data[0].senderUsername", is("testuser")))
                        .andExpect(jsonPath("$.data[1].senderUsername", is("thirduser")));

                verify(friendService).getPendingFriendRequests(anyLong());
        }

        @Test
        void getPendingFriendRequests_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(friendService.getPendingFriendRequests(anyLong())).thenThrow(new RuntimeException("Failed to retrieve pending friend requests"));

                mockMvc.perform(get("/api/friendships/requests/pending"))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.message", is("Failed to retrieve pending friend requests")));

                verify(friendService).getPendingFriendRequests(anyLong());
        }

        @Test
        void getFriends_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(friendService.getFriends(anyLong())).thenReturn(testFriendshipList);

                mockMvc.perform(get("/api/friendships/friends"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Friends retrieved successfully")))
                        .andExpect(jsonPath("$.data", hasSize(2)))
                        .andExpect(jsonPath("$.data[0].username", is("otheruser")))
                        .andExpect(jsonPath("$.data[1].username", is("thirduser")));

                verify(friendService).getFriends(anyLong());
        }

        @Test
        void getFriends_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(friendService.getFriends(anyLong())).thenThrow(new RuntimeException("Failed to retrieve friends"));

                mockMvc.perform(get("/api/friendships/friends"))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.message", is("Failed to retrieve friends")));

                verify(friendService).getFriends(anyLong());
        }
}
