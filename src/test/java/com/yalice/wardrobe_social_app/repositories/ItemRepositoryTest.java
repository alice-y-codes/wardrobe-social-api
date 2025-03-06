package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private Profile profile;
    private Wardrobe wardrobe;
    private Item item1;
    private Item item2;

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

        // Create wardrobe
        wardrobe = new Wardrobe();
        wardrobe.setName("Test Wardrobe");
        wardrobe.setProfile(profile);
        entityManager.persist(wardrobe);

        // Create items
        item1 = new Item();
        item1.setName("Blue Jeans");
        item1.setCategory("PANTS");
        item1.setProfile(profile);
        item1.setWardrobe(wardrobe);
        entityManager.persist(item1);

        item2 = new Item();
        item2.setName("White T-Shirt");
        item2.setCategory("TOPS");
        item2.setProfile(profile);
        item2.setWardrobe(wardrobe);
        entityManager.persist(item2);

        entityManager.flush();
    }

    @Test
    public void whenFindByName_thenReturnItem() {
        // when
        Optional<Item> found = itemRepository.findByName("Blue Jeans");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Blue Jeans");
        assertThat(found.get().getCategory()).isEqualTo("PANTS");
    }

    @Test
    public void whenFindByProfileId_thenReturnItems() {
        // when
        List<Item> items = itemRepository.findByProfileId(profile.getId());

        // then
        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getName)
                .containsExactlyInAnyOrder("Blue Jeans", "White T-Shirt");
    }

    @Test
    public void whenFindByNameAndWardrobeId_thenReturnItem() {
        // when
        Optional<Item> found = itemRepository.findByNameAndWardrobeId("Blue Jeans", wardrobe.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Blue Jeans");
        assertThat(found.get().getWardrobe().getId()).isEqualTo(wardrobe.getId());
    }

    @Test
    public void whenFindAllByWardrobeId_thenReturnItems() {
        // when
        List<Item> items = itemRepository.findAllByWardrobeId(wardrobe.getId());

        // then
        assertThat(items).hasSize(2);
        assertThat(items).extracting(Item::getName)
                .containsExactlyInAnyOrder("Blue Jeans", "White T-Shirt");
    }

    @Test
    public void whenFindByName_withNonExistentName_thenReturnEmpty() {
        // when
        Optional<Item> found = itemRepository.findByName("Nonexistent Item");

        // then
        assertThat(found).isEmpty();
    }
}