package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.WardrobeService;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WardrobeServiceImpl extends BaseService implements WardrobeService {

    private final WardrobeRepository wardrobeRepository;
    private final ProfileRepository profileRepository;

    public WardrobeServiceImpl(WardrobeRepository wardrobeRepository, ProfileRepository profileRepository) {
        this.wardrobeRepository = wardrobeRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public WardrobeResponseDto createWardrobe(Long profileId, WardrobeDto wardrobeDto) {
        logger.info("Creating wardrobe '{}' for profile ID: {}", wardrobeDto.getName(), profileId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));

        boolean exists = wardrobeRepository.existsByProfileIdAndName(profileId, wardrobeDto.getName());
        if (exists) {
            throw new IllegalStateException("Profile already has a wardrobe with this name");
        }

        Wardrobe wardrobe = Wardrobe.builder()
                .name(wardrobeDto.getName())
                .profile(profile)
                .build();

        wardrobe = wardrobeRepository.save(wardrobe);
        logger.info("Wardrobe '{}' created successfully for profile ID '{}'.", wardrobe.getName(), profileId);
        return new WardrobeResponseDto(wardrobe.getId(), wardrobe.getName(), wardrobe.getProfile().getId());
    }

    @Override
    @Transactional
    public WardrobeResponseDto getWardrobeById(Long wardrobeId) {
        logger.info("Fetching wardrobe with ID: {}", wardrobeId);

        Wardrobe wardrobe = wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> new ResourceNotFoundException("Wardrobe not found with ID: " + wardrobeId));

        return convertToWardrobeResponseDto(wardrobe);
    }

    @Override
    public List<WardrobeResponseDto> getProfileWardrobes(Long profileId) {
        logger.info("Fetching wardrobes for profile ID: {}", profileId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));

        List<Wardrobe> wardrobes = wardrobeRepository.findAllByProfileId(profileId);

        return wardrobes.stream()
                .map(this::convertToWardrobeResponseDto).toList();
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
        return convertToWardrobeResponseDto(wardrobe);
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
