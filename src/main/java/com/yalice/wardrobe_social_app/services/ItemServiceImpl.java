package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(Long userId, ItemDto itemDto) {
        if (itemDto == null || userId == null) {
            throw new IllegalArgumentException("User ID and Item cannot be null");
        }

        if (itemRepository.findByName(itemDto.getName()).isPresent()) {
            throw new IllegalStateException("Item with this name already exists");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setBrand(itemDto.getBrand());
        item.setCategory(itemDto.getCategory());
        item.setSize(itemDto.getSize());
        item.setColor(itemDto.getColor());
        item.setImageUrl(itemDto.getImageUrl());
        item.setUser(user); // Set the full User object

        user.getItems().add(item);
        userRepository.save(user); // Save the user to persist the relationship
        item = itemRepository.save(item); // Save the item

        return mapToItemResponseDto(item, user); // Return the DTO with userId
    }

    @Override
    public ItemResponseDto getItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID " + id));

        User user = item.getUser(); // Get the User object from Item

        return mapToItemResponseDto(item, user); // Return DTO with userId
    }

    @Override
    public List<ItemResponseDto> getAllItems(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        List<Item> items = itemRepository.findByUserId(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return items.stream()
                .map(item -> mapToItemResponseDto(item, user)) // Map each item to DTO with userId
                .collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto getItemByName(String itemName) {
        if (itemName == null || itemName.isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }

        Item item = itemRepository.findByName(itemName)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with name " + itemName));

        User user = item.getUser(); // Get the User object from Item

        return mapToItemResponseDto(item, user); // Return DTO with userId
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long id, ItemDto itemDto) {
        if (id == null || itemDto == null) {
            throw new IllegalArgumentException("Item ID and Item cannot be null");
        }

        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID " + id));

        existingItem.setName(itemDto.getName());
        existingItem.setBrand(itemDto.getBrand());
        existingItem.setCategory(itemDto.getCategory());
        existingItem.setSize(itemDto.getSize());
        existingItem.setColor(itemDto.getColor());
        existingItem.setImageUrl(itemDto.getImageUrl());

        itemRepository.saveAndFlush(existingItem); // Save the updated item
        User user = existingItem.getUser(); // Get the User object from Item

        return mapToItemResponseDto(existingItem, user); // Return updated DTO with userId
    }

    @Override
    @Transactional
    public boolean deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with ID " + id);
        }

        itemRepository.deleteById(id); // Delete the item
        return true;
    }

    /**
     * Maps an Item and User to ItemResponseDto.
     * Here, we're mapping the userId from the User object.
     */
    private ItemResponseDto mapToItemResponseDto(Item item, User user) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .brand(item.getBrand())
                .category(item.getCategory())
                .size(item.getSize())
                .color(item.getColor())
                .imageUrl(item.getImageUrl())
                .userId(user.getId()) // Include userId instead of User object
                .build();
    }
}
