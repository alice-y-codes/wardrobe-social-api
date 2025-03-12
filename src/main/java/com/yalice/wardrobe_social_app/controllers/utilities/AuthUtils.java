package com.yalice.wardrobe_social_app.controllers.utilities;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UnauthorizedAccessException;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    private final UserSearchService userSearchService;

    public AuthUtils(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    /**
     * Utility method to get the current authenticated user's username.
     * Throws an UnauthorizedAccessException if the user is not authenticated.
     */
    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedAccessException("You are not logged in");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }

        throw new UnauthorizedAccessException("Invalid authentication details");
    }

    /**
     * Retrieves the current authenticated user's DTO.
     */
    private UserResponseDto getCurrentUser() {
        String username = getAuthenticatedUsername();
        return userSearchService.getUserByUsername(username);
    }

    /**
     * Retrieves the current authenticated user's entity or throws an exception.
     */
    public User getCurrentUserOrElseThrow() {
        UserResponseDto userResponseDto = getCurrentUser();

        User user = userSearchService.getUserEntityById(userResponseDto.getId());
        if (user == null) {
            throw new UnauthorizedAccessException("You are not logged in");
        }

        return user;
    }
}
