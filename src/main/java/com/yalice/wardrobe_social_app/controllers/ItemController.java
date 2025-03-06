package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller responsible for handling item-related operations.
 * Provides endpoints for creating, updating, and managing wardrobe items.
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
     * Creates a new wardrobe item.
     *
     * @param itemDto the item data
     * @param image   the item image file
     * @return ResponseEntity containing the created item
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponseDto>> createItem(
            @RequestPart("item") ItemDto itemDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Attempting to create new item for user");

        User currentUser = getLoggedInUser();
        try {
            ItemResponseDto createdItem = itemService.createItem(currentUser.getId(), itemDto, image);
            logger.info("Successfully created item with ID: {} for user ID: {}", createdItem.getId(),
                    currentUser.getId());
            return createSuccessResponse("Item created successfully", createdItem);
        } catch (Exception e) {
            logger.error("Failed to create item for user ID: {}", currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to create item");
        }
    }

    /**
     * Updates an existing wardrobe item.
     *
     * @param itemId  the ID of the item to update
     * @param itemDto the updated item data
     * @param image   the new item image file (optional)
     * @return ResponseEntity containing the updated item
     */
    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(
            @PathVariable Long itemId,
            @RequestPart("item") ItemDto itemDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        logger.info("Attempting to update item with ID: {}", itemId);

        User currentUser = getLoggedInUser();
        try {
            ItemResponseDto updatedItem = itemService.updateItem(currentUser.getId(), itemId, itemDto, image);
            logger.info("Successfully updated item with ID: {} for user ID: {}", itemId, currentUser.getId());
            return createSuccessResponse("Item updated successfully", updatedItem);
        } catch (Exception e) {
            logger.error("Failed to update item with ID: {} for user ID: {}", itemId, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to update item");
        }
    }

    /**
     * Deletes a wardrobe item.
     *
     * @param itemId the ID of the item to delete
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long itemId) {
        logger.info("Attempting to delete item with ID: {}", itemId);

        User currentUser = getLoggedInUser();
        try {
            itemService.deleteItem(currentUser.getId(), itemId);
            logger.info("Successfully deleted item with ID: {} for user ID: {}", itemId, currentUser.getId());
            return createSuccessResponse("Item deleted successfully", null);
        } catch (Exception e) {
            logger.error("Failed to delete item with ID: {} for user ID: {}", itemId, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to delete item");
        }
    }

    /**
     * Gets all items for the current user.
     *
     * @return ResponseEntity containing the list of items
     */
    @GetMapping("/my-items")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> getMyItems() {
        logger.info("Retrieving items for current user");

        User currentUser = getLoggedInUser();
        try {
            List<ItemResponseDto> items = itemService.getUserItems(currentUser.getId());
            logger.info("Successfully retrieved {} items for user ID: {}", items.size(), currentUser.getId());
            return createSuccessResponse("Items retrieved successfully", items);
        } catch (Exception e) {
            logger.error("Failed to retrieve items for user ID: {}", currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to retrieve items");
        }
    }

    /**
     * Gets a specific item by ID.
     *
     * @param itemId the ID of the item to retrieve
     * @return ResponseEntity containing the item
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> getItem(@PathVariable Long itemId) {
        logger.info("Retrieving item with ID: {}", itemId);

        try {
            ItemResponseDto item = itemService.getItem(itemId);
            logger.info("Successfully retrieved item with ID: {}", itemId);
            return createSuccessResponse("Item retrieved successfully", item);
        } catch (Exception e) {
            logger.error("Failed to retrieve item with ID: {}", itemId, e);
            return createNotFoundResponse("Item not found with ID: " + itemId);
        }
    }
}
