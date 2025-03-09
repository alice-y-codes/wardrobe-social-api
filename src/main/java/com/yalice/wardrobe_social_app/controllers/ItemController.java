package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for item-related operations.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController extends ApiBaseController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService, AuthUtils authUtils) {
        super(authUtils);
        this.itemService = itemService;
    }

    /**
     * Creates a new item in the user's wardrobe.
     */
    @PostMapping("/{wardrobeId}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> createItem(
            @PathVariable Long wardrobeId,
            @RequestPart("item") ItemDto itemDto,
            @RequestPart(value = "image") MultipartFile image) {

        if (image == null || image.isEmpty()) {
            logger.warn("Attempted to create an item without an image for wardrobe ID: {}", wardrobeId);
            return createBadRequestResponse("Image is required to create an item.");
        }

        return handleEntityAction(
                () -> itemService.createItem(getLoggedInUser().getId(), wardrobeId, itemDto, image),
                "create", "Item", "created");
    }

    /**
     * Updates an existing wardrobe item.
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(
            @PathVariable Long itemId,
            @RequestPart("item") ItemDto itemDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return handleEntityAction(
                () -> itemService.updateItem(getLoggedInUser().getId(), itemId, itemDto, image),
                "update", "Item", "updated");
    }

    /**
     * Deletes an item from the wardrobe.
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long itemId) {
        return handleVoidAction(
                () -> itemService.deleteItem(getLoggedInUser().getId(), itemId),
                "delete", "Item", "deleted");
    }

    /**
     * Retrieves all items for the current user.
     */
    @GetMapping("/my-items")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> getMyItems() {
        return handleEntityAction(
                () -> itemService.getUserItems(getLoggedInUser().getId()),
                "retrieve", "Item", "retrieved");
    }

    /**
     * Retrieves a specific item by its ID.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> getItem(@PathVariable Long itemId) {
        return handleEntityAction(
                () -> itemService.getItem(itemId),
                "retrieve", "Item", "retrieved");
    }
}
