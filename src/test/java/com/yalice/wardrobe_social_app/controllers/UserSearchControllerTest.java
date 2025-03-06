package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserSearchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserSearchService userSearchService;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private UserSearchController userSearchController;

    private UserResponseDto testUserResponseDto;
    private List<UserResponseDto> testUserList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userSearchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        initializeTestData();
    }

    private void initializeTestData() {
        testUserResponseDto = UserResponseDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testUserList = Arrays.asList(
                testUserResponseDto,
                UserResponseDto.builder()
                        .id(2L)
                        .username("otheruser")
                        .email("other@example.com")
                        .build());
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
    void getUserByUsername_NotFound() throws Exception {
        when(userSearchService.getUserByUsername(anyString()))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/users/search/username/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("User not found with username: nonexistent")));

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
    void getUserById_NotFound() throws Exception {
        when(userSearchService.getUserById(anyLong()))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/users/search/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("User not found with ID: 999")));

        verify(userSearchService).getUserById(anyLong());
    }

    @Test
    void searchUsersByUsername_Success() throws Exception {
        when(userSearchService.searchUsersByUsername(anyString())).thenReturn(testUserList);

        mockMvc.perform(get("/api/users/search/search")
                .param("partialUsername", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Users found successfully")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].username", is("testuser")))
                .andExpect(jsonPath("$.data[1].username", is("otheruser")));

        verify(userSearchService).searchUsersByUsername(anyString());
    }

    @Test
    void searchUsersByUsername_Error() throws Exception {
        when(userSearchService.searchUsersByUsername(anyString()))
                .thenThrow(new RuntimeException("Search failed"));

        mockMvc.perform(get("/api/users/search/search")
                .param("partialUsername", "test"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Failed to search users")));

        verify(userSearchService).searchUsersByUsername(anyString());
    }

    @Test
    void getAllUsers_Success() throws Exception {
        when(userSearchService.getAllUsers(anyInt(), anyInt())).thenReturn(testUserList);

        mockMvc.perform(get("/api/users/search/all")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Users retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].username", is("testuser")))
                .andExpect(jsonPath("$.data[1].username", is("otheruser")));

        verify(userSearchService).getAllUsers(anyInt(), anyInt());
    }

    @Test
    void getAllUsers_Error() throws Exception {
        when(userSearchService.getAllUsers(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Failed to retrieve users"));

        mockMvc.perform(get("/api/users/search/all")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Failed to retrieve users")));

        verify(userSearchService).getAllUsers(anyInt(), anyInt());
    }

    @Test
    void getAllUsers_DefaultPagination() throws Exception {
        when(userSearchService.getAllUsers(anyInt(), anyInt())).thenReturn(testUserList);

        mockMvc.perform(get("/api/users/search/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Users retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(userSearchService).getAllUsers(0, 20); // Default values
    }
}