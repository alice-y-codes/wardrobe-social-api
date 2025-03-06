package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
        return handleOutfitOperation(() -> {
            User currentUser = getLoggedInUser();
            return outfitService.createOutfit(currentUser.getId(), outfitDto, image);
        });
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
        return handleOutfitOperation(() -> {
            User currentUser = getLoggedInUser();
            return outfitService.updateOutfit(currentUser.getId(), outfitId, outfitDto, image);
        });
    }

    private ResponseEntity<ApiResponse<OutfitResponseDto>> handleOutfitOperation(OutfitAction outfitAction) {
        try {
            OutfitResponseDto result = outfitAction.execute();
            return createSuccessResponse("Outfit operation successful", result);
        } catch (Exception e) {
            logger.error("Outfit operation failed", e);
            return createInternalServerErrorResponse("Failed to process outfit operation");
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
        return handleOutfitOperation(() -> {
            User currentUser = getLoggedInUser();
            outfitService.deleteOutfit(currentUser.getId(), outfitId);
            return null; // No return value needed for deletion
        });
    }

    /**
     * Retrieves all outfits for the current user.
     *
     * @return ResponseEntity containing the list of outfits
     */
    @GetMapping("/my-outfits")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getMyOutfits() {
        return handleOutfitRetrieval(() -> {
            User currentUser = getLoggedInUser();
            return outfitService.getUserOutfits(currentUser.getId());
        });
    }

    /**
     * Retrieves a specific outfit by ID.
     *
     * @param outfitId the ID of the outfit to retrieve
     * @return ResponseEntity containing the outfit
     */
    @GetMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> getOutfit(@PathVariable Long outfitId) {
        return handleOutfitRetrieval(() -> {
            OutfitResponseDto outfit = outfitService.getOutfit(outfitId);
            if (outfit == null) throw new RuntimeException("Outfit not found");
            return outfit;
        });
    }

    /**
     * Retrieves all outfits for a specific user.
     *
     * @param userId the ID of the user
     * @return ResponseEntity containing the list of outfits
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getUserOutfits(@PathVariable Long userId) {
        return handleOutfitRetrieval(() -> outfitService.getUserOutfits(userId));
    }

    private <T> ResponseEntity<ApiResponse<T>> handleOutfitRetrieval(OutfitRetriever<T> outfitRetriever) {
        try {
            T result = outfitRetriever.execute();
            if (result instanceof List && ((List<?>) result).isEmpty()) {
                return createNotFoundResponse("No outfits found");
            }
            return createSuccessResponse("Outfits retrieved successfully", result);
        } catch (Exception e) {
            logger.error("Failed to retrieve outfits", e);
            return createInternalServerErrorResponse("Failed to retrieve outfits");
        }
    }

    @FunctionalInterface
    interface OutfitAction {
        OutfitResponseDto execute() throws Exception;
    }

    @FunctionalInterface
    interface OutfitRetriever<T> {
        T execute() throws Exception;
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
        return handleItemModification(outfitId, itemId, true);
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
        return handleItemModification(outfitId, itemId, false);
    }

    private ResponseEntity<ApiResponse<OutfitResponseDto>> handleItemModification(Long outfitId, Long itemId, boolean add) {
        try {
            if (isUserOwnerOfOutfit(outfitId)) {
                return createForbiddenResponse("You don't have permission to modify this outfit");
            }

            OutfitResponseDto updatedOutfit = add
                    ? outfitService.addItemToOutfit(outfitId, itemId)
                    : outfitService.removeItemFromOutfit(outfitId, itemId);

            if (updatedOutfit == null) return createNotFoundResponse("Outfit or Item not found");
            return createSuccessResponse("Item modification successful", updatedOutfit);
        } catch (Exception e) {
            logger.error("Item modification failed", e);
            return createInternalServerErrorResponse("Failed to modify item in outfit");
        }
    }

    /**
     * Retrieves outfits by season.
     *
     * @param season the season to filter by
     * @return ResponseEntity containing the list of outfits
     */
    @GetMapping("/season/{season}")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getOutfitsBySeason(@PathVariable String season) {
        return handleOutfitRetrieval(() -> {
            User currentUserEntity = getLoggedInUser();
            List<Outfit> outfits = outfitService.getUserOutfits(currentUserEntity.getId()).stream()
                    .map(outfitDto -> outfitService.getOutfitEntityById(outfitDto.getId()))
                    .filter(outfit -> outfit.getSeason().equals(season))
                    .toList();

            return outfits.stream()
                    .map(outfit -> outfitService.getOutfit(outfit.getId()))
                    .collect(Collectors.toList());
        });
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
        return existingOutfit == null || !existingOutfit.getProfileId().equals(currentUserEntity.getId());
    }
}
