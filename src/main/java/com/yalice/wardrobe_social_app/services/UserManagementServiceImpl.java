package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.user.ChangePasswordDto;
import com.yalice.wardrobe_social_app.dtos.user.UserRegistrationDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.UsernameAlreadyExistsException;
import com.yalice.wardrobe_social_app.interfaces.UserManagementService;
import com.yalice.wardrobe_social_app.mappers.UserMapper;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserManagementService that handles user registration,
 * profile updates, password changes, and deletions.
 * This service extends BaseService to reuse common functionality like DTO
 * conversion.
 */
@Service
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserManagementServiceImpl(UserRepository userRepository,
            ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Check if the username already exists
        if (existsByUsername(registrationDto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + registrationDto.getUsername());
        }

        // Use builder to create the User entity
        User user = User.builder()
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .provider(registrationDto.getProvider())
                .build();


        user = userRepository.save(user);


        Profile profile = Profile.builder()
                .user(user)
                .bio("")
                .location("")
                .visibility(Profile.ProfileVisibility.PUBLIC)
                .build();

        profileRepository.save(profile);

        return userMapper.toResponseDto(user);
    }


    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordDto passwordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
            throw new SecurityException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}
