package com.yalice.wardrobe_social_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.UserService;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Implementation of Spring Security's UserDetailsService.
 * This service is responsible for loading user details during authentication.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /** Service for user-related operations. */
    @Autowired
    @Lazy
    private UserService userService;

    /**
     * Loads a user by their username for authentication purposes.
     *
     * @param username The username to load
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException If the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        final Optional<User> userOptional = userService.findUserByUsername(username);
        final User user = userOptional
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>());
    }
}
