package com.yalice.wardrobe_social_app.services.user;

import com.yalice.wardrobe_social_app.dtos.user.ChangePasswordDto;
import com.yalice.wardrobe_social_app.dtos.user.UserRegistrationDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.UserManagementService;
import com.yalice.wardrobe_social_app.mappers.UserMapper;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserManagementService that handles user registration,
 * profile updates, password changes, and deletions.
 * This service extends BaseService to reuse common functionality like
 * validation
 * and error handling.
 */
@Service
public class UserManagementServiceImpl extends BaseService<User, Long> implements UserManagementService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_USERNAME_LENGTH = 30;

    public UserManagementServiceImpl(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
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
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        logger.info("Registering new user with username: {}", registrationDto.getUsername());

        validateRegistrationData(registrationDto);
        validateUsernameAvailability(registrationDto.getUsername());

        User user = buildUser(registrationDto);
        user = save(user);

        Profile profile = buildProfile(user);
        profileRepository.save(profile);

        logger.info("Successfully registered user with ID: {}", user.getId());
        return mapEntity(user, userMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDto passwordDto) {
        logger.info("Changing password for user ID: {}", userId);

        validatePasswordChangeData(userId, passwordDto);
        User user = findById(userId);

        validateOldPassword(user, passwordDto.getOldPassword());
        validateNewPassword(passwordDto.getNewPassword());

        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        save(user);

        logger.info("Successfully changed password for user ID: {}", userId);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);

        validationService.validateNotNull(userId, "User ID");
        validationService.validateExists(existsById(userId),
                "User not found with ID: " + userId);

        delete(userId);
        logger.info("Successfully deleted user with ID: {}", userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        validationService.validateStringNotEmpty(username, "Username");
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsById(Long userId) {
        validationService.validateNotNull(userId, "User ID");
        return userRepository.existsById(userId);
    }

    private void validateRegistrationData(UserRegistrationDto registrationDto) {
        validationService.validateNotNull(registrationDto, "Registration data");
        validationService.validateStringNotEmpty(registrationDto.getUsername(), "Username");
        validationService.validateStringNotEmpty(registrationDto.getEmail(), "Email");
        validationService.validateStringNotEmpty(registrationDto.getPassword(), "Password");

        validationService.validateExists(
                registrationDto.getUsername().length() <= MAX_USERNAME_LENGTH,
                String.format("Username must not exceed %d characters", MAX_USERNAME_LENGTH));

        validateNewPassword(registrationDto.getPassword());
    }

    private void validateUsernameAvailability(String username) {
        validationService.validateExists(!existsByUsername(username),
                "Username already exists: " + username);
    }

    private void validatePasswordChangeData(Long userId, ChangePasswordDto passwordDto) {
        validationService.validateNotNull(userId, "User ID");
        validationService.validateNotNull(passwordDto, "Password change data");
        validationService.validateStringNotEmpty(passwordDto.getOldPassword(), "Old password");
        validationService.validateStringNotEmpty(passwordDto.getNewPassword(), "New password");
    }

    private void validateOldPassword(User user, String oldPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new SecurityException("Invalid old password");
        }
    }

    private void validateNewPassword(String password) {
        validationService.validateExists(
                password.length() >= MIN_PASSWORD_LENGTH,
                String.format("Password must be at least %d characters long", MIN_PASSWORD_LENGTH));
    }

    private User buildUser(UserRegistrationDto registrationDto) {
        return User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .provider(registrationDto.getProvider())
                .build();
    }

    private Profile buildProfile(User user) {
        return Profile.builder()
                .user(user)
                .bio("")
                .location("")
                .visibility(Profile.ProfileVisibility.PUBLIC)
                .build();
    }
}
