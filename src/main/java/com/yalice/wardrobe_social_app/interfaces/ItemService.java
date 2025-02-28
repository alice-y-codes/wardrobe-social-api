package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Optional<Item> createItem(Long userId, Item item);
    List<Item> getAllItems(Long userId);
    Optional<Item> getItem(Long id);
    Optional<Item> getItemByName(String itemName);
    Item updateItem(Long id, Item item);
    void deleteItem(Long id);
}
