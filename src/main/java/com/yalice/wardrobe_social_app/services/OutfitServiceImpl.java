package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
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
    private final DtoConversionService dtoConversionService;

    public OutfitServiceImpl(OutfitRepository outfitRepository, ProfileRepository profileRepository,
                             ItemService itemService, DtoConversionService dtoConversionService) {
        this.outfitRepository = outfitRepository;
        this.profileRepository = profileRepository;
        this.itemService = itemService;
        this.dtoConversionService = dtoConversionService;
    }

    @Override
    @Transactional
    public OutfitResponseDto createOutfit(Long userId, OutfitDto outfitDto, MultipartFile image) {
        logger.info("Attempting to create outfit for user ID: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.warn("Profile not found with user ID: {}", userId);
                    return new EntityNotFoundException("Profile not found with user ID: " + userId);
                });

        Outfit outfit = new Outfit();
        outfit.setProfile(profile);
        outfit.setName(outfitDto.getName());
        outfit.setDescription(outfitDto.getDescription());
        outfit.setSeason(outfitDto.getSeason());
        outfit.setFavorite(outfitDto.isFavorite());
        outfit.setPublic(outfitDto.isPublic());

        if (image != null && !image.isEmpty()) {
            // TODO: Implement image upload logic
            outfit.setImageUrl("placeholder_url");
        }

        Outfit savedOutfit = outfitRepository.save(outfit);
        logger.info("Outfit '{}' created successfully for user '{}'.", outfit.getName(),
                profile.getUser().getUsername());

        return dtoConversionService.convertToOutfitResponseDto(savedOutfit);
    }

    @Override
    @Transactional
    public OutfitResponseDto updateOutfit(Long userId, Long outfitId, OutfitDto outfitDto, MultipartFile image) {
        logger.info("Attempting to update outfit with ID: {} for user ID: {}", outfitId, userId);

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));

        if (!outfit.getProfile().getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("User does not own this outfit");
        }

        outfit.setName(outfitDto.getName());
        outfit.setDescription(outfitDto.getDescription());
        outfit.setSeason(outfitDto.getSeason());
        outfit.setFavorite(outfitDto.isFavorite());
        outfit.setPublic(outfitDto.isPublic());

        if (image != null && !image.isEmpty()) {
            // TODO: Implement image upload logic
            outfit.setImageUrl("placeholder_url");
        }

        Outfit updatedOutfit = outfitRepository.save(outfit);
        logger.info("Outfit '{}' updated successfully.", updatedOutfit.getName());

        return dtoConversionService.convertToOutfitResponseDto(updatedOutfit);
    }

    @Override
    @Transactional
    public void deleteOutfit(Long userId, Long outfitId) {
        logger.info("Attempting to delete outfit with ID: {} for user ID: {}", outfitId, userId);

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));

        if (!outfit.getProfile().getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("User does not own this outfit");
        }

        outfitRepository.deleteById(outfitId);
        logger.info("Outfit with ID '{}' deleted successfully.", outfitId);
    }

    @Override
    public List<OutfitResponseDto> getUserOutfits(Long userId) {
        logger.info("Fetching all outfits for user ID: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with user ID: " + userId));

        List<Outfit> outfits = outfitRepository.findByProfileId(profile.getId());
        return outfits.stream()
                .map(dtoConversionService::convertToOutfitResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OutfitResponseDto getOutfit(Long outfitId) {
        logger.info("Fetching outfit with ID: {}", outfitId);

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));

        return dtoConversionService.convertToOutfitResponseDto(outfit);
    }

    @Override
    public Outfit getOutfitEntityById(Long outfitId) {
        logger.info("Fetching outfit entity with ID: {}", outfitId);

        return outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));
    }

    @Override
    @Transactional
    public OutfitResponseDto addItemToOutfit(Long outfitId, Long itemId) {
        logger.info("Attempting to add item with ID: {} to outfit with ID: {}", itemId, outfitId);

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));

        Item item = itemService.getItemEntity(itemId);
        outfit.addOutfitItem(item);
        Outfit updatedOutfit = outfitRepository.save(outfit);

        logger.info("Item '{}' added to outfit '{}'.", item.getName(), outfit.getName());

        return dtoConversionService.convertToOutfitResponseDto(updatedOutfit);
    }

    @Override
    @Transactional
    public OutfitResponseDto removeItemFromOutfit(Long outfitId, Long itemId) {
        logger.info("Attempting to remove item with ID: {} from outfit with ID: {}", itemId, outfitId);

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));

        Item item = itemService.getItemEntity(itemId);
        outfit.removeOutfitItem(item);
        Outfit updatedOutfit = outfitRepository.save(outfit);

        logger.info("Item '{}' removed from outfit '{}'.", item.getName(), outfit.getName());

        return dtoConversionService.convertToOutfitResponseDto(updatedOutfit);
    }
}
