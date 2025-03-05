package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;

import java.util.List;

public interface OutfitService {

    /**
     * Create a new outfit for a user.
     *
     * @param userId the ID of the user creating the outfit
     * @param outfitDto the outfit data to create
     * @return the created outfit as a DTO
     */
    OutfitResponseDto createOutfit(Long userId, OutfitDto outfitDto);

    /**
     * Get all outfits for a user.
     *
     * @param userId the ID of the user
     * @return a list of outfit response DTOs
     */
    List<OutfitResponseDto> getAllOutfits(Long userId);

    /**
     * Get an outfit by ID.
     *
     * @param outfitId the ID of the outfit to retrieve
     * @return the outfit response DTO
     */
    OutfitResponseDto getOutfit(Long outfitId);

    /**
     * Update an outfit.
     *
     * @param outfitId the ID of the outfit to update
     * @param outfitDto the updated outfit data
     * @return the updated outfit as a DTO
     */
    OutfitResponseDto updateOutfit(Long outfitId, OutfitDto outfitDto);

    /**
     * Delete an outfit.
     *
     * @param outfitId the ID of the outfit to delete
     */
    void deleteOutfit(Long outfitId);

    /**
     * Add an item to an outfit.
     *
     * @param outfitId the ID of the outfit
     * @param itemId the ID of the item to add
     * @return the updated outfit response DTO
     */
    OutfitResponseDto addItemToOutfit(Long outfitId, Long itemId);

    /**
     * Remove an item from an outfit.
     *
     * @param outfitId the ID of the outfit
     * @param itemId the ID of the item to remove
     * @return the updated outfit response DTO
     */
    OutfitResponseDto removeItemFromOutfit(Long outfitId, Long itemId);
}
