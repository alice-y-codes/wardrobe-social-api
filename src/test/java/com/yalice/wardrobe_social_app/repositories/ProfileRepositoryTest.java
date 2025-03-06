package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfileRepository profileRepository;

    private User user;
    private Profile profile;

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

        entityManager.flush();
    }

    @Test
    public void whenFindByUserId_thenReturnProfile() {
        // when
        Optional<Profile> found = profileRepository.findByUserId(user.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getBio()).isEqualTo("Test bio");
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    public void whenFindByUserId_withNonExistentUserId_thenReturnEmpty() {
        // when
        Optional<Profile> found = profileRepository.findByUserId(999L);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    public void whenSaveProfile_thenPersist() {
        // given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password");
        entityManager.persist(newUser);

        Profile newProfile = new Profile();
        newProfile.setUser(newUser);
        newProfile.setBio("New bio");

        // when
        Profile saved = profileRepository.save(newProfile);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBio()).isEqualTo("New bio");
        assertThat(saved.getUser().getId()).isEqualTo(newUser.getId());
    }

    @Test
    public void whenDeleteProfile_thenRemove() {
        // when
        profileRepository.delete(profile);
        entityManager.flush();

        // then
        Optional<Profile> found = profileRepository.findByUserId(user.getId());
        assertThat(found).isEmpty();
    }
}