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
     * @param profileId  the ID of the profile creating the item
     * @param wardrobeId  the ID of the wardrobe for the item to be held
     * @param itemDto the item data
     * @param image   the item image file (optional)
     * @return the created item
     */
    ItemResponseDto createItem(Long profileId, Long wardrobeId, ItemDto itemDto, MultipartFile image);

    /**
     * Updates an existing wardrobe item.
     *
     * @param profileId  the ID of the profile updating the item
     * @param itemId  the ID of the item to update
     * @param itemDto the updated item data
     * @param image   the new item image file (optional)
     * @return the updated item
     */
    ItemResponseDto updateItem(Long profileId, Long itemId, ItemDto itemDto, MultipartFile image);

    /**
     * Deletes a wardrobe item.
     *
     * @param profileId the ID of the profile deleting the item
     * @param itemId the ID of the item to delete
     */
    void deleteItem(Long profileId, Long itemId);

    /**
     * Gets all items for a specific profile.
     *
     * @param profileId the ID of the profile
     * @return the list of items
     */
    List<ItemResponseDto> getUserItems(Long profileId);

    /**
     * Gets a specific item by ID.
     *
     * @param id the ID of the item to retrieve
     * @return the item
     */
    ItemResponseDto getItem(Long id);

    /**
     * Gets an item by its name.
     *
     * @param itemName the name of the item to retrieve
     * @return the item
     */
    ItemResponseDto getItemByName(String itemName);

    /**
     * Gets the item entity by ID.
     *
     * @param id the ID of the item to retrieve
     * @return the item entity
     */
    Item getItemEntity(Long id);
}
