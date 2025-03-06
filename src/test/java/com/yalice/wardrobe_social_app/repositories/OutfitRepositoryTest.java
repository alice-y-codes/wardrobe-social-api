package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OutfitRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OutfitRepository outfitRepository;

    private User user;
    private Profile profile;
    private Outfit outfit1;
    private Outfit outfit2;
    private Outfit outfit3;

    @BeforeEach
    public void setup() {
        // Create user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);

        // Create profile
        profile = new Profile();
        profile.setUser(user);
        profile.setBio("Test bio");
        entityManager.persist(profile);

        // Create outfits
        outfit1 = new Outfit();
        outfit1.setName("Summer Casual");
        outfit1.setSeason("SUMMER");
        outfit1.setCategory("CASUAL");
        outfit1.setProfile(profile);
        entityManager.persist(outfit1);

        outfit2 = new Outfit();
        outfit2.setName("Winter Formal");
        outfit2.setSeason("WINTER");
        outfit2.setCategory("FORMAL");
        outfit2.setProfile(profile);
        entityManager.persist(outfit2);

        outfit3 = new Outfit();
        outfit3.setName("Summer Party");
        outfit3.setSeason("SUMMER");
        outfit3.setCategory("PARTY");
        outfit3.setProfile(profile);
        entityManager.persist(outfit3);

        entityManager.flush();
    }

    @Test
    public void whenFindByProfileId_thenReturnAllOutfits() {
        // when
        List<Outfit> outfits = outfitRepository.findByProfileId(profile.getId());

        // then
        assertThat(outfits).hasSize(3);
        assertThat(outfits).extracting(Outfit::getName)
                .containsExactlyInAnyOrder("Summer Casual", "Winter Formal", "Summer Party");
    }

    @Test
    public void whenFindByProfileIdAndSeason_thenReturnFilteredOutfits() {
        // when
        List<Outfit> summerOutfits = outfitRepository.findByProfileIdAndSeason(profile.getId(), "SUMMER");

        // then
        assertThat(summerOutfits).hasSize(2);
        assertThat(summerOutfits).extracting(Outfit::getName)
                .containsExactlyInAnyOrder("Summer Casual", "Summer Party");
    }

    @Test
    public void whenFindByProfileId_withNonExistentProfileId_thenReturnEmptyList() {
        // when
        List<Outfit> outfits = outfitRepository.findByProfileId(999L);

        // then
        assertThat(outfits).isEmpty();
    }

    @Test
    public void whenFindByProfileIdAndSeason_withNonExistentSeason_thenReturnEmptyList() {
        // when
        List<Outfit> outfits = outfitRepository.findByProfileIdAndSeason(profile.getId(), "SPRING");

        // then
        assertThat(outfits).isEmpty();
    }
}