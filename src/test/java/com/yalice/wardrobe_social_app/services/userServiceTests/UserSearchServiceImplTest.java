package com.yalice.wardrobe_social_app.services.userServiceTests;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserNotFoundException;
import com.yalice.wardrobe_social_app.mappers.UserMapper;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.UserSearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSearchServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserSearchServiceImpl userSearchService;

    private User user;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("testuser").email("test@example.com").build();
        userResponseDto = new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }

    @Test
    void shouldReturnUserWhenUsernameExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto response = userSearchService.getUserByUsername("testuser");

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void shouldThrowExceptionWhenUsernameNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userSearchService.getUserByUsername("testuser"));
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void shouldReturnPagedUsers() {
        User user2 = User.builder().id(2L).username("testuser2").email("test@example.com").build();
        List<User> users = List.of(user, user2);
        Page<User> userPage = new PageImpl<>(users);
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);

        List<UserResponseDto> userDtos = users.stream().map(u -> new UserResponseDto(u.getId(), u.getUsername(), u.getEmail())).toList();
        when(userMapper.toResponseDto(any(User.class))).thenReturn(userDtos.get(0), userDtos.get(1));

        List<UserResponseDto> result = userSearchService.getAllUsers(0, 10);

        assertEquals(2, result.size());
        verify(userRepository).findAll(PageRequest.of(0, 10));
    }
}
