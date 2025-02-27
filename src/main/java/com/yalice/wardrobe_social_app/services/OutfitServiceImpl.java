package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OutfitServiceImpl implements OutfitService {

    private final OutfitRepository outfitRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final UserRepository userRepository;

    @Autowired
    public OutfitServiceImpl(OutfitRepository outfitRepository, UserService userService,
            ItemService itemService, UserRepository userRepository) {
        this.outfitRepository = outfitRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Optional<Outfit> createOutfit(Long userId, Outfit outfit) {
        // Find the user by ID directly from the repository
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        // Set the user and timestamps
        outfit.setUser(userOptional.get());
        outfit.setCreatedAt(LocalDateTime.now());
        outfit.setUpdatedAt(LocalDateTime.now());

        // Save and return the outfit
        Outfit savedOutfit = outfitRepository.save(outfit);
        return Optional.of(savedOutfit);
    }

    @Override
    public Optional<Outfit> getOutfit(Long outfitId) {
        return outfitRepository.findById(outfitId);
    }

    @Override
    public List<Outfit> getAllOutfits(Long userId) {
        return outfitRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Outfit updateOutfit(Long outfitId, Outfit outfit) {
        Optional<Outfit> existingOutfitOptional = outfitRepository.findById(outfitId);
        if (existingOutfitOptional.isEmpty()) {
            return null;
        }

        Outfit existingOutfit = existingOutfitOptional.get();

        // Update fields
        existingOutfit.setName(outfit.getName());
        existingOutfit.setDescription(outfit.getDescription());
        existingOutfit.setOccasion(outfit.getOccasion());
        existingOutfit.setSeason(outfit.getSeason());
        existingOutfit.setFavorite(outfit.isFavorite());
        existingOutfit.setPublic(outfit.isPublic());
        existingOutfit.setUpdatedAt(LocalDateTime.now());

        // Save and return the updated outfit
        return outfitRepository.save(existingOutfit);
    }

    @Override
    @Transactional
    public void deleteOutfit(Long outfitId) {
        Optional<Outfit> outfitOptional = outfitRepository.findById(outfitId);
        outfitOptional.ifPresent(outfitRepository::delete);
    }

    @Override
    @Transactional
    public Optional<Outfit> addItemToOutfit(Long outfitId, Long itemId) {
        // Find the outfit
        Optional<Outfit> outfitOptional = outfitRepository.findById(outfitId);
        if (outfitOptional.isEmpty()) {
            return Optional.empty();
        }

        // Find the item
        Optional<Item> itemOptional = itemService.getItem(itemId);
        if (itemOptional.isEmpty()) {
            return Optional.empty();
        }

        // Add the item to the outfit
        Outfit outfit = outfitOptional.get();
        outfit.addItem(itemOptional.get());
        outfit.setUpdatedAt(LocalDateTime.now());

        // Save and return the updated outfit
        Outfit updatedOutfit = outfitRepository.save(outfit);
        return Optional.of(updatedOutfit);
    }

    @Override
    @Transactional
    public Optional<Outfit> removeItemFromOutfit(Long outfitId, Long itemId) {
        // Find the outfit
        Optional<Outfit> outfitOptional = outfitRepository.findById(outfitId);
        if (outfitOptional.isEmpty()) {
            return Optional.empty();
        }

        // Find the item
        Optional<Item> itemOptional = itemService.getItem(itemId);
        if (itemOptional.isEmpty()) {
            return Optional.empty();
        }

        // Remove the item from the outfit
        Outfit outfit = outfitOptional.get();
        outfit.removeItem(itemOptional.get());
        outfit.setUpdatedAt(LocalDateTime.now());

        // Save and return the updated outfit
        Outfit updatedOutfit = outfitRepository.save(outfit);
        return Optional.of(updatedOutfit);
    }

    @Override
    public List<Outfit> getOutfitsByOccasion(Long userId, String occasion) {
        return outfitRepository.findByUserIdAndOccasion(userId, occasion);
    }
}
