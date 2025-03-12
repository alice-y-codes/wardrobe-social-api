package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for handling outfit-related operations.
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
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OutfitResponseDto>> createOutfit(
            @RequestPart("outfit") OutfitDto outfitDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return handleEntityAction(() -> outfitService.createOutfit(getLoggedInUser().getId(), outfitDto, image),
                "create", "Outfit", "created");
    }

    /**
     * Updates an existing outfit.
     */
    @PatchMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> updateOutfit(
            @PathVariable Long outfitId,
            @RequestPart("outfit") OutfitDto outfitDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return handleEntityAction(
                () -> outfitService.updateOutfit(getLoggedInUser().getId(), outfitId, outfitDto, image),
                "update", "Outfit", "updated");
    }

    /**
     * Deletes an outfit.
     */
    @DeleteMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<Void>> deleteOutfit(@PathVariable Long outfitId) {
        return handleVoidAction(() -> outfitService.deleteOutfit(getLoggedInUser().getId(), outfitId),
                "delete", "Outfit", "deleted");
    }

    /**
     * Retrieves all outfits for the current user.
     */
    @GetMapping("/my-outfits")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getMyOutfits() {
        return handleEntityAction(() -> outfitService.getUserOutfits(getLoggedInUser().getId()),
                "retrieve", "Outfit", "retrieved");
    }

    /**
     * Retrieves a specific outfit by ID.
     */
    @GetMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> getOutfit(@PathVariable Long outfitId) {
        return handleEntityAction(() -> outfitService.getOutfit(outfitId),
                "retrieve", "Outfit", "retrieved");
    }

    /**
     * Retrieves all outfits for a specific user.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getUserOutfits(@PathVariable Long userId) {
        return handleEntityAction(() -> outfitService.getUserOutfits(userId),
                "retrieve", "Outfit", "retrieved");
    }

    /**
     * Adds an item to an outfit.
     */
    @PostMapping("/{outfitId}/items/{itemId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> addItemToOutfit(
            @PathVariable Long outfitId, @PathVariable Long itemId) {
        try {
            return handleEntityAction(() -> outfitService.addItemToOutfit(outfitId, itemId),
                    "add item to", "Outfit", "added to outfit");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Outfit or item not found", null));
        }
    }

    /**
     * Removes an item from an outfit.
     */
    @DeleteMapping("/{outfitId}/items/{itemId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> removeItemFromOutfit(
            @PathVariable Long outfitId, @PathVariable Long itemId) {
        try {
            return handleEntityAction(() -> outfitService.removeItemFromOutfit(outfitId, itemId),
                    "remove item from", "Outfit", "removed from outfit");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Outfit or item not found", null));
        }
    }

    // /**
    // * Retrieves outfits by season.
    // */
    // @GetMapping("/season/{season}")
    // public ResponseEntity<ApiResponse<List<OutfitResponseDto>>>
    // getOutfitsBySeason(@PathVariable String season) {
    // return handleEntityAction(() ->
    // outfitService.getOutfitsBySeason(getLoggedInUser().getId(), season),
    // "Outfits filtered by season retrieved successfully");
    // }
}
