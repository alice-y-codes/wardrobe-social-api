package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.WardrobeService;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WardrobeServiceImpl extends BaseService implements WardrobeService {

    private final WardrobeRepository wardrobeRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public WardrobeServiceImpl(WardrobeRepository wardrobeRepository, UserRepository userRepository, ProfileRepository profileRepository) {
        this.wardrobeRepository = wardrobeRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public WardrobeResponseDto createWardrobe(Long userId, WardrobeDto wardrobeDto) {
        logger.info("Creating wardrobe '{}' for user ID: {}", wardrobeDto.getName(), userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Retrieve the user's profile (assuming a user can have only one profile)
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user ID: " + userId));

        boolean exists = wardrobeRepository.existsByUserIdAndName(userId, wardrobeDto.getName());
        if (exists) {
            throw new IllegalStateException("User already has a wardrobe with this name");
        }

        Wardrobe wardrobe = Wardrobe.builder()
                .name(wardrobeDto.getName())
                .profile(profile) // Associate the wardrobe with the profile
                .build();

        wardrobe = wardrobeRepository.save(wardrobe);
        logger.info("Wardrobe '{}' created successfully for user '{}'.", wardrobe.getName(), user.getUsername());
        return new WardrobeResponseDto(wardrobe.getId(), wardrobe.getName(), wardrobe.getProfile().getId());
    }

    @Override
    @Transactional
    public WardrobeResponseDto getWardrobeById(Long wardrobeId) {
        logger.info("Fetching wardrobe with ID: {}", wardrobeId);

        Wardrobe wardrobe = wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> new ResourceNotFoundException("Wardrobe not found with ID: " + wardrobeId));

        return new WardrobeResponseDto(wardrobe.getId(), wardrobe.getName(), wardrobe.getProfile().getId());
    }

    @Override
    public List<WardrobeResponseDto> getUserWardrobes(Long userId) {
        logger.info("Fetching wardrobes for user ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        List<Wardrobe> wardrobes = wardrobeRepository.findAllByUserId(userId);

        return wardrobes.stream()
                .map(wardrobe -> new WardrobeResponseDto(wardrobe.getId(), wardrobe.getName(), wardrobe.getProfile().getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WardrobeResponseDto updateWardrobe(Long wardrobeId, WardrobeDto wardrobeDto) {
        logger.info("Updating wardrobe with ID: {}", wardrobeId);

        Wardrobe wardrobe = wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> new ResourceNotFoundException("Wardrobe not found with ID: " + wardrobeId));

        wardrobe.setName(wardrobeDto.getName());
        wardrobe = wardrobeRepository.save(wardrobe);

        logger.info("Wardrobe '{}' updated successfully.", wardrobe.getName());
        return new WardrobeResponseDto(wardrobe.getId(), wardrobe.getName(), wardrobe.getProfile().getId());
    }

    @Override
    @Transactional
    public boolean deleteWardrobe(Long wardrobeId) {
        logger.info("Deleting wardrobe with ID: {}", wardrobeId);

        if (!wardrobeRepository.existsById(wardrobeId)) {
            throw new ResourceNotFoundException("Wardrobe not found with ID: " + wardrobeId);
        }

        wardrobeRepository.deleteById(wardrobeId);
        logger.info("Wardrobe with ID '{}' deleted successfully.", wardrobeId);
        return true;
    }
}
