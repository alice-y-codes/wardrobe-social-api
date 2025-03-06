package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for item-related operations.
 * Handles creating, updating, deleting, and retrieving wardrobe items.
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
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return handleItemAction(() -> itemService.createItem(getLoggedInUser().getId(), wardrobeId, itemDto, image),
                "create item");
    }

    /**
     * Updates an existing wardrobe item.
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(
            @PathVariable Long itemId,
            @RequestPart("item") ItemDto itemDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return handleItemAction(() -> itemService.updateItem(getLoggedInUser().getId(), itemId, itemDto, image),
                "update item");
    }

    /**
     * Deletes an item from the wardrobe.
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long itemId) {
        return handleVoidAction(() -> itemService.deleteItem(getLoggedInUser().getId(), itemId), "delete item");
    }

    /**
     * Retrieves all items for the current user.
     */
    @GetMapping("/my-items")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> getMyItems() {
        return handleItemAction(() -> itemService.getUserItems(getLoggedInUser().getId()), "retrieve user items");
    }

    /**
     * Retrieves a specific item by its ID.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> getItem(@PathVariable Long itemId) {
        return handleItemAction(() -> itemService.getItem(itemId), "retrieve item");
    }

    /**
     * Handles item-related actions and error handling.
     */
    private <T> ResponseEntity<ApiResponse<T>> handleItemAction(ItemSupplier<T> supplier, String action) {
        try {
            T result = supplier.get();
            logger.info("Successfully {}: {}", action, result);
            return createSuccessResponse("Item " + action + "d successfully", result);
        } catch (Exception e) {
            return handleServiceError(e, action);
        }
    }

    /**
     * Handles actions that do not return data.
     */
    private ResponseEntity<ApiResponse<Void>> handleVoidAction(VoidSupplier supplier, String action) {
        try {
            supplier.execute();
            logger.info("Successfully {} item", action);
            return createSuccessResponse("Item " + action + "d successfully", null);
        } catch (Exception e) {
            return handleServiceError(e, action);
        }
    }

    /**
     * Functional interface for item operations that return a result.
     */
    @FunctionalInterface
    private interface ItemSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Functional interface for void operations.
     */
    @FunctionalInterface
    private interface VoidSupplier {
        void execute() throws Exception;
    }
}
