package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for handling outfit-related operations.
 * Provides endpoints for creating, updating, and managing outfits.
 */
@RestController
@RequestMapping("/api/outfits")
public class OutfitController extends ApiBaseController {

    private final OutfitService outfitService;

    @Autowired
    public OutfitController(OutfitService outfitService, AuthUtils authUtils) {
        super(authUtils);
        this.outfitService = outfitService;
    }

    /**
     * Creates a new outfit.
     *
     * @param outfitDto the outfit data
     * @param image     the outfit image file
     * @return ResponseEntity containing the created outfit
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OutfitResponseDto>> createOutfit(
            @RequestPart("outfit") OutfitDto outfitDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Attempting to create new outfit for user");

        User currentUser = getLoggedInUser();
        try {
            OutfitResponseDto createdOutfit = outfitService.createOutfit(currentUser.getId(), outfitDto, image);
            logger.info("Successfully created outfit with ID: {} for user ID: {}", createdOutfit.getId(),
                    currentUser.getId());
            return createSuccessResponse("Outfit created successfully", createdOutfit);
        } catch (Exception e) {
            logger.error("Failed to create outfit for user ID: {}", currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to create outfit");
        }
    }

    /**
     * Updates an existing outfit.
     *
     * @param outfitId  the ID of the outfit to update
     * @param outfitDto the updated outfit data
     * @param image     the new outfit image file (optional)
     * @return ResponseEntity containing the updated outfit
     */
    @PutMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> updateOutfit(
            @PathVariable Long outfitId,
            @RequestPart("outfit") OutfitDto outfitDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Attempting to update outfit with ID: {}", outfitId);

        User currentUser = getLoggedInUser();
        try {
            OutfitResponseDto updatedOutfit = outfitService.updateOutfit(currentUser.getId(), outfitId, outfitDto,
                    image);
            logger.info("Successfully updated outfit with ID: {} for user ID: {}", outfitId, currentUser.getId());
            return createSuccessResponse("Outfit updated successfully", updatedOutfit);
        } catch (Exception e) {
            logger.error("Failed to update outfit with ID: {} for user ID: {}", outfitId, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to update outfit");
        }
    }

    /**
     * Deletes an outfit.
     *
     * @param outfitId the ID of the outfit to delete
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<Void>> deleteOutfit(@PathVariable Long outfitId) {
        logger.info("Attempting to delete outfit with ID: {}", outfitId);

        User currentUser = getLoggedInUser();
        try {
            outfitService.deleteOutfit(currentUser.getId(), outfitId);
            logger.info("Successfully deleted outfit with ID: {} for user ID: {}", outfitId, currentUser.getId());
            return createSuccessResponse("Outfit deleted successfully", null);
        } catch (Exception e) {
            logger.error("Failed to delete outfit with ID: {} for user ID: {}", outfitId, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to delete outfit");
        }
    }

    /**
     * Gets all outfits for the current user.
     *
     * @return ResponseEntity containing the list of outfits
     */
    @GetMapping("/my-outfits")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getMyOutfits() {
        logger.info("Retrieving outfits for current user");

        User currentUser = getLoggedInUser();
        try {
            List<OutfitResponseDto> outfits = outfitService.getUserOutfits(currentUser.getId());
            logger.info("Successfully retrieved {} outfits for user ID: {}", outfits.size(), currentUser.getId());
            return createSuccessResponse("Outfits retrieved successfully", outfits);
        } catch (Exception e) {
            logger.error("Failed to retrieve outfits for user ID: {}", currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to retrieve outfits");
        }
    }

    /**
     * Gets a specific outfit by ID.
     *
     * @param outfitId the ID of the outfit to retrieve
     * @return ResponseEntity containing the outfit
     */
    @GetMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> getOutfit(@PathVariable Long outfitId) {
        logger.info("Retrieving outfit with ID: {}", outfitId);

        try {
            OutfitResponseDto outfit = outfitService.getOutfit(outfitId);
            logger.info("Successfully retrieved outfit with ID: {}", outfitId);
            return createSuccessResponse("Outfit retrieved successfully", outfit);
        } catch (Exception e) {
            logger.error("Failed to retrieve outfit with ID: {}", outfitId, e);
            return createNotFoundResponse("Outfit not found with ID: " + outfitId);
        }
    }

    /**
     * Gets all outfits for a specific user.
     *
     * @param userId the ID of the user
     * @return ResponseEntity containing the list of outfits
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getUserOutfits(@PathVariable Long userId) {
        logger.info("Retrieving outfits for user ID: {}", userId);

        try {
            List<OutfitResponseDto> outfits = outfitService.getUserOutfits(userId);
            logger.info("Successfully retrieved {} outfits for user ID: {}", outfits.size(), userId);
            return createSuccessResponse("User outfits retrieved successfully", outfits);
        } catch (Exception e) {
            logger.error("Failed to retrieve outfits for user ID: {}", userId, e);
            return createInternalServerErrorResponse("Failed to retrieve outfits");
        }
    }

    /**
     * Adds an item to an outfit.
     *
     * @param outfitId the ID of the outfit
     * @param itemId   the ID of the item to add
     * @return ResponseEntity containing the updated outfit
     */
    @PostMapping("/{outfitId}/items/{itemId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> addItemToOutfit(
            @PathVariable Long outfitId,
            @PathVariable Long itemId) {
        if (!isUserOwnerOfOutfit(outfitId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "You don't have permission to modify this outfit", null));
        }

        OutfitResponseDto updatedOutfit = outfitService.addItemToOutfit(outfitId, itemId);
        return updatedOutfit != null
                ? ResponseEntity.ok(new ApiResponse<>(true, "Item added to outfit successfully", updatedOutfit))
                : ResponseEntity.notFound().build();
    }

    /**
     * Removes an item from an outfit.
     *
     * @param outfitId the ID of the outfit
     * @param itemId   the ID of the item to remove
     * @return ResponseEntity containing the updated outfit
     */
    @DeleteMapping("/{outfitId}/items/{itemId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> removeItemFromOutfit(
            @PathVariable Long outfitId,
            @PathVariable Long itemId) {
        if (!isUserOwnerOfOutfit(outfitId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "You don't have permission to modify this outfit", null));
        }

        OutfitResponseDto updatedOutfit = outfitService.removeItemFromOutfit(outfitId, itemId);
        return updatedOutfit != null
                ? ResponseEntity.ok(new ApiResponse<>(true, "Item removed from outfit successfully", updatedOutfit))
                : ResponseEntity.notFound().build();
    }

    /**
     * Gets outfits by season.
     *
     * @param season the season to filter by
     * @return ResponseEntity containing the list of outfits
     */
    @GetMapping("/season/{season}")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getOutfitsBySeason(@PathVariable String season) {
        User currentUserEntity = getLoggedInUser();
        List<Outfit> outfits = outfitService.getUserOutfits(currentUserEntity.getId()).stream()
                .map(outfitDto -> outfitService.getOutfitEntityById(outfitDto.getId()))
                .filter(outfit -> outfit.getSeason().equals(season))
                .collect(Collectors.toList());
        List<OutfitResponseDto> responseDtos = outfits.stream()
                .map(outfit -> outfitService.getOutfit(outfit.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Seasonal outfits retrieved successfully", responseDtos));
    }

    /**
     * Checks if the current user owns the outfit.
     *
     * @param outfitId the ID of the outfit to check
     * @return true if the current user owns the outfit, false otherwise
     */
    private boolean isUserOwnerOfOutfit(Long outfitId) {
        User currentUserEntity = getLoggedInUser();
        OutfitResponseDto existingOutfit = outfitService.getOutfit(outfitId);
        return existingOutfit != null && existingOutfit.getProfileId().equals(currentUserEntity.getId());
    }
}
