package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {

    /**
     * Find all outfits for a user
     * 
     * @param userId the ID of the user
     * @return a list of outfits
     */
    List<Outfit> findByUserId(Long userId);

    /**
     * Find outfits by user ID and occasion
     * 
     * @param userId   the ID of the user
     * @param occasion the occasion to filter by
     * @return a list of outfits
     */
    List<Outfit> findByUserIdAndOccasion(Long userId, String occasion);
}
