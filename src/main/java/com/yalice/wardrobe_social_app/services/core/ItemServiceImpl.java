package com.yalice.wardrobe_social_app.services.core;

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
import com.yalice.wardrobe_social_app.services.helpers.ImageHandlerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl extends BaseService<Item, Long> implements ItemService {

    private final ItemRepository itemRepository;
    private final ProfileRepository profileRepository;
    private final WardrobeRepository wardrobeRepository;
    private final ItemMapper itemMapper;
    private final ImageHandlerService imageHandler;

    private static final int MAX_ITEM_NAME_LENGTH = 100;

    public ItemServiceImpl(
            ItemRepository itemRepository,
            ProfileRepository profileRepository,
            WardrobeRepository wardrobeRepository,
            ItemMapper itemMapper,
            ImageHandlerService imageHandler) {
        this.itemRepository = itemRepository;
        this.profileRepository = profileRepository;
        this.wardrobeRepository = wardrobeRepository;
        this.itemMapper = itemMapper;
        this.imageHandler = imageHandler;
    }

    @Override
    protected JpaRepository<Item, Long> getRepository() {
        return itemRepository;
    }

    @Override
    protected String getEntityName() {
        return "Item";
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(Long profileId, Long wardrobeId, ItemDto itemDto, MultipartFile image) {
        logger.info("Creating item for profile ID: {} in wardrobe ID: {}", profileId, wardrobeId);

        validateCreateItemParameters(profileId, wardrobeId, itemDto);
        Profile profile = getProfile(profileId);
        Wardrobe wardrobe = getWardrobe(wardrobeId);
        validateWardrobeOwnership(wardrobe, profileId);
        validateItemNameUniqueness(itemDto.getName(), wardrobeId);

        Item item = buildItem(itemDto, profile, wardrobe);
        item = save(item);

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageHandler.handleImageUpload(image, "item", item.getId(), null);
            item.setImageUrl(imageUrl);
            item = save(item);
        }

        logger.info("Successfully created item with ID: {}", item.getId());
        return mapEntity(item, itemMapper::toResponseDto);
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long profileId, Long itemId, ItemDto itemDto, MultipartFile image) {
        logger.info("Updating item with ID: {} for profile ID: {}", itemId, profileId);

        validateUpdateItemParameters(profileId, itemId, itemDto);
        Item existingItem = findById(itemId);
        validateItemOwnership(existingItem, profileId);

        if (!existingItem.getName().equals(itemDto.getName())) {
            validateItemNameUniqueness(itemDto.getName(), existingItem.getWardrobe().getId());
        }

        Item updatedItem = updateItemFields(existingItem, itemDto);

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageHandler.handleImageUpload(image, "item", itemId, existingItem.getImageUrl());
            updatedItem.setImageUrl(imageUrl);
        }

        updatedItem = save(updatedItem);
        logger.info("Successfully updated item with ID: {}", itemId);
        return mapEntity(updatedItem, itemMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void deleteItem(Long profileId, Long itemId) {
        logger.info("Deleting item with ID: {} for profile ID: {}", itemId, profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateNotNull(itemId, "Item ID");

        Item item = findById(itemId);
        validateItemOwnership(item, profileId);

        imageHandler.handleImageDelete(item.getImageUrl());
        delete(itemId);
        logger.info("Successfully deleted item with ID: {}", itemId);
    }

    @Override
    public List<ItemResponseDto> getUserItems(Long profileId) {
        logger.info("Fetching all items for profile ID: {}", profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        Profile profile = getProfile(profileId);
        Wardrobe wardrobe = getWardrobe(profile.getId());

        return mapEntityList(
                itemRepository.findAllByWardrobeId(wardrobe.getId()),
                itemMapper::toResponseDto);
    }

    @Override
    public ItemResponseDto getItem(Long itemId) {
        logger.info("Fetching item with ID: {}", itemId);
        validationService.validateNotNull(itemId, "Item ID");
        return mapEntity(findById(itemId), itemMapper::toResponseDto);
    }

    @Override
    public ItemResponseDto getItemByName(String itemName) {
        logger.info("Fetching item by name: {}", itemName);
        validationService.validateStringNotEmpty(itemName, "Item name");

        Item item = itemRepository.findByName(itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with name: " + itemName));
        return mapEntity(item, itemMapper::toResponseDto);
    }

    @Override
    public Item getItemEntity(Long itemId) {
        logger.info("Fetching item entity with ID: {}", itemId);
        validationService.validateNotNull(itemId, "Item ID");
        return findById(itemId);
    }

    private void validateCreateItemParameters(Long profileId, Long wardrobeId, ItemDto itemDto) {
        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateNotNull(wardrobeId, "Wardrobe ID");
        validateItemDto(itemDto);
    }

    private void validateUpdateItemParameters(Long profileId, Long itemId, ItemDto itemDto) {
        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateNotNull(itemId, "Item ID");
        validateItemDto(itemDto);
    }

    private void validateItemDto(ItemDto itemDto) {
        validationService.validateNotNull(itemDto, "Item data");
        validationService.validateStringNotEmpty(itemDto.getName(), "Item name");
        validationService.validateStringNotEmpty(itemDto.getCategory(), "Category");
        validationService.validateExists(
                itemDto.getName().length() <= MAX_ITEM_NAME_LENGTH,
                String.format("Item name must not exceed %d characters", MAX_ITEM_NAME_LENGTH));
    }

    private Profile getProfile(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with ID: " + profileId));
    }

    private Wardrobe getWardrobe(Long profileId) {
        return wardrobeRepository.findByProfileId(profileId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Wardrobe not found for profile with ID: " + profileId));
    }

    private void validateWardrobeOwnership(Wardrobe wardrobe, Long profileId) {
        if (!wardrobe.getProfile().getId().equals(profileId)) {
            throw new SecurityException("Profile does not own this wardrobe");
        }
    }

    private void validateItemOwnership(Item item, Long profileId) {
        if (!item.getProfile().getId().equals(profileId)) {
            throw new SecurityException("Profile does not own this item");
        }
    }

    private void validateItemNameUniqueness(String itemName, Long wardrobeId) {
        itemRepository.findByNameAndWardrobeId(itemName, wardrobeId)
                .ifPresent(item -> {
                    throw new IllegalStateException(
                            String.format("Item with name '%s' already exists in this wardrobe", itemName));
                });
    }

    private Item buildItem(ItemDto itemDto, Profile profile, Wardrobe wardrobe) {
        return Item.builder()
                .name(itemDto.getName())
                .brand(itemDto.getBrand())
                .category(itemDto.getCategory())
                .size(itemDto.getSize())
                .color(itemDto.getColor())
                .wardrobe(wardrobe)
                .profile(profile)
                .build();
    }

    private Item updateItemFields(Item existingItem, ItemDto itemDto) {
        existingItem.setName(itemDto.getName());
        existingItem.setBrand(itemDto.getBrand());
        existingItem.setCategory(itemDto.getCategory());
        existingItem.setSize(itemDto.getSize());
        existingItem.setColor(itemDto.getColor());
        return existingItem;
    }
}
