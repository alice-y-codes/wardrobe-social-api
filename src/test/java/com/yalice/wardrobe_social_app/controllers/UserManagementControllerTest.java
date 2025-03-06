package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
import com.yalice.wardrobe_social_app.interfaces.UserManagementService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserManagementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserManagementService userManagementService;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private UserManagementController userManagementController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User testUser;
    private UserDto testUserDto;
    private UserResponseDto testUserResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userManagementController)
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
    void registerUser_ValidationError() throws Exception {
        UserDto invalidUserDto = UserDto.builder()
                .username("") // Empty username
                .email("invalid-email") // Invalid email
                .password("") // Empty password
                .build();

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")));

        verify(userManagementService, never()).registerUser(any(UserDto.class));
    }

    @Test
    void registerUser_DuplicateUsername() throws Exception {
        when(userManagementService.registerUser(any(UserDto.class)))
                .thenThrow(new UserRegistrationException("Username already exists"));

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Username already exists")));

        verify(userManagementService).registerUser(any(UserDto.class));
    }

    @Test
    void updateUserProfile_Success() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(userManagementService.updateUserProfile(anyLong(), any(UserDto.class))).thenReturn(testUserResponseDto);

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
        User unauthorizedUser = User.builder()
                .id(2L)
                .username("otheruser")
                .build();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(unauthorizedUser);

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
        doNothing().when(userManagementService).changePassword(anyLong(), anyString(), anyString());

        mockMvc.perform(post("/api/users/1/password")
                .param("oldPassword", "oldpass123")
                .param("newPassword", "newpass123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Password changed successfully")));

        verify(userManagementService).changePassword(anyLong(), anyString(), anyString());
    }

    @Test
    void changePassword_Unauthorized() throws Exception {
        User unauthorizedUser = User.builder()
                .id(2L)
                .username("otheruser")
                .build();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(unauthorizedUser);

        mockMvc.perform(post("/api/users/1/password")
                .param("oldPassword", "oldpass123")
                .param("newPassword", "newpass123"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("You can only change your own password")));

        verify(userManagementService, never()).changePassword(anyLong(), anyString(), anyString());
    }

    @Test
    void deleteUser_Success() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        doNothing().when(userManagementService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User deleted successfully")));

        verify(userManagementService).deleteUser(anyLong());
    }

    @Test
    void deleteUser_Unauthorized() throws Exception {
        User unauthorizedUser = User.builder()
                .id(2L)
                .username("otheruser")
                .build();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(unauthorizedUser);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("You can only delete your own account")));

        verify(userManagementService, never()).deleteUser(anyLong());
    }
}