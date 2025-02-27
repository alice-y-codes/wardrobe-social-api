package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Optional<Item> createItem(Long userId, Item item) {
        if (item == null) return Optional.empty();
        if (itemRepository.findByItemName(item.getName()).isPresent()) return Optional.empty();

        item.setUserId(userId);
        User user = userRepository.findById(userId).orElse(null);

        if(user == null) return Optional.empty();
        user.getItems().add(item);

        userRepository.save(user);
        Item savedItem = itemRepository.save(item);

        return Optional.of(savedItem);
    }

    public Optional<Item> getItem(Long itemId) {
        if (itemId == null) return Optional.empty();
        return itemRepository.findById(itemId);
    }

    public List<Item> getAllItems(Long userId) {
        if (userId == null) return List.of();
        return itemRepository.findByUserId(userId);
    }

    public Optional<Item> getItemByName(String itemName) {
        if (itemName == null || itemName.isEmpty()) return Optional.empty();
        return itemRepository.findByItemName(itemName);
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    public Item updateItem(Long itemId, Item item) {
        if (itemId == null) return null;
        if (item == null ) return itemRepository.findById(itemId).orElse(null);
        return itemRepository.saveAndFlush(item);
    }
}
