package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link OutfitService} interface. Provides functionality to manage outfits for a user.
 * This includes creating, updating, deleting outfits, and adding/removing items to/from outfits.
 */
@Service
public class OutfitServiceImpl implements OutfitService {

    private final UserService userService;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final OutfitRepository outfitRepository;
    private final ItemRepository itemRepository;

    /**
     * Constructor for OutfitServiceImpl.
     *
     * @param outfitRepository the repository for outfit operations
     * @param userService the service for user-related operations
     * @param itemService the service for item-related operations
     * @param userRepository the repository for user operations
     * @param itemRepository the repository for item operations
     */
    @Autowired
    public OutfitServiceImpl(OutfitRepository outfitRepository, UserService userService,
                             ItemService itemService, UserRepository userRepository, ItemRepository itemRepository) {
        this.outfitRepository = outfitRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Creates a new outfit for a given user.
     *
     * @param userId the ID of the user creating the outfit
     * @param outfit the outfit to be created
     * @return an Optional containing the saved outfit if successful, otherwise empty
     */
    @Override
    @Transactional
    public Optional<Outfit> createOutfit(Long userId, Outfit outfit) {
        return userRepository.findById(userId)
                .map(user -> {
                    outfit.setUser(user);
                    outfit.setCreatedAt(LocalDateTime.now());
                    outfit.setUpdatedAt(LocalDateTime.now());
                    Outfit savedOutfit = outfitRepository.save(outfit);
                    return Optional.of(savedOutfit);
                }).orElse(Optional.empty());
    }

    /**
     * Retrieves an outfit by its ID.
     *
     * @param outfitId the ID of the outfit
     * @return an Optional containing the found outfit if present, otherwise empty
     */
    @Override
    public Optional<Outfit> getOutfit(Long outfitId) {
        return outfitRepository.findById(outfitId);
    }

    /**
     * Retrieves all outfits of a given user.
     *
     * @param userId the ID of the user whose outfits are to be fetched
     * @return a list of outfits for the specified user
     */
    @Override
    public List<Outfit> getAllOutfits(Long userId) {
        return outfitRepository.findByUserId(userId);
    }

    /**
     * Updates an existing outfit with new details.
     *
     * @param outfitId the ID of the outfit to be updated
     * @param outfit the new outfit details
     * @return the updated outfit if found and updated, otherwise null
     */
    @Override
    @Transactional
    public Outfit updateOutfit(Long outfitId, Outfit outfit) {
        return outfitRepository.findById(outfitId)
                .map(existingOutfit -> {
                    existingOutfit.setName(outfit.getName());
                    existingOutfit.setDescription(outfit.getDescription());
                    existingOutfit.setOccasion(outfit.getOccasion());
                    existingOutfit.setSeason(outfit.getSeason());
                    existingOutfit.setFavorite(outfit.isFavorite());
                    existingOutfit.setPublic(outfit.isPublic());
                    existingOutfit.setUpdatedAt(LocalDateTime.now());
                    return outfitRepository.save(existingOutfit);
                }).orElse(null);
    }

    /**
     * Deletes an outfit by its ID.
     *
     * @param outfitId the ID of the outfit to be deleted
     */
    @Override
    @Transactional
    public void deleteOutfit(Long outfitId) {
        if (outfitRepository.existsById(outfitId)) {
            outfitRepository.deleteById(outfitId);
        }
    }

    /**
     * Adds an item to a specified outfit.
     *
     * @param outfitId the ID of the outfit to which the item should be added
     * @param itemId the ID of the item to be added
     * @return an Optional containing the updated outfit if successful, otherwise empty
     */
    @Override
    @Transactional
    public Optional<Outfit> addItemToOutfit(Long outfitId, Long itemId) {
        // Find the outfit
        Optional<Outfit> outfitOptional = outfitRepository.findById(outfitId);
        if (outfitOptional.isEmpty()) {
            return Optional.empty();
        }

        // Fetch the item entity directly from the repository
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            return Optional.empty(); // Item not found
        }

        // Add the item to the outfit
        Outfit outfit = outfitOptional.get();
        outfit.addItem(itemOptional.get());
        outfit.setUpdatedAt(LocalDateTime.now());

        // Save and return the updated outfit
        Outfit updatedOutfit = outfitRepository.save(outfit);
        return Optional.of(updatedOutfit);
    }

    /**
     * Removes an item from a specified outfit.
     *
     * @param outfitId the ID of the outfit from which the item should be removed
     * @param itemId the ID of the item to be removed
     * @return an Optional containing the updated outfit if successful, otherwise empty
     */
    @Override
    @Transactional
    public Optional<Outfit> removeItemFromOutfit(Long outfitId, Long itemId) {
        // Find the outfit
        Optional<Outfit> outfitOptional = outfitRepository.findById(outfitId);
        if (outfitOptional.isEmpty()) {
            return Optional.empty();
        }

        // Fetch the item entity directly from the repository
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            return Optional.empty(); // Item not found
        }

        // Remove the item from the outfit
        Outfit outfit = outfitOptional.get();
        outfit.removeItem(itemOptional.get());
        outfit.setUpdatedAt(LocalDateTime.now());

        // Save and return the updated outfit
        Outfit updatedOutfit = outfitRepository.save(outfit);
        return Optional.of(updatedOutfit);
    }

    /**
     * Retrieves all outfits of a given user for a specific occasion.
     *
     * @param userId the ID of the user whose outfits are to be fetched
     * @param occasion the occasion filter
     * @return a list of outfits matching the occasion for the specified user
     */
    @Override
    public List<Outfit> getOutfitsByOccasion(Long userId, String occasion) {
        return outfitRepository.findByUserIdAndOccasion(userId, occasion);
    }
}
