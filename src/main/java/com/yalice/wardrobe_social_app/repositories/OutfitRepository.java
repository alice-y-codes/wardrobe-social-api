package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    // Additional query methods can be defined here
}
