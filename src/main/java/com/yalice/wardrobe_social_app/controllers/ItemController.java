package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final CurrentUser currentUser;

    @Autowired
    public ItemController(ItemService itemService, UserSearchService userSearchService) {
        this.itemService = itemService;
        this.currentUser = new CurrentUser(userSearchService); // Instantiate CurrentUser with UserService
    }

    /**
     * Create a new item.
     *
     * @param itemDto the item data transfer object (DTO)
     * @return a response containing the created item
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponseDto>> createItem(@RequestBody ItemDto itemDto) {
        User user = currentUser.getCurrentUserOrElseThrow(); // Fetch the current authenticated user

        ItemResponseDto createdItem = itemService.createItem(user.getId(), itemDto);

        return createdItem != null
                ? ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Item created successfully", createdItem))
                : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Item creation failed", null));
    }

    /**
     * Get all items for a specific user.
     *
     * @param userId the user ID
     * @return a response containing the list of items for the user
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> getAllItems(@PathVariable Long userId) {
        // Call the service to get the list of ItemResponseDto
        List<ItemResponseDto> items = itemService.getAllItems(userId);

        return ResponseEntity.ok(new ApiResponse<>(true, "Items retrieved successfully", items));
    }

    /**
     * Get all items for the current authenticated user.
     *
     * @return a response containing the list of items for the current user
     */
    @GetMapping("/my-items")
    public ResponseEntity<ApiResponse<List<ItemResponseDto>>> getMyItems() {
        User user = currentUser.getCurrentUserOrElseThrow(); // Fetch the current authenticated user

        // Call the service to get the list of ItemResponseDto
        List<ItemResponseDto> items = itemService.getAllItems(user.getId());

        return ResponseEntity.ok(new ApiResponse<>(true, "Items retrieved successfully", items));
    }

    /**
     * Get an item by its ID.
     *
     * @param id the ID of the item
     * @return a response containing the item or an error message if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> getItemById(@PathVariable Long id) {
        ItemResponseDto item = itemService.getItem(id);

        if (item != null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Item retrieved successfully", item));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Item not found", null));
        }
    }

    /**
     * Get an item by its name.
     *
     * @param name the name of the item
     * @return a response containing the item or an error message if not found
     */
    @GetMapping("/names/{name}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> getItemByName(@PathVariable String name) {
        ItemResponseDto item = itemService.getItemByName(name);

        if (item != null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Item retrieved successfully", item));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Item not found", null));
        }
    }

    /**
     * Update an existing item.
     *
     * @param id the ID of the item to be updated
     * @param itemDto the item data transfer object (DTO)
     * @return a response containing the updated item or an error message if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(@PathVariable Long id, @RequestBody ItemDto itemDto) {
        ItemResponseDto updatedItem = itemService.updateItem(id, itemDto);

        if (updatedItem != null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Item updated successfully", updatedItem));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Item not found", null));
        }
    }

    /**
     * Delete an item by its ID.
     *
     * @param id the ID of the item to be deleted
     * @return a response indicating the result of the deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        boolean deleted = itemService.deleteItem(id);

        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(true, "Item deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Item not found", null));
        }
    }
}
