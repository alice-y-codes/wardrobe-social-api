package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.interfaces.WardrobeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling wardrobe-related operations.
 * Provides endpoints for managing user wardrobes.
 */
@RestController
@RequestMapping("/api/wardrobes")
public class WardrobeController extends ApiBaseController {

    private final WardrobeService wardrobeService;

    @Autowired
    public WardrobeController(WardrobeService wardrobeService, AuthUtils authUtils) {
        super(authUtils);
        this.wardrobeService = wardrobeService;
    }

    /**
     * Creates a new wardrobe for the logged-in user.
     *
     * @param wardrobeDto the details of the wardrobe to create
     * @return ResponseEntity containing the created wardrobe details
     */
    @PostMapping
    public ResponseEntity<ApiResponse<WardrobeResponseDto>> createWardrobe(@RequestBody WardrobeDto wardrobeDto) {
        return handleEntityAction(
                () -> wardrobeService.createWardrobe(getLoggedInUser().getProfile().getId(), wardrobeDto),
                "create wardrobe", "Wardrobe"
        );
    }

    /**
     * Retrieves all wardrobes for the logged-in user.
     *
     * @return ResponseEntity containing the list of wardrobes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<WardrobeResponseDto>>> getProfileWardrobes() {
        return handleEntityAction(
                () -> wardrobeService.getProfileWardrobes(getLoggedInUser().getProfile().getId()),
                "retrieve user wardrobes", "Wardrobe"
        );
    }

    /**
     * Retrieves a specific wardrobe by its ID.
     *
     * @param wardrobeId the ID of the wardrobe to retrieve
     * @return ResponseEntity containing the wardrobe details
     */
    @GetMapping("/{wardrobeId}")
    public ResponseEntity<ApiResponse<WardrobeResponseDto>> getWardrobeById(@PathVariable Long wardrobeId) {
        return handleEntityAction(
                () -> wardrobeService.getWardrobeById(wardrobeId),
                "retrieve wardrobe by ID", "Wardrobe"
        );
    }

    /**
     * Updates the details of a specific wardrobe.
     *
     * @param wardrobeId  the ID of the wardrobe to update
     * @param wardrobeDto the updated wardrobe details
     * @return ResponseEntity containing the updated wardrobe details
     */
    @PutMapping("/{wardrobeId}")
    public ResponseEntity<ApiResponse<WardrobeResponseDto>> updateWardrobe(@PathVariable Long wardrobeId,
                                                                           @RequestBody WardrobeDto wardrobeDto) {
        return handleEntityAction(
                () -> wardrobeService.updateWardrobe(wardrobeId, wardrobeDto),
                "update wardrobe", "Wardrobe"
        );
    }

    /**
     * Deletes a specific wardrobe.
     *
     * @param wardrobeId the ID of the wardrobe to delete
     * @return ResponseEntity indicating whether the deletion was successful
     */
    @DeleteMapping("/{wardrobeId}")
    public ResponseEntity<ApiResponse<Void>> deleteWardrobe(@PathVariable Long wardrobeId) {
        return handleEntityAction(
                () -> {
                    wardrobeService.deleteWardrobe(wardrobeId);
                    return null;
                },
                "delete wardrobe", "Wardrobe"
        );
    }
}
