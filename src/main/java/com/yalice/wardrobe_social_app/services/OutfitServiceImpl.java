package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.interfaces.ImageService;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.mappers.OutfitMapper;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OutfitServiceImpl extends BaseService implements OutfitService {

    private final ProfileRepository profileRepository;
    private final ItemService itemService;
    private final OutfitRepository outfitRepository;
    private final OutfitMapper outfitMapper;
    private final ImageService imageService;

    public OutfitServiceImpl(OutfitRepository outfitRepository, ProfileRepository profileRepository,
            ItemService itemService, OutfitMapper outfitMapper,
            ImageService imageService) {
        this.outfitRepository = outfitRepository;
        this.profileRepository = profileRepository;
        this.itemService = itemService;
        this.outfitMapper = outfitMapper;
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public OutfitResponseDto createOutfit(Long profileId, OutfitDto outfitDto, MultipartFile image) {
        logger.info("Creating outfit for profile ID: {}", profileId);

        Profile profile = findProfileById(profileId);

        Outfit outfit = buildOutfit(outfitDto, profile, null);
        outfit = outfitRepository.save(outfit);

        // Upload image after outfit is saved to get the ID
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageService.uploadImage(image, "outfit", outfit.getId());
            outfit.setImageUrl(imageUrl);
            outfit = outfitRepository.save(outfit);
        }

        logger.info("Outfit '{}' successfully created for profile '{}'.", outfit.getName(),
                profile.getUser().getUsername());

        return outfitMapper.toResponseDto(outfit);
    }

    @Override
    @Transactional
    public OutfitResponseDto updateOutfit(Long profileId, Long outfitId, OutfitDto outfitDto, MultipartFile image) {
        logger.info("Updating outfit with ID: {} for profile ID: {}", outfitId, profileId);

        Outfit outfit = findOutfitByIdAndProfile(outfitId, profileId);

        // Delete old image if a new one is provided
        if (image != null && !image.isEmpty() && outfit.getImageUrl() != null) {
            imageService.deleteImage(outfit.getImageUrl());
        }

        updateOutfitFields(outfit, outfitDto);

        // Upload new image if provided
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageService.uploadImage(image, "outfit", outfit.getId());
            outfit.setImageUrl(imageUrl);
        }

        Outfit updatedOutfit = outfitRepository.save(outfit);

        logger.info("Outfit '{}' successfully updated.", updatedOutfit.getName());

        return outfitMapper.toResponseDto(updatedOutfit);
    }

    @Override
    @Transactional
    public void deleteOutfit(Long profileId, Long outfitId) {
        logger.info("Deleting outfit with ID: {} for profile ID: {}", outfitId, profileId);

        Outfit outfit = findOutfitByIdAndProfile(outfitId, profileId);

        // Delete associated image if it exists
        if (outfit.getImageUrl() != null) {
            imageService.deleteImage(outfit.getImageUrl());
        }

        outfitRepository.deleteById(outfitId);

        logger.info("Outfit with ID '{}' successfully deleted.", outfitId);
    }

    @Override
    public List<OutfitResponseDto> getUserOutfits(Long profileId) {
        logger.info("Fetching all outfits for profile ID: {}", profileId);

        Profile profile = findProfileById(profileId);

        List<Outfit> outfits = outfitRepository.findByProfileId(profile.getId());

        return outfits.stream()
                .map(outfitMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OutfitResponseDto getOutfit(Long outfitId) {
        logger.info("Fetching outfit with ID: {}", outfitId);

        Outfit outfit = findOutfitById(outfitId);

        return outfitMapper.toResponseDto(outfit);
    }

    @Override
    public Outfit getOutfitEntityById(Long outfitId) {
        logger.info("Fetching outfit entity with ID: {}", outfitId);

        return findOutfitById(outfitId);
    }

    @Override
    @Transactional
    public OutfitResponseDto addItemToOutfit(Long outfitId, Long itemId) {
        logger.info("Adding item with ID: {} to outfit with ID: {}", itemId, outfitId);

        Outfit outfit = findOutfitById(outfitId);

        Item item = itemService.getItemEntity(itemId);
        outfit.addOutfitItem(item);

        Outfit updatedOutfit = outfitRepository.save(outfit);

        logger.info("Item '{}' successfully added to outfit '{}'.", item.getName(), outfit.getName());

        return outfitMapper.toResponseDto(updatedOutfit);
    }

    @Override
    @Transactional
    public OutfitResponseDto removeItemFromOutfit(Long outfitId, Long itemId) {
        logger.info("Removing item with ID: {} from outfit with ID: {}", itemId, outfitId);

        Outfit outfit = findOutfitById(outfitId);

        Item item = itemService.getItemEntity(itemId);
        outfit.removeOutfitItem(item);

        Outfit updatedOutfit = outfitRepository.save(outfit);

        logger.info("Item '{}' successfully removed from outfit '{}'.", item.getName(), outfit.getName());

        return outfitMapper.toResponseDto(updatedOutfit);
    }

    private Profile findProfileById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> {
                    logger.warn("Profile not found with ID: {}", profileId);
                    return new EntityNotFoundException("Profile not found with ID: " + profileId);
                });
    }

    private Outfit findOutfitById(Long outfitId) {
        return outfitRepository.findById(outfitId)
                .orElseThrow(() -> {
                    logger.warn("Outfit not found with ID: {}", outfitId);
                    return new EntityNotFoundException("Outfit not found with ID: " + outfitId);
                });
    }

    private Outfit findOutfitByIdAndProfile(Long outfitId, Long profileId) {
        Outfit outfit = findOutfitById(outfitId);

        if (!outfit.getProfile().getId().equals(profileId)) {
            throw new EntityNotFoundException("Profile does not own this outfit");
        }

        return outfit;
    }

    private Outfit buildOutfit(OutfitDto outfitDto, Profile profile, MultipartFile image) {
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