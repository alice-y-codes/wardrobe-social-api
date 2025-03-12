package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    List<Outfit> findByProfileId(Long profileId);
    List<Outfit> findByProfileIdAndSeason(Long profileId, String season);
}
