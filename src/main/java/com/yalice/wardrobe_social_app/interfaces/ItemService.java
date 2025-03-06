package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing wardrobe items.
 */
public interface ItemService {
    /**
     * Creates a new wardrobe item.
     *
     * @param userId  the ID of the user creating the item
     * @param itemDto the item data
     * @param image   the item image file (optional)
     * @return the created item
     */
    ItemResponseDto createItem(Long userId, ItemDto itemDto, MultipartFile image);

    /**
     * Updates an existing wardrobe item.
     *
     * @param userId  the ID of the user updating the item
     * @param itemId  the ID of the item to update
     * @param itemDto the updated item data
     * @param image   the new item image file (optional)
     * @return the updated item
     */
    ItemResponseDto updateItem(Long userId, Long itemId, ItemDto itemDto, MultipartFile image);

    /**
     * Deletes a wardrobe item.
     *
     * @param userId the ID of the user deleting the item
     * @param itemId the ID of the item to delete
     */
    void deleteItem(Long userId, Long itemId);

    /**
     * Gets all items for a specific user.
     *
     * @param userId the ID of the user
     * @return the list of items
     */
    List<ItemResponseDto> getUserItems(Long userId);

    /**
     * Gets a specific item by ID.
     *
     * @param id the ID of the item to retrieve
     * @return the item
     */
    ItemResponseDto getItem(Long id);

    // Get Item by name: Returns ItemResponseDto
    ItemResponseDto getItemByName(String itemName); // Returns ItemResponseDto, throws exception if not found

    // Get Item entity: Return Item entity
    Item getItemEntity(Long id); // Returns Item, throws exception if not found
}
