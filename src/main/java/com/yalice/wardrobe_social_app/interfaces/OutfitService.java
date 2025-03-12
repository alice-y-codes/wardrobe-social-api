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
     * @param profileId    the ID of the profile creating the outfit
     * @param outfitDto the outfit data
     * @param image     the outfit image file (optional)
     * @return the created outfit
     */
    OutfitResponseDto createOutfit(Long profileId, OutfitDto outfitDto, MultipartFile image);

    /**
     * Updates an existing outfit.
     *
     * @param profileId    the ID of the profile updating the outfit
     * @param outfitId  the ID of the outfit to update
     * @param outfitDto the updated outfit data
     * @param image     the new outfit image file (optional)
     * @return the updated outfit
     */
    OutfitResponseDto updateOutfit(Long profileId, Long outfitId, OutfitDto outfitDto, MultipartFile image);

    /**
     * Deletes an outfit.
     *
     * @param profileId   the ID of the profile deleting the outfit
     * @param outfitId the ID of the outfit to delete
     */
    void deleteOutfit(Long profileId, Long outfitId);

    /**
     * Gets all outfits for a specific profile.
     *
     * @param profileId the ID of the profile
     * @return the list of outfits
     */
    List<OutfitResponseDto> getUserOutfits(Long profileId);

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
