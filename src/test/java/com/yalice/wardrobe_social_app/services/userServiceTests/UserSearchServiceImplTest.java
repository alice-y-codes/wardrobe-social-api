package com.yalice.wardrobe_social_app.services.userServiceTests;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserNotFoundException;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.UserSearchServiceImpl;
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSearchServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DtoConversionService dtoConversionService;

    @InjectMocks
    private UserSearchServiceImpl userSearchService;

    private User user;
    private UserResponseDto userResponseDto;

    @Mock
    Page<User> userPage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        userResponseDto = new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }

    @Test
    void testGetUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(dtoConversionService.convertToUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto response = userSearchService.getUserByUsername("testuser");

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(dtoConversionService, times(1)).convertToUserResponseDto(user);
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userSearchService.getUserByUsername("testuser");
        });

        assertEquals("User not found with username: testuser", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(dtoConversionService.convertToUserResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto response = userSearchService.getUserById(1L);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(dtoConversionService, times(1)).convertToUserResponseDto(user);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userSearchService.getUserById(1L);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserEntityById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User response = userSearchService.getUserEntityById(1L);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserEntityById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userSearchService.getUserEntityById(1L);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testSearchUsersByUsername_Success() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");

        when(userRepository.findByUsernameContainingIgnoreCase("test")).thenReturn(Arrays.asList(user, user2));

        UserResponseDto userResponseDto1 = new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
        UserResponseDto userResponseDto2 = new UserResponseDto(user2.getId(), user2.getUsername(), user2.getEmail());

        when(dtoConversionService.convertToUserResponseDto(user)).thenReturn(userResponseDto1);
        when(dtoConversionService.convertToUserResponseDto(user2)).thenReturn(userResponseDto2);

        List<UserResponseDto> users = userSearchService.searchUsersByUsername("test");

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        assertEquals("testuser2", users.get(1).getUsername());
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase("test");
        verify(dtoConversionService, times(2)).convertToUserResponseDto(any(User.class));
    }

    @Test
    void testSearchUsersByUsername_EmptyResult() {
        when(userRepository.findByUsernameContainingIgnoreCase("unknown")).thenReturn(List.of());

        List<UserResponseDto> users = userSearchService.searchUsersByUsername("unknown");

        assertNotNull(users);
        assertEquals(0, users.size());
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase("unknown");
    }

    @Test
    void testGetAllUsers_Success() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");
        user2.setEmail("test2@example.com");

        List<User> userList = Arrays.asList(user, user2);
        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);

        UserResponseDto userResponseDto1 = new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
        UserResponseDto userResponseDto2 = new UserResponseDto(user2.getId(), user2.getUsername(), user2.getEmail());

        when(dtoConversionService.convertToUserResponseDto(user)).thenReturn(userResponseDto1);
        when(dtoConversionService.convertToUserResponseDto(user2)).thenReturn(userResponseDto2);

        List<UserResponseDto> users = userSearchService.getAllUsers(0, 10);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        verify(userRepository, times(1)).findAll(PageRequest.of(0, 10));
        verify(dtoConversionService, times(2)).convertToUserResponseDto(any(User.class));
    }

    @Test
    void testGetAllUsers_NoUsers() {
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);
        when(userPage.getContent()).thenReturn(List.of());

        List<UserResponseDto> users = userSearchService.getAllUsers(0, 10);

        assertNotNull(users);
        assertEquals(0, users.size());
        verify(userRepository, times(1)).findAll(PageRequest.of(0, 10));
    }
}
