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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl extends BaseService implements ItemService {

    private final ItemRepository itemRepository;
    private final ProfileRepository profileRepository;
    private final WardrobeRepository wardrobeRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ProfileRepository profileRepository,
                           WardrobeRepository wardrobeRepository) {
        this.itemRepository = itemRepository;
        this.profileRepository = profileRepository;
        this.wardrobeRepository = wardrobeRepository;
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(Long userId, ItemDto itemDto, Long wardrobeId, MultipartFile image) {
        logger.info("Attempting to create item for user ID: {} in wardrobe ID: {}", userId, wardrobeId);

        if (itemDto == null || userId == null || wardrobeId == null) {
            throw new IllegalArgumentException("User ID, Wardrobe ID, and Item cannot be null");
        }

        // Find profile and wardrobe
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with ID: " + userId));

        Wardrobe wardrobe = wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> new ResourceNotFoundException("Wardrobe not found with ID: " + wardrobeId));

        // Check if item with same name exists in the same wardrobe
        if (itemRepository.findByNameAndWardrobeId(itemDto.getName(), wardrobeId).isPresent()) {
            throw new IllegalStateException("Item with this name already exists in the wardrobe");
        }

        // Create item
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setBrand(itemDto.getBrand());
        item.setCategory(itemDto.getCategory());
        item.setSize(itemDto.getSize());
        item.setColor(itemDto.getColor());
        item.setWardrobe(wardrobe);
        item.setProfile(profile);

        // Handle image if provided
        if (image != null && !image.isEmpty()) {
            // TODO: Implement image upload logic here
            item.setImageUrl("placeholder_url"); // Example placeholder for image
        }

        // Save the item and return response DTO
        item = itemRepository.save(item);
        return convertToItemResponseDto(item);
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long userId, Long itemId, ItemDto itemDto, MultipartFile image) {
        logger.info("Attempting to update item with ID: {} for user ID: {}", itemId, userId);

        if (itemId == null || itemDto == null) {
            throw new IllegalArgumentException("Item ID and Item cannot be null");
        }

        // Fetch existing item
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        // Check if the item belongs to the user
        if (!existingItem.getProfile().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("User does not own this item");
        }

        // Update item fields
        existingItem.setName(itemDto.getName());
        existingItem.setBrand(itemDto.getBrand());
        existingItem.setCategory(itemDto.getCategory());
        existingItem.setSize(itemDto.getSize());
        existingItem.setColor(itemDto.getColor());

        // Handle image if provided
        if (image != null && !image.isEmpty()) {
            // TODO: Implement image upload logic here
            existingItem.setImageUrl("placeholder_url"); // Example placeholder for image
        }

        // Save the updated item and return response DTO
        itemRepository.saveAndFlush(existingItem);
        return convertToItemResponseDto(existingItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        logger.info("Attempting to delete item with ID: {} for user ID: {}", itemId, userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID " + itemId));

        if (!item.getProfile().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("User does not own this item");
        }

        itemRepository.deleteById(itemId);
        logger.info("Item with ID '{}' deleted successfully.", itemId);
    }

    @Override
    public List<ItemResponseDto> getUserItems(Long userId) {
        logger.info("Fetching all items for user ID: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user with ID: " + userId));

        Wardrobe wardrobe = wardrobeRepository.findByProfileId(profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wardrobe not found for profile with ID: " + profile.getId()));

        List<Item> items = itemRepository.findAllByWardrobeId(wardrobe.getId());
        return items.stream()
                .map(this::convertToItemResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItem(Long id) {
        logger.info("Fetching item with ID: {}", id);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID " + id));

        return convertToItemResponseDto(item);
    }

    @Override
    public ItemResponseDto getItemByName(String itemName) {
        logger.info("Fetching item with name: {}", itemName);

        Item item = itemRepository.findByName(itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with name " + itemName));

        return convertToItemResponseDto(item);
    }

    @Override
    public Item getItemEntity(Long id) {
        logger.info("Fetching item with ID: {}", id);

        return itemRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Item not found with ID " + id)
        );
    }
}
