package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
                "Outfit created successfully", "Outfit");
    }

    /**
     * Updates an existing outfit.
     */
    @PutMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> updateOutfit(
            @PathVariable Long outfitId,
            @RequestPart("outfit") OutfitDto outfitDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return handleEntityAction(() -> outfitService.updateOutfit(getLoggedInUser().getId(), outfitId, outfitDto, image),
                "Outfit updated successfully", "Outfit");
    }

    /**
     * Deletes an outfit.
     */
    @DeleteMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<Void>> deleteOutfit(@PathVariable Long outfitId) {
        return handleVoidAction(() -> outfitService.deleteOutfit(getLoggedInUser().getId(), outfitId),
                "Outfit deleted successfully", "Outfit");
    }

    /**
     * Retrieves all outfits for the current user.
     */
    @GetMapping("/my-outfits")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getMyOutfits() {
        return handleEntityAction(() -> outfitService.getUserOutfits(getLoggedInUser().getId()),
                "User outfits retrieved successfully", "Outfit");
    }

    /**
     * Retrieves a specific outfit by ID.
     */
    @GetMapping("/{outfitId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> getOutfit(@PathVariable Long outfitId) {
        return handleEntityAction(() -> outfitService.getOutfit(outfitId),
                "Outfit retrieved successfully", "Outfit");
    }

    /**
     * Retrieves all outfits for a specific user.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getUserOutfits(@PathVariable Long userId) {
        return handleEntityAction(() -> outfitService.getUserOutfits(userId),
                "User outfits retrieved successfully", "Outfit");
    }

    /**
     * Adds an item to an outfit.
     */
    @PostMapping("/{outfitId}/items/{itemId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> addItemToOutfit(
            @PathVariable Long outfitId, @PathVariable Long itemId) {

        return handleEntityAction(() -> outfitService.addItemToOutfit(outfitId, itemId),
                "Item added to outfit successfully", "Outfit");
    }

    /**
     * Removes an item from an outfit.
     */
    @DeleteMapping("/{outfitId}/items/{itemId}")
    public ResponseEntity<ApiResponse<OutfitResponseDto>> removeItemFromOutfit(
            @PathVariable Long outfitId, @PathVariable Long itemId) {

        return handleEntityAction(() -> outfitService.removeItemFromOutfit(outfitId, itemId),
                "Item removed from outfit successfully", "Outfit");
    }

//    /**
//     * Retrieves outfits by season.
//     */
//    @GetMapping("/season/{season}")
//    public ResponseEntity<ApiResponse<List<OutfitResponseDto>>> getOutfitsBySeason(@PathVariable String season) {
//        return handleEntityAction(() -> outfitService.getOutfitsBySeason(getLoggedInUser().getId(), season),
//                "Outfits filtered by season retrieved successfully");
//    }
}
