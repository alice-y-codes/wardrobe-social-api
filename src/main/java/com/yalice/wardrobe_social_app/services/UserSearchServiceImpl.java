package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.UserMapper;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the UserSearchService interface that provides methods for searching and retrieving user details.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto getUserByUsername(String username) {
        log.info("Searching for user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(userMapper::toResponseDto)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        log.info("Searching for user by ID: {}", userId);
        return userMapper.toResponseDto(getUserEntityById(userId));
    }

    @Override
    public User getUserEntityById(Long userId) {
        log.info("Fetching user entity by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });
    }

    @Override
    public List<UserResponseDto> searchUsersByUsername(String partialUsername) {
        log.info("Searching for users with partial username: {}", partialUsername);
        return userRepository.findByUsernameContainingIgnoreCase(partialUsername).stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDto> getAllUsers(int page, int size) {
        log.info("Fetching all users - Page: {}, Size: {}", page, size);
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
        return userPage.stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
