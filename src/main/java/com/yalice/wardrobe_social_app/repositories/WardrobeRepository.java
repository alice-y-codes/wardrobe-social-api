package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Wardrobe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardrobeRepository extends JpaRepository<Wardrobe, Long> {
    Optional<Wardrobe> findByUserId(Long userId);
    Boolean existsByUserIdAndName(Long userId, String wardrobeName);
    List<Wardrobe> findAllByUserId(Long userId);
}
