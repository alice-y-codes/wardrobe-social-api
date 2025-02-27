package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    // Find item by name
    Optional<Item> findByName(String name);

    // Find items by user ID - using the user_id column in the database
    List<Item> findByUserId(Long userId);
}
