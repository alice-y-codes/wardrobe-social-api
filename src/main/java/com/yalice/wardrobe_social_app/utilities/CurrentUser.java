package com.yalice.wardrobe_social_app.utilities;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UnauthorizedAccessException;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class CurrentUser {

    private final UserService userService;

    public CurrentUser(UserService userService) {
        this.userService = userService;
    }

    /**
     * Utility method to get the current authenticated user.
     */
    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = authentication.getName();
        return userService.findUserByUsername(username);
    }

    /**
     * Utility method to get the current authenticated user or throw an exception if not authenticated.
     */
    public User getCurrentUserOrElseThrow() {
        Optional<User> currentUser = getCurrentUser();
        if (currentUser.isEmpty()) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }
        return currentUser.get();
    }
}
