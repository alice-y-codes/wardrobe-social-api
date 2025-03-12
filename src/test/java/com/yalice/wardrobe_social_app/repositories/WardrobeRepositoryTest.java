//package com.yalice.wardrobe_social_app.repositories;
//
//import com.yalice.wardrobe_social_app.entities.Profile;
//import com.yalice.wardrobe_social_app.entities.User;
//import com.yalice.wardrobe_social_app.entities.Wardrobe;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@ActiveProfiles("test")
//public class WardrobeRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private WardrobeRepository wardrobeRepository;
//
//    private User user;
//    private Profile profile;
//    private Wardrobe wardrobe1;
//    private Wardrobe wardrobe2;
//
//    @BeforeEach
//    public void setup() {
//        // Create user
//        user = new User();
//        user.setUsername("testuser");
//        user.setEmail("test@example.com");
//        user.setPassword("password");
//        entityManager.persist(user);
//
//        // Create profile
//        profile = new Profile();
//        profile.setUser(user);
//        profile.setBio("Test bio");
//        entityManager.persist(profile);
//
//        // Create wardrobes
//        wardrobe1 = new Wardrobe();
//        wardrobe1.setName("Casual Wardrobe");
//        wardrobe1.setProfile(profile);
//        entityManager.persist(wardrobe1);
//
//        wardrobe2 = new Wardrobe();
//        wardrobe2.setName("Formal Wardrobe");
//        wardrobe2.setProfile(profile);
//        entityManager.persist(wardrobe2);
//
//        entityManager.flush();
//    }
//
//    @Test
//    public void whenFindByProfileId_thenReturnWardrobe() {
//        // when
//        Optional<Wardrobe> found = wardrobeRepository.findByProfileId(profile.getId());
//
//        // then
//        assertThat(found).isPresent();
//        assertThat(found.get().getName()).isEqualTo("Casual Wardrobe");
//    }
//
//    @Test
//    public void whenExistsByUserIdAndName_thenReturnTrue() {
//        // when
//        Boolean exists = wardrobeRepository.existsByUserIdAndName(user.getId(), "Casual Wardrobe");
//
//        // then
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    public void whenExistsByUserIdAndName_withNonExistentName_thenReturnFalse() {
//        // when
//        Boolean exists = wardrobeRepository.existsByUserIdAndName(user.getId(), "Nonexistent Wardrobe");
//
//        // then
//        assertThat(exists).isFalse();
//    }
//
//    @Test
//    public void whenFindAllByUserId_thenReturnAllWardrobes() {
//        // when
//        List<Wardrobe> wardrobes = wardrobeRepository.findAllByUserId(user.getId());
//
//        // then
//        assertThat(wardrobes).hasSize(2);
//        assertThat(wardrobes).extracting(Wardrobe::getName)
//                .containsExactlyInAnyOrder("Casual Wardrobe", "Formal Wardrobe");
//    }
//
//    @Test
//    public void whenFindAllByUserId_withNonExistentUserId_thenReturnEmptyList() {
//        // when
//        List<Wardrobe> wardrobes = wardrobeRepository.findAllByUserId(999L);
//
//        // then
//        assertThat(wardrobes).isEmpty();
//    }
//}