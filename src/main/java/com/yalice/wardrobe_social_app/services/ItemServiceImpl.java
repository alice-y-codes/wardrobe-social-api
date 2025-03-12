package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.mappers.ItemMapper;
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
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository,
                           ProfileRepository profileRepository,
                           WardrobeRepository wardrobeRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.profileRepository = profileRepository;
        this.wardrobeRepository = wardrobeRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(Long profileId, Long wardrobeId, ItemDto itemDto, MultipartFile image) {
        logger.info("Creating item for profile ID: {} in wardrobe ID: {}", profileId, wardrobeId);

        validateCreateItemRequest(profileId, wardrobeId, itemDto);

        Profile profile = findProfileById(profileId);
        Wardrobe wardrobe = findWardrobeById(wardrobeId);

        checkIfItemExistsInWardrobe(itemDto.getName(), wardrobeId);

        Item item = buildItem(itemDto, profile, wardrobe, image);

        item = itemRepository.save(item);
        return itemMapper.toResponseDto(item);
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long profileId, Long itemId, ItemDto itemDto, MultipartFile image) {
        logger.info("Updating item with ID: {} for profile ID: {}", itemId, profileId);

        if (itemId == null || itemDto == null) {
            throw new IllegalArgumentException("Item ID and Item cannot be null");
        }

        Item existingItem = findItemById(itemId);
        validateItemOwnership(existingItem, profileId);

        Item updatedItem = buildUpdatedItem(itemDto, image, existingItem);

        itemRepository.saveAndFlush(updatedItem);
        return itemMapper.toResponseDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItem(Long profileId, Long itemId) {
        logger.info("Deleting item with ID: {} for profile ID: {}", itemId, profileId);

        Item item = findItemById(itemId);
        validateItemOwnership(item, profileId);

        itemRepository.deleteById(itemId);
        logger.info("Item with ID '{}' deleted successfully.", itemId);
    }

    @Override
    public List<ItemResponseDto> getUserItems(Long profileId) {
        logger.info("Fetching all items for profile ID: {}", profileId);

        Profile profile = findProfileById(profileId);
        Wardrobe wardrobe = findWardrobeByProfileId(profile.getId());

        List<Item> items = itemRepository.findAllByWardrobeId(wardrobe.getId());
        return items.stream()
                .map(itemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItem(Long id) {
        logger.info("Fetching item with ID: {}", id);

        Item item = findItemById(id);
        return itemMapper.toResponseDto(item);
    }

    @Override
    public ItemResponseDto getItemByName(String itemName) {
        logger.info("Fetching item with name: {}", itemName);

        Item item = itemRepository.findByName(itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with name: " + itemName));

        return itemMapper.toResponseDto(item);
    }

    @Override
    public Item getItemEntity(Long id) {
        logger.info("Fetching item with ID: {}", id);

        return findItemById(id);
    }

    // Helper methods

    private void validateCreateItemRequest(Long profileId, Long wardrobeId, ItemDto itemDto) {
        if (profileId == null || wardrobeId == null || itemDto == null) {
            throw new IllegalArgumentException("Profile ID, Wardrobe ID, and Item cannot be null");
        }
    }

    private Profile findProfileById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));
    }

    private Wardrobe findWardrobeById(Long wardrobeId) {
        return wardrobeRepository.findById(wardrobeId)
                .orElseThrow(() -> new ResourceNotFoundException("Wardrobe not found with ID: " + wardrobeId));
    }

    private void checkIfItemExistsInWardrobe(String itemName, Long wardrobeId) {
        itemRepository.findByNameAndWardrobeId(itemName, wardrobeId)
                .ifPresent(item -> {
                    throw new IllegalStateException("Item already exists in the wardrobe");
                });
    }

    private Item buildItem(ItemDto itemDto, Profile profile, Wardrobe wardrobe, MultipartFile image) {
        return Item.builder()
                .name(itemDto.getName())
                .brand(itemDto.getBrand())
                .category(itemDto.getCategory())
                .size(itemDto.getSize())
                .color(itemDto.getColor())
                .wardrobe(wardrobe)
                .profile(profile)
                .imageUrl(image != null && !image.isEmpty() ? "placeholder_url" : null)  // Example placeholder for image
                .build();
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));
    }

    private void validateItemOwnership(Item item, Long profileId) {
        if (!item.getProfile().getId().equals(profileId)) {
            throw new ResourceNotFoundException("Profile does not own this item");
        }
    }

    private Item buildUpdatedItem(ItemDto itemDto, MultipartFile image, Item existingItem) {
        return Item.builder()
                .id(existingItem.getId())
                .name(itemDto.getName())
                .brand(itemDto.getBrand())
                .category(itemDto.getCategory())
                .size(itemDto.getSize())
                .color(itemDto.getColor())
                .imageUrl(image != null && !image.isEmpty() ? "placeholder_url" : existingItem.getImageUrl()) // Use existing image URL if no new image is provided
                .build();
    }

    private Wardrobe findWardrobeByProfileId(Long profileId) {
        return wardrobeRepository.findByProfileId(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Wardrobe not found for profile with ID: " + profileId));
    }
}
