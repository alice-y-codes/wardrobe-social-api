package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public Optional<Item> createItem(Long userId, Item item) {
        if (item == null)
            return Optional.empty();

        // Check if an item with the same name already exists
        Optional<Item> existingItem = itemRepository.findByName(item.getName());
        if (existingItem.isPresent())
            return Optional.empty();

        // Verify user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty())
            return Optional.empty();

        // Set the userId and add to user's items collection
        item.setUserId(userId);
        User user = userOptional.get();
        user.getItems().add(item);

        // Save the user to persist the relationship
        userRepository.save(user);

        Item savedItem = itemRepository.save(item);
        return Optional.of(savedItem);
    }

    @Override
    public Optional<Item> getItem(Long itemId) {
        if (itemId == null)
            return Optional.empty();
        return itemRepository.findById(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        if (userId == null)
            return List.of();
        return itemRepository.findByUserId(userId);
    }

    @Override
    public Optional<Item> getItemByName(String itemName) {
        if (itemName == null || itemName.isEmpty())
            return Optional.empty();
        return itemRepository.findByName(itemName);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public Item updateItem(Long itemId, Item item) {
        if (itemId == null) {
            return null;
        }

        if (item == null) {
            return itemRepository.findById(itemId).orElse(null);
        }

        Optional<Item> existingItemOptional = itemRepository.findById(itemId);

        if (existingItemOptional.isEmpty()) {
            return null;
        }

        Item existingItem = existingItemOptional.get();

        // Update fields but preserve the userId
        Long userId = existingItem.getUserId();

        existingItem.setName(item.getName());
        existingItem.setBrand(item.getBrand());
        existingItem.setCategory(item.getCategory());
        existingItem.setSize(item.getSize());
        existingItem.setColor(item.getColor());
        existingItem.setImageUrl(item.getImageUrl());
        existingItem.setUserId(userId); // Explicitly set the userId back

        Item savedItem = itemRepository.saveAndFlush(existingItem);

        if (savedItem == null) {
            return existingItem;
        }

        return savedItem;
    }
}
