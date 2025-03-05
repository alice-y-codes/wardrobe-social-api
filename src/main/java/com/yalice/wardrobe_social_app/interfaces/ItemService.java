package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;

import java.util.List;

public interface ItemService {

    // Create Item: Takes ItemDto and wardrobeId as input, returns ItemResponseDto
    ItemResponseDto createItem(Long userId, Long wardrobeId, ItemDto itemDto); // Throws exceptions on failure

    // Get all items for a user: Returns a list of ItemResponseDto
    List<ItemResponseDto> getAllItems(Long userId); // Return List of ItemResponseDto

    // Get Item by ID: Returns ItemResponseDto
    ItemResponseDto getItem(Long id); // Returns ItemResponseDto, throws exception if not found

    // Get Item by name: Returns ItemResponseDto
    ItemResponseDto getItemByName(String itemName); // Returns ItemResponseDto, throws exception if not found

    // Update Item: Takes ItemDto as input, returns updated ItemResponseDto
    ItemResponseDto updateItem(Long id, ItemDto itemDto); // Returns updated ItemResponseDto, throws exception if not found

    // Delete Item: No return value, throws exception if not found
    boolean deleteItem(Long id); // No return, throws exception if not found

    // Get Item entity: Return Item entity
    Item getItemEntity(Long itemId); // Returns Item, throws exception if not found
}
