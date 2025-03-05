package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/outfits")
public class OutfitController {

    private final OutfitService outfitService;
    private final UserSearchService userSearchService;

    @Autowired
    public OutfitController(OutfitService outfitService, UserSearchService userSearchService) {
        this.outfitService = outfitService;
        this.userSearchService = userSearchService;
    }

    /**
     * Create a new outfit
     */
    @PostMapping
    public ResponseEntity<Outfit> createOutfit(@RequestBody Outfit outfit) {
        Optional<User> userOptional = getCurrentUser();

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = userOptional.get().getId();
        Optional<Outfit> createdOutfit = outfitService.createOutfit(userId, outfit);

        return createdOutfit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    /**
     * Get all outfits for the current user
     */
    @GetMapping("/my-outfits")
    public ResponseEntity<List<Outfit>> getMyOutfits() {
        Optional<User> userOptional = getCurrentUser();

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = userOptional.get().getId();
        List<Outfit> outfits = outfitService.getAllOutfits(userId);

        return ResponseEntity.ok(outfits);
    }

    /**
     * Get all outfits for a specific user
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Outfit>> getUserOutfits(@PathVariable Long userId) {
        List<Outfit> outfits = outfitService.getAllOutfits(userId);
        return ResponseEntity.ok(outfits);
    }

    /**
     * Get an outfit by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Outfit> getOutfitById(@PathVariable Long id) {
        Optional<Outfit> outfit = outfitService.getOutfit(id);
        return outfit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update an outfit
     */
    @PutMapping("/{id}")
    public ResponseEntity<Outfit> updateOutfit(@PathVariable Long id, @RequestBody Outfit outfit) {
        // Verify the current user owns this outfit
        Optional<User> userOptional = getCurrentUser();
        Optional<Outfit> existingOutfitOptional = outfitService.getOutfit(id);

        if (userOptional.isEmpty() || existingOutfitOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if the outfit belongs to the current user
        if (!existingOutfitOptional.get().getUser().getId().equals(userOptional.get().getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        Outfit updatedOutfit = outfitService.updateOutfit(id, outfit);
        return updatedOutfit != null ? ResponseEntity.ok(updatedOutfit) : ResponseEntity.notFound().build();
    }

    /**
     * Delete an outfit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOutfit(@PathVariable Long id) {
        // Verify the current user owns this outfit
        Optional<User> userOptional = getCurrentUser();
        Optional<Outfit> existingOutfitOptional = outfitService.getOutfit(id);

        if (userOptional.isEmpty() || existingOutfitOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if the outfit belongs to the current user
        if (!existingOutfitOptional.get().getUser().getId().equals(userOptional.get().getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        outfitService.deleteOutfit(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add an item to an outfit
     */
    @PostMapping("/{outfitId}/items/{itemId}")
    public ResponseEntity<Outfit> addItemToOutfit(@PathVariable Long outfitId, @PathVariable Long itemId) {
        // Verify the current user owns this outfit
        Optional<User> userOptional = getCurrentUser();
        Optional<Outfit> existingOutfitOptional = outfitService.getOutfit(outfitId);

        if (userOptional.isEmpty() || existingOutfitOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if the outfit belongs to the current user
        if (!existingOutfitOptional.get().getUser().getId().equals(userOptional.get().getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        Optional<Outfit> updatedOutfit = outfitService.addItemToOutfit(outfitId, itemId);
        return updatedOutfit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Remove an item from an outfit
     */
    @DeleteMapping("/{outfitId}/items/{itemId}")
    public ResponseEntity<Outfit> removeItemFromOutfit(@PathVariable Long outfitId, @PathVariable Long itemId) {
        // Verify the current user owns this outfit
        Optional<User> userOptional = getCurrentUser();
        Optional<Outfit> existingOutfitOptional = outfitService.getOutfit(outfitId);

        if (userOptional.isEmpty() || existingOutfitOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if the outfit belongs to the current user
        if (!existingOutfitOptional.get().getUser().getId().equals(userOptional.get().getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        Optional<Outfit> updatedOutfit = outfitService.removeItemFromOutfit(outfitId, itemId);
        return updatedOutfit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get outfits by occasion
     */
    @GetMapping("/occasion/{occasion}")
    public ResponseEntity<List<Outfit>> getOutfitsByOccasion(@PathVariable String occasion) {
        Optional<User> userOptional = getCurrentUser();

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = userOptional.get().getId();
        List<Outfit> outfits = outfitService.getOutfitsByOccasion(userId, occasion);

        return ResponseEntity.ok(outfits);
    }

    /**
     * Utility method to get the current authenticated user
     */
    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = authentication.getName();
        return userSearchService.findUserByUsername(username);
    }
}