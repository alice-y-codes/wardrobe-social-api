package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the UserSearchService interface that provides methods for searching and retrieving user details.
 * This service extends the BaseService to reuse common conversion logic and adds custom logic for user search.
 */
@Service
public class UserSearchServiceImpl extends BaseService implements UserSearchService {

    private final UserRepository userRepository;
    private final DtoConversionService dtoConversionService;

    /**
     * Constructor to inject the UserRepository dependency.
     *
     * @param userRepository The repository to interact with the user database.
     */
    public UserSearchServiceImpl(UserRepository userRepository, DtoConversionService dtoConversionService) {
        this.userRepository = userRepository;
        this.dtoConversionService = dtoConversionService;
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to search for.
     * @return A UserResponseDto containing user details.
     * @throws UserNotFoundException if no user is found with the provided username.
     */
    @Override
    public UserResponseDto getUserByUsername(String username) {
        logger.info("Attempting to find user by username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with username: " + username);
                });

        logger.info("User '{}' found.", user.getUsername());
        return dtoConversionService.convertToUserResponseDto(user);
    }

    /**
     * Retrieves a user by their unique user ID.
     *
     * @param userId The ID of the user to search for.
     * @return A UserResponseDto containing user details.
     * @throws UserNotFoundException if no user is found with the provided ID.
     */
    @Override
    public UserResponseDto getUserById(Long userId) {
        logger.info("Attempting to find user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        logger.info("User with ID '{}' found.", user.getId());
        return dtoConversionService.convertToUserResponseDto(user);
    }

    /**
     * Retrieves a user entity by their unique user ID.
     *
     * @param userId The ID of the user to search for.
     * @return A User containing user details.
     * @throws UserNotFoundException if no user is found with the provided ID.
     */
    @Override
    public User getUserEntityById(Long userId) {
        logger.info("Attempting to find user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        logger.info("User with ID '{}' found.", user.getId());
        return user;
    }

    /**
     * Searches for users with a partial username match. This method returns a list of users whose usernames
     * contain the specified partial username.
     *
     * @param partialUsername The partial username to search for.
     * @return A list of UserResponseDto containing matching user details.
     */
    @Override
    public List<UserResponseDto> searchUsersByUsername(String partialUsername) {
        logger.info("Searching for users with partial username: {}", partialUsername);

        List<User> users = userRepository.findByUsernameContainingIgnoreCase(partialUsername);
        logger.info("Found {} users matching the partial username '{}'.", users.size(), partialUsername);

        return users.stream()
                .map(dtoConversionService::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param page The page number to fetch.
     * @param size The number of users per page.
     * @return A list of UserResponseDto containing user details for the requested page.
     */
    @Override
    public List<UserResponseDto> getAllUsers(int page, int size) {
        logger.info("Fetching all users (Page: {}, Size: {}).", page, size);

        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
        logger.info("Fetched {} users for the requested page.", userPage.getNumberOfElements());

        return userPage.stream()
                .map(dtoConversionService::convertToUserResponseDto)
                .collect(Collectors.toList());
    }
}
