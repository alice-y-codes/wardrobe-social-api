package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Outfit;

import java.util.List;
import java.util.Optional;

public interface OutfitService {

    /**
     * Create a new outfit for a user
     * 
     * @param userId the ID of the user creating the outfit
     * @param outfit the outfit to create
     * @return the created outfit wrapped in an Optional, or empty if creation
     *         failed
     */
    Optional<Outfit> createOutfit(Long userId, Outfit outfit);

    /**
     * Get all outfits for a user
     * 
     * @param userId the ID of the user
     * @return a list of outfits belonging to the user
     */
    List<Outfit> getAllOutfits(Long userId);

    /**
     * Get an outfit by ID
     * 
     * @param outfitId the ID of the outfit to retrieve
     * @return the outfit wrapped in an Optional, or empty if not found
     */
    Optional<Outfit> getOutfit(Long outfitId);

    /**
     * Update an outfit
     * 
     * @param outfitId the ID of the outfit to update
     * @param outfit   the updated outfit data
     * @return the updated outfit, or null if update failed
     */
    Outfit updateOutfit(Long outfitId, Outfit outfit);

    /**
     * Delete an outfit
     * 
     * @param outfitId the ID of the outfit to delete
     */
    void deleteOutfit(Long outfitId);

    /**
     * Add an item to an outfit
     * 
     * @param outfitId the ID of the outfit
     * @param itemId   the ID of the item to add
     * @return the updated outfit wrapped in an Optional, or empty if the operation
     *         failed
     */
    Optional<Outfit> addItemToOutfit(Long outfitId, Long itemId);

    /**
     * Remove an item from an outfit
     * 
     * @param outfitId the ID of the outfit
     * @param itemId   the ID of the item to remove
     * @return the updated outfit wrapped in an Optional, or empty if the operation
     *         failed
     */
    Optional<Outfit> removeItemFromOutfit(Long outfitId, Long itemId);

    /**
     * Get outfits by occasion for a user
     * 
     * @param userId   the ID of the user
     * @param occasion the occasion to filter by
     * @return a list of outfits matching the occasion
     */
    List<Outfit> getOutfitsByOccasion(Long userId, String occasion);
}
