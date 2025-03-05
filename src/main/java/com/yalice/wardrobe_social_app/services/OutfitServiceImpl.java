package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OutfitServiceImpl extends BaseService implements OutfitService {

    private final UserSearchService userSearchService;
    private final ItemService itemService;
    private final OutfitRepository outfitRepository;

    public OutfitServiceImpl(OutfitRepository outfitRepository, UserSearchService userSearchService, ItemService itemService) {
        this.outfitRepository = outfitRepository;
        this.userSearchService = userSearchService;
        this.itemService = itemService;
    }

    @Override
    @Transactional
    public OutfitResponseDto createOutfit(Long userId, OutfitDto outfitDto) {
        logger.info("Attempting to create outfit for user ID: {}", userId);

        User user = userSearchService.getUserEntityById(userId);
        Outfit outfit = new Outfit();
        outfit.setUser(user);
        outfit.setName(outfitDto.getName());
        outfit.setDescription(outfitDto.getDescription());
        outfit.setSeason(outfitDto.getSeason());
        outfit.setFavorite(outfitDto.isFavorite());
        outfit.setPublic(outfitDto.isPublic());

        Outfit savedOutfit = outfitRepository.save(outfit);
        logger.info("Outfit '{}' created successfully for user '{}'.", outfit.getName(), user.getUsername());

        return convertToOutfitResponseDto(savedOutfit);
    }

    @Override
    public OutfitResponseDto getOutfit(Long outfitId) {
        logger.info("Fetching outfit with ID: {}", outfitId);

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));

        return convertToOutfitResponseDto(outfit);
    }

    @Override
    public Outfit getOutfitEntityById(Long outfitId) {
        logger.info("Fetching outfit with ID: {}", outfitId);

        return outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));
    }


    @Override
    public List<OutfitResponseDto> getAllOutfits(Long userId) {
        logger.info("Fetching all outfits for user ID: {}", userId);

        List<Outfit> outfits = outfitRepository.findByUserId(userId);
        return outfits.stream()
                .map(this::convertToOutfitResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OutfitResponseDto updateOutfit(Long outfitId, OutfitDto outfitDto) {
        logger.info("Attempting to update outfit with ID: {}", outfitId);

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));

        outfit.setName(outfitDto.getName());
        outfit.setDescription(outfitDto.getDescription());
        outfit.setSeason(outfitDto.getSeason());
        outfit.setFavorite(outfitDto.isFavorite());
        outfit.setPublic(outfitDto.isPublic());

        Outfit updatedOutfit = outfitRepository.save(outfit);
        logger.info("Outfit '{}' updated successfully.", updatedOutfit.getName());

        return convertToOutfitResponseDto(updatedOutfit);
    }

    @Override
    @Transactional
    public void deleteOutfit(Long outfitId) {
        logger.info("Attempting to delete outfit with ID: {}", outfitId);

        if (!outfitRepository.existsById(outfitId)) {
            logger.warn("Outfit with ID '{}' not found for deletion.", outfitId);
            throw new EntityNotFoundException("Outfit not found with ID: " + outfitId);
        }

        outfitRepository.deleteById(outfitId);
        logger.info("Outfit with ID '{}' deleted successfully.", outfitId);
    }

    @Override
    @Transactional
    public OutfitResponseDto addItemToOutfit(Long outfitId, Long itemId) {
        logger.info("Attempting to add item with ID: {} to outfit with ID: {}", itemId, outfitId);

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));

        Item item = itemService.getItemEntity(itemId);
        outfit.addItem(item);
        Outfit updatedOutfit = outfitRepository.save(outfit);

        logger.info("Item '{}' added to outfit '{}'.", item.getName(), outfit.getName());

        return convertToOutfitResponseDto(updatedOutfit);
    }

    @Override
    @Transactional
    public OutfitResponseDto removeItemFromOutfit(Long outfitId, Long itemId) {
        logger.info("Attempting to remove item with ID: {} from outfit with ID: {}", itemId, outfitId);

        Outfit outfit = outfitRepository.findById(outfitId)
                .orElseThrow(() -> new EntityNotFoundException("Outfit not found with ID: " + outfitId));

        Item item = itemService.getItemEntity(itemId);
        outfit.removeItem(item);
        Outfit updatedOutfit = outfitRepository.save(outfit);

        logger.info("Item '{}' removed from outfit '{}'.", item.getName(), outfit.getName());

        return convertToOutfitResponseDto(updatedOutfit);
    }

}
