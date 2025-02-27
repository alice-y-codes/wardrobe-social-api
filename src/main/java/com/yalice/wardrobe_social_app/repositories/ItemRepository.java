package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // Additional query methods can be defined here
    Optional<Item> findByItemName(String itemName);
    List<Item> findByUserId(Long userId);
}
