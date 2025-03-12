package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByName(String itemName);
    List<Item> findByProfileId(Long profileId);
    Optional<Item> findByNameAndWardrobeId(String itemName, Long wardrobeId);
    List<Item> findAllByWardrobeId(Long wardrobeId);
}
