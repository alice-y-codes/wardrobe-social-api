package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling user search operations.
 */
@RestController
@RequestMapping("/users/search")
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
        return handleEntityRetrieval(() -> userSearchService.getUserByUsername(username), "User (username: " + username + ")");
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId the ID of the user to find
     * @return ResponseEntity containing the found user
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long userId) {
        return handleEntityRetrieval(() -> userSearchService.getUserById(userId), "User (ID: " + userId + ")");
    }

    /**
     * Searches for users by partial username.
     *
     * @param partialUsername the partial username to search for
     * @return ResponseEntity containing the list of matching users
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> searchUsersByUsername(@RequestParam String partialUsername) {
        return handleEntityRetrieval(() -> userSearchService.searchUsersByUsername(partialUsername),
                "Users matching partial username: " + partialUsername);
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
        return handleEntityRetrieval(() -> userSearchService.getAllUsers(page, size),
                "All users (page: " + page + ", size: " + size + ")");
    }
}
