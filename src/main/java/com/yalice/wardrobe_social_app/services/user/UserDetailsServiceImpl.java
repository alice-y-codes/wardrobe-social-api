package com.yalice.wardrobe_social_app.services.user;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Implementation of Spring Security's UserDetailsService.
 * This service is responsible for loading user details during authentication.
 * Extends BaseService to leverage common validation and error handling
 * functionality.
 */
@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl extends BaseService<User, Long> implements UserDetailsService {

    /** Repository to fetch user authentication details. */
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

    /**
     * Loads a user by their username for authentication.
     *
     * @param username The username to load
     * @return UserDetails object containing user authentication info
     * @throws UsernameNotFoundException If the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);

        validationService.validateStringNotEmpty(username, "Username");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        logger.info("Successfully loaded authentication details for user: {}", username);

        return buildUserDetails(user);
    }

    /**
     * Builds a UserDetails object from our User entity.
     * This method can be extended to include roles and authorities.
     */
    private UserDetails buildUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()); // Add authorities/roles here if needed
    }
}
