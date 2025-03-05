package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ItemService interface for managing items in the wardrobe application.
 * This class handles CRUD operations for items, including creation, retrieval, updating, and deletion.
 */
@Service
public class ItemServiceImpl extends BaseService implements ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new item for a given user.
     *
     * @param userId The ID of the user creating the item.
     * @param itemDto The item data to be created.
     * @return ItemResponseDto The response DTO containing the created item details.
     */
    @Override
    @Transactional
    public ItemResponseDto createItem(Long userId, ItemDto itemDto) {
        logger.info("Attempting to create item for user ID: {}", userId);

        if (itemDto == null || userId == null) {
            throw new IllegalArgumentException("User ID and Item cannot be null");
        }

        if (itemRepository.findByName(itemDto.getName()).isPresent()) {
            logger.warn("Item with name '{}' already exists.", itemDto.getName());
            throw new IllegalStateException("Item with this name already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setBrand(itemDto.getBrand());
        item.setCategory(itemDto.getCategory());
        item.setSize(itemDto.getSize());
        item.setColor(itemDto.getColor());
        item.setImageUrl(itemDto.getImageUrl());
        item.setUser(user);

        user.getItems().add(item);
        userRepository.save(user); // Save the user to persist the relationship
        item = itemRepository.save(item); // Save the item

        logger.info("Item '{}' created successfully for user '{}'.", item.getName(), user.getUsername());

        return convertToItemResponseDto(item, user); // Return the DTO with userId
    }

    /**
     * Retrieves an item by its ID.
     *
     * @param id The ID of the item to retrieve.
     * @return ItemResponseDto The response DTO containing the item details.
     */
    @Override
    public ItemResponseDto getItem(Long id) {
        logger.info("Fetching item with ID: {}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Item not found with ID: {}", id);
                    return new ResourceNotFoundException("Item not found with ID " + id);
                });

        User user = item.getUser(); // Get the User object from Item
        logger.info("Item '{}' found for user '{}'.", item.getName(), user.getUsername());

        return convertToItemResponseDto(item, user); // Return DTO with userId
    }

    /**
     * Retrieves all items associated with a specific user.
     *
     * @param userId The ID of the user whose items are to be retrieved.
     * @return List<ItemResponseDto> A list of ItemResponseDto containing the items' details.
     */
    @Override
    public List<ItemResponseDto> getAllItems(Long userId) {
        logger.info("Fetching all items for user ID: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        List<Item> items = itemRepository.findByUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        logger.info("Found {} items for user '{}'.", items.size(), user.getUsername());

        return items.stream()
                .map(item -> convertToItemResponseDto(item, user))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an item by its name.
     *
     * @param itemName The name of the item to retrieve.
     * @return ItemResponseDto The response DTO containing the item details.
     */
    @Override
    public ItemResponseDto getItemByName(String itemName) {
        logger.info("Fetching item with name: {}", itemName);

        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }

        Item item = itemRepository.findByName(itemName)
                .orElseThrow(() -> {
                    logger.warn("Item not found with name: {}", itemName);
                    return new ResourceNotFoundException("Item not found with name " + itemName);
                });

        User user = item.getUser(); // Get the User object from Item
        logger.info("Item '{}' found for user '{}'.", item.getName(), user.getUsername());

        return convertToItemResponseDto(item, user); // Return DTO with userId
    }

    /**
     * Updates an existing item.
     *
     * @param id The ID of the item to update.
     * @param itemDto The updated item data.
     * @return ItemResponseDto The response DTO containing the updated item details.
     */
    @Override
    @Transactional
    public ItemResponseDto updateItem(Long id, ItemDto itemDto) {
        logger.info("Attempting to update item with ID: {}", id);

        if (id == null || itemDto == null) {
            throw new IllegalArgumentException("Item ID and Item cannot be null");
        }

        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Item not found with ID: {}", id);
                    return new ResourceNotFoundException("Item not found with ID " + id);
                });

        existingItem.setName(itemDto.getName());
        existingItem.setBrand(itemDto.getBrand());
        existingItem.setCategory(itemDto.getCategory());
        existingItem.setSize(itemDto.getSize());
        existingItem.setColor(itemDto.getColor());
        existingItem.setImageUrl(itemDto.getImageUrl());

        itemRepository.saveAndFlush(existingItem); // Save the updated item
        User user = existingItem.getUser(); // Get the User object from Item

        logger.info("Item '{}' updated successfully for user '{}'.", existingItem.getName(), user.getUsername());

        return convertToItemResponseDto(existingItem, user); // Return updated DTO with userId
    }

    /**
     * Deletes an item by its ID.
     *
     * @param id The ID of the item to delete.
     * @return boolean Indicates whether the item was successfully deleted.
     */
    @Override
    @Transactional
    public boolean deleteItem(Long id) {
        logger.info("Attempting to delete item with ID: {}", id);

        if (!itemRepository.existsById(id)) {
            logger.warn("Item with ID '{}' not found for deletion.", id);
            throw new ResourceNotFoundException("Item not found with ID " + id);
        }

        itemRepository.deleteById(id); // Delete the item
        logger.info("Item with ID '{}' deleted successfully.", id);

        return true;
    }

}
