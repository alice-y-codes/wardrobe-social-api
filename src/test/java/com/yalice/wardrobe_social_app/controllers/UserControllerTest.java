package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.UserManagementService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

        private MockMvc mockMvc;

        @Mock
        private UserManagementService userManagementService;

        @Mock
        private UserSearchService userSearchService;

        @Mock
        private AuthUtils authUtils;

        @InjectMocks
        private UserManagementController userManagementController;

        @InjectMocks
        private UserSearchController userSearchController;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private User testUser;
        private UserDto testUserDto;
        private UserResponseDto testUserResponseDto;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(userManagementController, userSearchController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

                initializeTestData();
        }

        private void initializeTestData() {
                testUser = User.builder()
                                .id(1L)
                                .username("testuser")
                                .email("test@example.com")
                                .password("password123")
                                .provider(User.Provider.GOOGLE)
                                .build();

                testUserDto = UserDto.builder()
                                .username("testuser")
                                .email("test@example.com")
                                .password("password123")
                                .build();

                testUserResponseDto = UserResponseDto.builder()
                                .id(1L)
                                .username("testuser")
                                .email("test@example.com")
                                .build();
        }

        @Test
        void registerUser_Success() throws Exception {
                when(userManagementService.registerUser(any(UserDto.class))).thenReturn(testUserResponseDto);

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUserDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("User registered successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.username", is("testuser")))
                                .andExpect(jsonPath("$.data.email", is("test@example.com")));

                verify(userManagementService).registerUser(any(UserDto.class));
        }

        @Test
        void registerUser_ValidationFailure() throws Exception {
                UserDto invalidUserDto = UserDto.builder()
                                .username("test")
                                .email("invalid-email")
                                .password("short") // Too short password
                                .build();

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUserDto)))
                                .andExpect(status().isBadRequest());

                verify(userManagementService, never()).registerUser(any(UserDto.class));
        }

        @Test
        void getUserByUsername_Success() throws Exception {
                when(userSearchService.getUserByUsername(anyString())).thenReturn(testUserResponseDto);

                mockMvc.perform(get("/api/users/search/username/testuser"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("User found successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.username", is("testuser")))
                                .andExpect(jsonPath("$.data.email", is("test@example.com")));

                verify(userSearchService).getUserByUsername(anyString());
        }

        @Test
        void getUserById_Success() throws Exception {
                when(userSearchService.getUserById(anyLong())).thenReturn(testUserResponseDto);

                mockMvc.perform(get("/api/users/search/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("User found successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.username", is("testuser")))
                                .andExpect(jsonPath("$.data.email", is("test@example.com")));

                verify(userSearchService).getUserById(anyLong());
        }

        @Test
        void searchUsersByUsername_Success() throws Exception {
                List<UserResponseDto> users = Arrays.asList(testUserResponseDto);
                when(userSearchService.searchUsersByUsername(anyString())).thenReturn(users);

                mockMvc.perform(get("/api/users/search/search")
                                .param("partialUsername", "test"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Users found successfully")))
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].id", is(1)))
                                .andExpect(jsonPath("$.data[0].username", is("testuser")));

                verify(userSearchService).searchUsersByUsername(anyString());
        }

        @Test
        void getAllUsers_Success() throws Exception {
                List<UserResponseDto> users = Arrays.asList(testUserResponseDto);
                when(userSearchService.getAllUsers(anyInt(), anyInt())).thenReturn(users);

                mockMvc.perform(get("/api/users/search/all")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Users retrieved successfully")))
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].id", is(1)))
                                .andExpect(jsonPath("$.data[0].username", is("testuser")));

                verify(userSearchService).getAllUsers(anyInt(), anyInt());
        }

        @Test
        void updateUserProfile_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(userManagementService.updateUserProfile(anyLong(), any(UserDto.class)))
                                .thenReturn(testUserResponseDto);

                mockMvc.perform(put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUserDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("User profile updated successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.username", is("testuser")));

                verify(userManagementService).updateUserProfile(anyLong(), any(UserDto.class));
        }

        @Test
        void updateUserProfile_Unauthorized() throws Exception {
                User otherUser = User.builder().id(2L).build();
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(otherUser);

                mockMvc.perform(put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testUserDto)))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("You can only update your own profile")));

                verify(userManagementService, never()).updateUserProfile(anyLong(), any(UserDto.class));
        }

        @Test
        void changePassword_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);

                mockMvc.perform(post("/api/users/1/password")
                                .param("oldPassword", "oldpass")
                                .param("newPassword", "newpass"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Password changed successfully")));

                verify(userManagementService).changePassword(anyLong(), anyString(), anyString());
        }

        @Test
        void deleteUser_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);

                mockMvc.perform(delete("/api/users/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("User deleted successfully")));

                verify(userManagementService).deleteUser(anyLong());
        }
}
