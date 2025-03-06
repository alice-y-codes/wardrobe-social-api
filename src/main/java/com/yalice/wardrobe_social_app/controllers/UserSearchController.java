package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling user search operations.
 * Provides endpoints for finding and retrieving user information.
 */
@RestController
@RequestMapping("/api/users/search")
public class UserSearchController extends ApiBaseController {

    private final UserSearchService userSearchService;

    @Autowired
    public UserSearchController(UserSearchService userSearchService, AuthUtils authUtils) {
        super(authUtils);
        this.userSearchService = userSearchService;
    }

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return ResponseEntity containing the found user
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByUsername(@PathVariable String username) {
        return handleUserSearch(() -> userSearchService.getUserByUsername(username), "username", username);
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId the ID of the user to find
     * @return ResponseEntity containing the found user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long userId) {
        return handleUserSearch(() -> userSearchService.getUserById(userId), "ID", String.valueOf(userId));
    }

    /**
     * Searches for users by partial username.
     *
     * @param partialUsername the partial username to search for
     * @return ResponseEntity containing the list of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> searchUsersByUsername(
            @RequestParam String partialUsername) {
        logger.info("Searching for users with partial username: {}", partialUsername);

        try {
            List<UserResponseDto> users = userSearchService.searchUsersByUsername(partialUsername);
            logger.info("Found {} users matching partial username: {}", users.size(), partialUsername);
            return createSuccessResponse("Users found successfully", users, HttpStatus.FOUND);
        } catch (Exception e) {
            logger.error("Failed to search users with partial username: {}", partialUsername, e);
            return createInternalServerErrorResponse("Failed to search users");
        }
    }

    /**
     * Gets all users with pagination.
     *
     * @param page the page number (zero-based)
     * @param size the number of users per page
     * @return ResponseEntity containing the paginated list of users
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("Retrieving all users (page: {}, size: {})", page, size);

        try {
            List<UserResponseDto> users = userSearchService.getAllUsers(page, size);
            logger.info("Successfully retrieved {} users for page {} with size {}", users.size(), page, size);
            return createSuccessResponse("Users retrieved successfully", users);
        } catch (Exception e) {
            logger.error("Failed to retrieve users for page {} with size {}", page, size, e);
            return createInternalServerErrorResponse("Failed to retrieve users");
        }
    }

    private ResponseEntity<ApiResponse<UserResponseDto>> handleUserSearch(
            UserSearchSupplier supplier, String searchType, String searchValue) {
        logger.info("Searching for user by {}: {}", searchType, searchValue);

        try {
            UserResponseDto user = supplier.get();
            logger.info("Successfully found user with {}: {}", searchType, searchValue);
            return createSuccessResponse("User found successfully", user);
        } catch (Exception e) {
            logger.error("Failed to find user with {}: {}", searchType, searchValue, e);
            return createNotFoundResponse("User not found with " + searchType + ": " + searchValue);
        }
    }

    @FunctionalInterface
    interface UserSearchSupplier {
        UserResponseDto get() throws Exception;
    }
}
