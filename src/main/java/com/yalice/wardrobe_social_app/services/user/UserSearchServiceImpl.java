package com.yalice.wardrobe_social_app.services.user;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.UserMapper;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the UserSearchService interface that provides methods for
 * searching and retrieving user details.
 */
@Service
@Transactional(readOnly = true)
public class UserSearchServiceImpl extends BaseService<User, Long> implements UserSearchService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final int MAX_PAGE_SIZE = 50;

    public UserSearchServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        logger.info("Searching for user by username: {}", username);

        validationService.validateStringNotEmpty(username, "Username");

        return userRepository.findByUsername(username)
                .map(userMapper::toResponseDto)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        logger.info("Searching for user by ID: {}", userId);

        validationService.validateNotNull(userId, "User ID");
        return mapEntity(getUserEntityById(userId), userMapper::toResponseDto);
    }

    @Override
    public User getUserEntityById(Long userId) {
        logger.info("Fetching user entity by ID: {}", userId);

        validationService.validateNotNull(userId, "User ID");
        return findById(userId);
    }

    @Override
    public List<UserResponseDto> searchUsersByUsername(String partialUsername) {
        logger.info("Searching for users with partial username: {}", partialUsername);

        validationService.validateStringNotEmpty(partialUsername, "Partial username");
        validationService.validateExists(partialUsername.length() >= 3,
                "Search term must be at least 3 characters long");

        return mapEntityList(
                userRepository.findByUsernameContainingIgnoreCase(partialUsername),
                userMapper::toResponseDto);
    }

    @Override
    public List<UserResponseDto> getAllUsers(int page, int size) {
        logger.info("Fetching all users - Page: {}, Size: {}", page, size);

        validationService.validatePositive((long) page, "Page number");
        validationService.validatePositive((long) size, "Page size");
        validationService.validateExists(size <= MAX_PAGE_SIZE,
                String.format("Page size must not exceed %d", MAX_PAGE_SIZE));

        Page<User> userPage = userRepository.findAll(createPageRequest(page, size));
        return mapEntityList(userPage.getContent(), userMapper::toResponseDto);
    }

    private Pageable createPageRequest(int page, int size) {
        return PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
    }
}
