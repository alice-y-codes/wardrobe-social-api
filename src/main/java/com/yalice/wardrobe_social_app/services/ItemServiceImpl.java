package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import jakarta.persistence.EntityNotFoundException;
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

    private final ItemRepository itemRepository;
    private final ProfileRepository profileRepository;
    private final WardrobeRepository wardrobeRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ProfileRepository profileRepository, WardrobeRepository wardrobeRepository) {
        this.itemRepository = itemRepository;
        this.profileRepository = profileRepository;
        this.wardrobeRepository = wardrobeRepository;
    }

    /**
     * Creates a new item for a given user profile and links it to the specified wardrobe.
     *
     * @param userId The ID of the user creating the item.
     * @param wardrobeId The ID of the wardrobe where the item will be added.
     * @param itemDto The item data to be created.
     * @return ItemResponseDto The response DTO containing the created item details.
     */
    @Override
    @Transactional
    public ItemResponseDto createItem(Long userId, Long wardrobeId, ItemDto itemDto) {
        logger.info("Attempting to create item for user ID: {} in wardrobe ID: {}", userId, wardrobeId);

        if (itemDto == null || userId == null || wardrobeId == null) {
            throw new IllegalArgumentException("User ID, Wardrobe ID, and Item cannot be null");
        }

        // Find the profile and wardrobe
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.warn("Profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Profile not found for user with ID: " + userId);
                });

        Wardrobe wardrobe = wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> {
                    logger.warn("Wardrobe not found with ID: {}", wardrobeId);
                    return new ResourceNotFoundException("Wardrobe not found with ID: " + wardrobeId);
                });

        // Check if item with the same name exists within the same wardrobe
        if (itemRepository.findByNameAndWardrobeId(itemDto.getName(), wardrobeId).isPresent()) {
            logger.warn("Item with name '{}' already exists in the wardrobe.", itemDto.getName());
            throw new IllegalStateException("Item with this name already exists in the wardrobe");
        }

        // Create and save the new item
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setBrand(itemDto.getBrand());
        item.setCategory(itemDto.getCategory());
        item.setSize(itemDto.getSize());
        item.setColor(itemDto.getColor());
        item.setImageUrl(itemDto.getImageUrl());
        item.setWardrobe(wardrobe);
        item.setProfile(profile);  // Set the profile instead of the user

        // Save the item
        item = itemRepository.save(item);

        // Optionally, add the item to the wardrobe (if needed for other functionality)
        wardrobe.getItems().add(item);
        wardrobeRepository.save(wardrobe);

        logger.info("Item '{}' created successfully for user '{}' in wardrobe '{}'.", item.getName(), profile.getUser().getUsername(), wardrobe.getName());

        return convertToItemResponseDto(item);
    }

    /**
     * Retrieves an item entity by its ID.
     *
     * @param id The ID of the item to retrieve.
     * @return Item entity The response entity containing the item details.
     */
    @Override
    public Item getItemEntity(Long id) {
        logger.info("Fetching item with ID: {}", id);

        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Item not found with ID: {}", id);
                    return new EntityNotFoundException("Item not found with ID: " + id);
                });
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

        Profile profile = item.getProfile();
        logger.info("Item '{}' found for user '{}'.", item.getName(), profile.getUser().getUsername());

        return convertToItemResponseDto(item);
    }

    /**
     * Retrieves all items associated with a specific user profile.
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

        // Find the profile by userId
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.warn("Profile not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Profile not found for user with ID: " + userId);
                });

        // Assuming each profile has a default wardrobe. Adjust this logic if multiple wardrobes exist.
        Wardrobe wardrobe = wardrobeRepository.findByProfileId(profile.getId())
                .orElseThrow(() -> {
                    logger.warn("Wardrobe not found for profile ID: {}", profile.getId());
                    return new ResourceNotFoundException("Wardrobe not found for profile with ID: " + profile.getId());
                });

        // Fetch all items for the given profile's wardrobe
        List<Item> items = itemRepository.findAllByWardrobeId(wardrobe.getId());

        logger.info("Found {} items for user '{}'.", items.size(), profile.getUser().getUsername());

        // Convert to ItemResponseDto and return
        return items.stream()
                .map(this::convertToItemResponseDto)
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

        Profile profile = item.getProfile();
        logger.info("Item '{}' found for user '{}'.", item.getName(), profile.getUser().getUsername());

        return convertToItemResponseDto(item);
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
        Profile profile = existingItem.getProfile();

        logger.info("Item '{}' updated successfully for user '{}'.", existingItem.getName(), profile.getUser().getUsername());

        return convertToItemResponseDto(existingItem);
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
