package com.yalice.wardrobe_social_app.services.core;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.mappers.OutfitMapper;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import com.yalice.wardrobe_social_app.services.helpers.ImageHandlerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class OutfitServiceImpl extends BaseService<Outfit, Long> implements OutfitService {

    private final ProfileService profileService;
    private final ItemService itemService;
    private final OutfitRepository outfitRepository;
    private final OutfitMapper outfitMapper;
    private final ImageHandlerService imageHandler;

    public OutfitServiceImpl(
            OutfitRepository outfitRepository,
            ProfileService profileService,
            ItemService itemService,
            OutfitMapper outfitMapper,
            ImageHandlerService imageHandler) {
        this.outfitRepository = outfitRepository;
        this.profileService = profileService;
        this.itemService = itemService;
        this.outfitMapper = outfitMapper;
        this.imageHandler = imageHandler;
    }

    @Override
    protected JpaRepository<Outfit, Long> getRepository() {
        return outfitRepository;
    }

    @Override
    protected String getEntityName() {
        return "Outfit";
    }

    @Override
    @Transactional
    public OutfitResponseDto createOutfit(Long profileId, OutfitDto outfitDto, MultipartFile image) {
        logger.info("Creating outfit for profile ID: {}", profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateNotNull(outfitDto, "Outfit data");
        validationService.validateStringNotEmpty(outfitDto.getName(), "Name");

        Profile profile = profileService.getProfileEntityById(profileId);
        Outfit outfit = buildOutfit(outfitDto, profile);
        outfit = save(outfit);

        if (image != null && !image.isEmpty()) {
            outfit.setImageUrl(imageHandler.handleImageUpload(image, "outfit", outfit.getId(), null));
            outfit = save(outfit);
        }

        return mapEntity(outfit, outfitMapper::toResponseDto);
    }

    @Override
    @Transactional
    public OutfitResponseDto updateOutfit(Long profileId, Long outfitId, OutfitDto outfitDto, MultipartFile image) {
        logger.info("Updating outfit with ID: {} for profile ID: {}", outfitId, profileId);

        Outfit outfit = findById(outfitId);
        validationService.validateOwnership(outfit.getProfile(), profileId, "outfit");
        validationService.validateNotNull(outfitDto, "Outfit data");
        validationService.validateStringNotEmpty(outfitDto.getName(), "Name");

        updateOutfitFields(outfit, outfitDto);
        outfit.setImageUrl(imageHandler.handleImageUpload(image, "outfit", outfitId, outfit.getImageUrl()));

        return mapEntity(save(outfit), outfitMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void deleteOutfit(Long profileId, Long outfitId) {
        logger.info("Deleting outfit with ID: {} for profile ID: {}", outfitId, profileId);

        Outfit outfit = findById(outfitId);
        validationService.validateOwnership(outfit.getProfile(), profileId, "outfit");

        imageHandler.handleImageDelete(outfit.getImageUrl());
        delete(outfitId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutfitResponseDto> getUserOutfits(Long profileId) {
        logger.info("Fetching all outfits for profile ID: {}", profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        Profile profile = profileService.getProfileEntityById(profileId);

        return mapEntityList(outfitRepository.findByProfileId(profile.getId()), outfitMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OutfitResponseDto getOutfit(Long outfitId) {
        logger.info("Fetching outfit with ID: {}", outfitId);
        return mapEntity(findById(outfitId), outfitMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Outfit getOutfitEntityById(Long outfitId) {
        logger.info("Fetching outfit entity with ID: {}", outfitId);
        return findById(outfitId);
    }

    @Override
    @Transactional
    public OutfitResponseDto addItemToOutfit(Long outfitId, Long itemId) {
        logger.info("Adding item with ID: {} to outfit with ID: {}", itemId, outfitId);

        validationService.validateNotNull(outfitId, "Outfit ID");
        validationService.validateNotNull(itemId, "Item ID");

        Outfit outfit = findById(outfitId);
        Item item = itemService.getItemEntity(itemId);

        validationService.validateOwnership(outfit.getProfile(), item.getProfile().getId(), "outfit");

        outfit.addOutfitItem(item);
        return mapEntity(save(outfit), outfitMapper::toResponseDto);
    }

    @Override
    @Transactional
    public OutfitResponseDto removeItemFromOutfit(Long outfitId, Long itemId) {
        logger.info("Removing item with ID: {} from outfit with ID: {}", itemId, outfitId);

        validationService.validateNotNull(outfitId, "Outfit ID");
        validationService.validateNotNull(itemId, "Item ID");

        Outfit outfit = findById(outfitId);
        Item item = itemService.getItemEntity(itemId);

        validationService.validateOwnership(outfit.getProfile(), item.getProfile().getId(), "outfit");

        outfit.removeOutfitItem(item);
        return mapEntity(save(outfit), outfitMapper::toResponseDto);
    }

    private Outfit buildOutfit(OutfitDto outfitDto, Profile profile) {
        return Outfit.builder()
                .profile(profile)
                .name(outfitDto.getName())
                .description(outfitDto.getDescription())
                .season(outfitDto.getSeason())
                .favorite(outfitDto.isFavorite())
                .isPublic(outfitDto.isPublic())
                .build();
    }

    private void updateOutfitFields(Outfit outfit, OutfitDto outfitDto) {
        outfit.setName(outfitDto.getName());
        outfit.setDescription(outfitDto.getDescription());
        outfit.setSeason(outfitDto.getSeason());
        outfit.setFavorite(outfitDto.isFavorite());
        outfit.setPublic(outfitDto.isPublic());
    }
}