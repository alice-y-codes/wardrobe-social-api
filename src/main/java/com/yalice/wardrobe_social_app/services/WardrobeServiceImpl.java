package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.WardrobeService;
import com.yalice.wardrobe_social_app.mappers.WardrobeMapper;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WardrobeServiceImpl extends BaseService implements WardrobeService {

    private final WardrobeRepository wardrobeRepository;
    private final ProfileRepository profileRepository;
    private final WardrobeMapper wardrobeMapper;

    public WardrobeServiceImpl(WardrobeRepository wardrobeRepository, ProfileRepository profileRepository, WardrobeMapper wardrobeMapper) {
        this.wardrobeRepository = wardrobeRepository;
        this.profileRepository = profileRepository;
        this.wardrobeMapper = wardrobeMapper;
    }

    @Override
    @Transactional
    public WardrobeResponseDto createWardrobe(Long profileId, WardrobeDto wardrobeDto) {
        logger.info("Creating wardrobe '{}' for profile ID: {}", wardrobeDto.getName(), profileId);

        Profile profile = findProfileById(profileId);
        ensureWardrobeDoesNotExist(profileId, wardrobeDto.getName());

        Wardrobe wardrobe = wardrobeRepository.save(buildWardrobe(wardrobeDto, profile));
        logger.info("Wardrobe '{}' created successfully for profile ID '{}'.", wardrobe.getName(), profileId);

        return wardrobeMapper.toResponseDto(wardrobe);
    }

    @Override
    @Transactional
    public WardrobeResponseDto getWardrobeById(Long wardrobeId) {
        logger.info("Fetching wardrobe with ID: {}", wardrobeId);
        Wardrobe wardrobe = findWardrobeById(wardrobeId);
        return wardrobeMapper.toResponseDto(wardrobe);
    }

    @Override
    public List<WardrobeResponseDto> getProfileWardrobes(Long profileId) {
        logger.info("Fetching wardrobes for profile ID: {}", profileId);

        findProfileById(profileId);

        List<Wardrobe> wardrobes = wardrobeRepository.findAllByProfileId(profileId);
        return wardrobes.stream()
                .map(wardrobeMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WardrobeResponseDto updateWardrobe(Long wardrobeId, WardrobeDto wardrobeDto) {
        logger.info("Updating wardrobe with ID: {}", wardrobeId);

        Wardrobe wardrobe = findWardrobeById(wardrobeId);
        wardrobe.setName(wardrobeDto.getName());

        Wardrobe updatedWardrobe = wardrobeRepository.save(wardrobe);
        logger.info("Wardrobe '{}' updated successfully.", updatedWardrobe.getName());

        return wardrobeMapper.toResponseDto(updatedWardrobe);
    }

    @Override
    @Transactional
    public boolean deleteWardrobe(Long wardrobeId) {
        logger.info("Deleting wardrobe with ID: {}", wardrobeId);

        findWardrobeById(wardrobeId);  // Throws exception if not found
        wardrobeRepository.deleteById(wardrobeId);

        logger.info("Wardrobe with ID '{}' deleted successfully.", wardrobeId);
        return true;
    }

    // Private Helper Methods

    private Profile findProfileById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));
    }

    private void ensureWardrobeDoesNotExist(Long profileId, String wardrobeName) {
        boolean exists = wardrobeRepository.existsByProfileIdAndName(profileId, wardrobeName);
        if (exists) {
            throw new IllegalStateException("Profile already has a wardrobe with this name");
        }
    }

    private Wardrobe findWardrobeById(Long wardrobeId) {
        return wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> new ResourceNotFoundException("Wardrobe not found with ID: " + wardrobeId));
    }

    private Wardrobe buildWardrobe(WardrobeDto wardrobeDto, Profile profile) {
        return Wardrobe.builder()
                .name(wardrobeDto.getName())
                .profile(profile)
                .build();
    }
}
