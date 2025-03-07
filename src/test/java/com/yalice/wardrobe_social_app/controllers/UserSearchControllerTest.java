package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserSearchControllerTest {

    private static final String BASE_URL = "/api/users";
    private static final String USERNAME = "user1";
    private static final Long USER_ID = 1L;
    private static final String PARTIAL_USERNAME = "user";
    private static final int PAGE = 0;
    private static final int SIZE = 20;

    private MockMvc mockMvc;

    @Mock
    private UserSearchService userSearchService;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private UserSearchController userSearchController;

    private UserResponseDto userResponseDto;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userSearchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(USER_ID)
                .username(USERNAME)
                .build();
    }

    @Test
    void getUserByUsername_success() throws Exception {
        when(userSearchService.getUserByUsername(USERNAME)).thenReturn(userResponseDto);

        mockMvc.perform(get(BASE_URL + "/username/{username}", USERNAME)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User (username: user1)")))
                .andExpect(jsonPath("$.data.id", is(USER_ID.intValue())))
                .andExpect(jsonPath("$.data.username", is(USERNAME)));

        verify(userSearchService).getUserByUsername(USERNAME);
    }

    @Test
    void getUserByUsername_notFound() throws Exception {
        when(userSearchService.getUserByUsername(USERNAME)).thenReturn(null);

        mockMvc.perform(get(BASE_URL + "/username/{username}", USERNAME)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("User (username: user1) not found")));

        verify(userSearchService).getUserByUsername(USERNAME);
    }

    @Test
    void getUserById_success() throws Exception {
        when(userSearchService.getUserById(USER_ID)).thenReturn(userResponseDto);

        mockMvc.perform(get(BASE_URL + "/{userId}", USER_ID)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User (ID: 1)")))
                .andExpect(jsonPath("$.data.id", is(USER_ID.intValue())))
                .andExpect(jsonPath("$.data.username", is(USERNAME)));

        verify(userSearchService).getUserById(USER_ID);
    }

    @Test
    void getUserById_notFound() throws Exception {
        when(userSearchService.getUserById(USER_ID)).thenReturn(null);

        mockMvc.perform(get(BASE_URL + "/{userId}", USER_ID)
                        .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("User (ID: 1) not found")));

        verify(userSearchService).getUserById(USER_ID);
    }

    @Test
    void searchUsersByUsername_success() throws Exception {
        List<UserResponseDto> userList = List.of(userResponseDto);
        when(userSearchService.searchUsersByUsername(PARTIAL_USERNAME)).thenReturn(userList);

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("username", PARTIAL_USERNAME)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Users matching partial username: user")))
                .andExpect(jsonPath("$.data[0].id", is(USER_ID.intValue())))
                .andExpect(jsonPath("$.data[0].username", is(USERNAME)));

        verify(userSearchService).searchUsersByUsername(PARTIAL_USERNAME);
    }

    @Test
    void searchUsersByUsername_emptyList() throws Exception {
        List<UserResponseDto> userList = List.of();
        when(userSearchService.searchUsersByUsername(PARTIAL_USERNAME)).thenReturn(userList);

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("username", PARTIAL_USERNAME)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Users matching partial username: user")))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(userSearchService).searchUsersByUsername(PARTIAL_USERNAME);
    }

    @Test
    void getAllUsers_success() throws Exception {
        List<UserResponseDto> userList = List.of(userResponseDto);
        when(userSearchService.getAllUsers(PAGE, SIZE)).thenReturn(userList);

        mockMvc.perform(get(BASE_URL + "/all")
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("All users (page: 0, size: 20)")))
                .andExpect(jsonPath("$.data[0].id", is(USER_ID.intValue())))
                .andExpect(jsonPath("$.data[0].username", is(USERNAME)));

        verify(userSearchService).getAllUsers(PAGE, SIZE);
    }

    @Test
    void getAllUsers_emptyList() throws Exception {
        List<UserResponseDto> userList = List.of();
        when(userSearchService.getAllUsers(PAGE, SIZE)).thenReturn(userList);

        mockMvc.perform(get(BASE_URL + "/all")
                        .param("page", String.valueOf(PAGE))
                        .param("size", String.valueOf(SIZE))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("All users (page: 0, size: 20)")))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(userSearchService).getAllUsers(PAGE, SIZE);
    }
}
