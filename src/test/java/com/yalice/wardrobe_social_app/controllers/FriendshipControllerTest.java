package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.FriendRequestDto;
import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.Friendship.FriendshipStatus;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FriendshipControllerTest {

        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Mock
        private FriendshipService friendshipService;

        @Mock
        private UserSearchService userSearchService;

        @InjectMocks
        private FriendshipController friendshipController;

        private User user1;
        private User user2;
        private Friendship pendingFriendship;
        private Friendship acceptedFriendship;
        private FriendRequestDto friendRequestDto;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(friendshipController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

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

                friendRequestDto = new FriendRequestDto();
                friendRequestDto.setRecipientId(2L);
                friendRequestDto.setRequestId(1L);
                friendRequestDto.setFriendId(2L);
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void sendFriendRequest_whenValidRequest_returnsFriendship() throws Exception {
                // Setup authentication
                setupAuthentication("user1");

                when(userSearchService.findUserByUsername("user1")).thenReturn(Optional.of(user1));
                when(friendshipService.sendFriendRequest(1L, 2L)).thenReturn(pendingFriendship);

                mockMvc.perform(post("/api/friends/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(friendRequestDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        void acceptFriendRequest_whenValidRequest_returnsFriendship() throws Exception {
                // Setup authentication
                setupAuthentication("user2");

                when(userSearchService.findUserByUsername("user2")).thenReturn(Optional.of(user2));
                when(friendshipService.acceptFriendRequest(1L, 2L)).thenReturn(acceptedFriendship);

                mockMvc.perform(post("/api/friends/accept")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(friendRequestDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(2))
                                .andExpect(jsonPath("$.status").value("ACCEPTED"));
        }

        @Test
        void rejectFriendRequest_whenValidRequest_returnsSuccess() throws Exception {
                // Setup authentication
                setupAuthentication("user2");

                when(userSearchService.findUserByUsername("user2")).thenReturn(Optional.of(user2));

                mockMvc.perform(post("/api/friends/reject")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(friendRequestDto)))
                                .andExpect(status().isOk());
        }

        @Test
        void removeFriend_whenValidRequest_returnsSuccess() throws Exception {
                // Setup authentication
                setupAuthentication("user1");

                when(userSearchService.findUserByUsername("user1")).thenReturn(Optional.of(user1));

                mockMvc.perform(delete("/api/friends/remove")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(friendRequestDto)))
                                .andExpect(status().isOk());
        }

        @Test
        void getFriends_returnsListOfFriends() throws Exception {
                // Setup authentication
                setupAuthentication("user1");

                when(userSearchService.findUserByUsername("user1")).thenReturn(Optional.of(user1));
                when(friendshipService.getFriends(1L)).thenReturn(Arrays.asList(user2));

                mockMvc.perform(get("/api/friends"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(2))
                                .andExpect(jsonPath("$[0].username").value("user2"));
        }

        @Test
        void getPendingFriendRequests_returnsListOfPendingRequests() throws Exception {
                // Setup authentication
                setupAuthentication("user2");

                when(userSearchService.findUserByUsername("user2")).thenReturn(Optional.of(user2));
                when(friendshipService.getPendingFriendRequests(2L)).thenReturn(Arrays.asList(pendingFriendship));

                mockMvc.perform(get("/api/friends/pending"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].status").value("PENDING"));
        }

        private void setupAuthentication(String username) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                username, null, new ArrayList<>());
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
        }
}