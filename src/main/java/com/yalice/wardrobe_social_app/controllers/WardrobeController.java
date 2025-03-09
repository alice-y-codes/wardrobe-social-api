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
     */
    @PostMapping
    public ResponseEntity<ApiResponse<WardrobeResponseDto>> createWardrobe(@RequestBody WardrobeDto wardrobeDto) {
        return handleEntityAction(() ->
                        wardrobeService.createWardrobe(getLoggedInUser().getProfile().getId(), wardrobeDto),
                "create", "Wardrobe", "created"
        );
    }

    /**
     * Retrieves all wardrobes for the logged-in user.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<WardrobeResponseDto>>> getProfileWardrobes() {
        return handleEntityRetrieval(() ->
                        wardrobeService.getProfileWardrobes(getLoggedInUser().getProfile().getId()),
                "Wardrobes"
        );
    }

    /**
     * Retrieves a specific wardrobe by its ID.
     */
    @GetMapping("/{wardrobeId}")
    public ResponseEntity<ApiResponse<WardrobeResponseDto>> getWardrobeById(@PathVariable Long wardrobeId) {
        return handleEntityRetrieval(() ->
                        wardrobeService.getWardrobeById(wardrobeId),
                "Wardrobe"
        );
    }

    /**
     * Updates the details of a specific wardrobe.
     */
    @PutMapping("/{wardrobeId}")
    public ResponseEntity<ApiResponse<WardrobeResponseDto>> updateWardrobe(
            @PathVariable Long wardrobeId,
            @RequestBody WardrobeDto wardrobeDto) {
        return handleEntityAction(() ->
                        wardrobeService.updateWardrobe(wardrobeId, wardrobeDto),
                "update", "Wardrobe", "updated"
        );
    }

    /**
     * Deletes a specific wardrobe.
     */
    @DeleteMapping("/{wardrobeId}")
    public ResponseEntity<ApiResponse<Void>> deleteWardrobe(@PathVariable Long wardrobeId) {
        return handleVoidAction(() ->
                        wardrobeService.deleteWardrobe(wardrobeId),
                "delete", "Wardrobe", "deleted"
        );
    }
}
