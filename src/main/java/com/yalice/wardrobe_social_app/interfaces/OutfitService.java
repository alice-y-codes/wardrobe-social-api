package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.Outfit;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing outfits.
 */
public interface OutfitService {
    /**
     * Creates a new outfit.
     *
     * @param userId    the ID of the user creating the outfit
     * @param outfitDto the outfit data
     * @param image     the outfit image file (optional)
     * @return the created outfit
     */
    OutfitResponseDto createOutfit(Long userId, OutfitDto outfitDto, MultipartFile image);

    /**
     * Updates an existing outfit.
     *
     * @param userId    the ID of the user updating the outfit
     * @param outfitId  the ID of the outfit to update
     * @param outfitDto the updated outfit data
     * @param image     the new outfit image file (optional)
     * @return the updated outfit
     */
    OutfitResponseDto updateOutfit(Long userId, Long outfitId, OutfitDto outfitDto, MultipartFile image);

    /**
     * Deletes an outfit.
     *
     * @param userId   the ID of the user deleting the outfit
     * @param outfitId the ID of the outfit to delete
     */
    void deleteOutfit(Long userId, Long outfitId);

    /**
     * Gets all outfits for a specific user.
     *
     * @param userId the ID of the user
     * @return the list of outfits
     */
    List<OutfitResponseDto> getUserOutfits(Long userId);

    /**
     * Gets a specific outfit by ID.
     *
     * @param outfitId the ID of the outfit to retrieve
     * @return the outfit
     */
    OutfitResponseDto getOutfit(Long outfitId);

    /**
     * Gets the Outfit entity by ID.
     *
     * @param outfitId the ID of the outfit to retrieve
     * @return the Outfit entity
     */
    Outfit getOutfitEntityById(Long outfitId);

    /**
     * Add an item to an outfit.
     *
     * @param outfitId the ID of the outfit
     * @param itemId   the ID of the item to add
     * @return the updated outfit response DTO
     */
    OutfitResponseDto addItemToOutfit(Long outfitId, Long itemId);

    /**
     * Remove an item from an outfit.
     *
     * @param outfitId the ID of the outfit
     * @param itemId   the ID of the item to remove
     * @return the updated outfit response DTO
     */
    OutfitResponseDto removeItemFromOutfit(Long outfitId, Long itemId);
}
