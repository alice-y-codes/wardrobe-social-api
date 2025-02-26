package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final Set<String> VALID_PROVIDERS = Set.of("local", "google", "facebook");
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> registerUser(User user) {
        // Business rule validations
        validateBusinessRules(user);

        // Check if username exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return Optional.empty();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    private void validateBusinessRules(User user) {
        // Password strength validation
        if (user.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new UserRegistrationException("Password must be at least 8 characters long");
        }

        // Provider validation
        if (!VALID_PROVIDERS.contains(user.getProvider())) {
            throw new UserRegistrationException("Provider is not valid");
        }

        // Here you could add more business validations:
        // - Email format validation
        // - Password complexity rules
        // - Username format rules
        // - etc.
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}