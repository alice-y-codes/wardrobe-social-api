package com.yalice.wardrobe_social_app.utilities;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UnauthorizedAccessException;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    private final UserSearchService userSearchService;

    public AuthUtils(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    /**
     * Utility method to get the current authenticated user.
     * Throws an UnauthorizedAccessException if the user is not authenticated.
     */
    private UserResponseDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccessException("You are not logged in");
        }

        String username = authentication.getName();
        return userSearchService.getUserByUsername(username);
    }

    /**
     * Utility method to get the current authenticated user or throw an exception if not authenticated.
     * Retrieves the User entity of the authenticated user.
     */
    public User getCurrentUserOrElseThrow() {
        UserResponseDto userResponseDto = getCurrentUser();
        User currentUser = userSearchService.getUserEntityById(userResponseDto.getId());

        if (currentUser == null) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }

        return currentUser;
    }
}
