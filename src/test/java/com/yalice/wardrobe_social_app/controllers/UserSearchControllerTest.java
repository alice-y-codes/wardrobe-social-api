package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserSearchControllerTest {

    @Mock
    private UserSearchService userSearchService;

    @Mock
    private AuthUtils authUtils;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;

    @InjectMocks
    private UserSearchController userSearchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userSearchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        testUser = User.builder().id(1L).build();
    }

    @Test
    void getUserByUsername() throws Exception {
        UserResponseDto responseDto = createTestUserResponse();
        when(userSearchService.getUserByUsername(any())).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/search/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void getUserByUsername_NotFound() throws Exception {
        when(userSearchService.getUserByUsername(any()))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/search/username/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getUserById() throws Exception {
        UserResponseDto responseDto = createTestUserResponse();
        when(userSearchService.getUserById(any())).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/search/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(userSearchService.getUserById(any()))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/search/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void searchUsers() throws Exception {
        List<UserResponseDto> users = List.of(createTestUserResponse());
        when(userSearchService.searchUsersByUsername(any())).thenReturn(users);

        mockMvc.perform(get("/api/users/search/search")
                .param("partialUsername", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    @Test
    void searchUsers_NoResults() throws Exception {
        when(userSearchService.searchUsersByUsername(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/search/search")
                .param("partialUsername", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserResponseDto> users = List.of(createTestUserResponse());
        when(userSearchService.getAllUsers(0, 20)).thenReturn(users);

        mockMvc.perform(get("/api/users/search/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    @Test
    void getAllUsers_WithPagination() throws Exception {
        List<UserResponseDto> users = List.of(createTestUserResponse());
        when(userSearchService.getAllUsers(1, 10)).thenReturn(users);

        mockMvc.perform(get("/api/users/search/all")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    @Test
    void getAllUsers_NoResults() throws Exception {
        when(userSearchService.getAllUsers(0, 20)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/search/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    private UserResponseDto createTestUserResponse() {
        return UserResponseDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
    }
}
