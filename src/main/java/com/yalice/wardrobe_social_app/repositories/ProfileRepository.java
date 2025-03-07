package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);
}