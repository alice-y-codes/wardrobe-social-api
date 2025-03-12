package com.yalice.wardrobe_social_app.services.core;

import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.interfaces.WardrobeService;
import com.yalice.wardrobe_social_app.mappers.WardrobeMapper;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WardrobeServiceImpl extends BaseService<Wardrobe, Long> implements WardrobeService {

    private final WardrobeRepository wardrobeRepository;
    private final ProfileService profileService;
    private final WardrobeMapper wardrobeMapper;

    public WardrobeServiceImpl(
            WardrobeRepository wardrobeRepository,
            ProfileService profileService,
            WardrobeMapper wardrobeMapper) {
        this.wardrobeRepository = wardrobeRepository;
        this.profileService = profileService;
        this.wardrobeMapper = wardrobeMapper;
    }

    @Override
    protected JpaRepository<Wardrobe, Long> getRepository() {
        return wardrobeRepository;
    }

    @Override
    protected String getEntityName() {
        return "Wardrobe";
    }

    @Override
    @Transactional
    public WardrobeResponseDto createWardrobe(Long profileId, WardrobeDto wardrobeDto) {
        logger.info("Creating wardrobe '{}' for profile ID: {}", wardrobeDto.getName(), profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateNotNull(wardrobeDto, "Wardrobe data");
        validationService.validateStringNotEmpty(wardrobeDto.getName(), "Name");

        Profile profile = profileService.getProfileEntityById(profileId);
        ensureWardrobeDoesNotExist(profileId, wardrobeDto.getName());

        Wardrobe wardrobe = buildWardrobe(wardrobeDto, profile);
        return mapEntity(save(wardrobe), wardrobeMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public WardrobeResponseDto getWardrobeById(Long wardrobeId) {
        logger.info("Fetching wardrobe with ID: {}", wardrobeId);
        return mapEntity(findById(wardrobeId), wardrobeMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WardrobeResponseDto> getProfileWardrobes(Long profileId) {
        logger.info("Fetching wardrobes for profile ID: {}", profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateExists(profileService.getProfileEntityById(profileId) != null,
                "Profile not found with ID: " + profileId);

        return mapEntityList(wardrobeRepository.findAllByProfileId(profileId), wardrobeMapper::toResponseDto);
    }

    @Override
    @Transactional
    public WardrobeResponseDto updateWardrobe(Long wardrobeId, WardrobeDto wardrobeDto) {
        logger.info("Updating wardrobe with ID: {}", wardrobeId);

        validationService.validateNotNull(wardrobeId, "Wardrobe ID");
        validationService.validateNotNull(wardrobeDto, "Wardrobe data");
        validationService.validateStringNotEmpty(wardrobeDto.getName(), "Name");

        Wardrobe wardrobe = findById(wardrobeId);
        wardrobe.setName(wardrobeDto.getName());

        return mapEntity(save(wardrobe), wardrobeMapper::toResponseDto);
    }

    @Override
    @Transactional
    public boolean deleteWardrobe(Long wardrobeId) {
        logger.info("Deleting wardrobe with ID: {}", wardrobeId);

        validationService.validateNotNull(wardrobeId, "Wardrobe ID");
        Wardrobe wardrobe = findById(wardrobeId);

        // Don't allow deleting the default wardrobe
        validationService.validateExists(!wardrobe.getName().equals("Wardrobe"),
                "Cannot delete the default wardrobe");

        delete(wardrobeId);
        return true;
    }

    private void ensureWardrobeDoesNotExist(Long profileId, String wardrobeName) {
        validationService.validateExists(!wardrobeRepository.existsByProfileIdAndName(profileId, wardrobeName),
                "Profile already has a wardrobe with this name");
    }

    private Wardrobe buildWardrobe(WardrobeDto wardrobeDto, Profile profile) {
        return Wardrobe.builder()
                .name(wardrobeDto.getName())
                .profile(profile)
                .build();
    }
}
