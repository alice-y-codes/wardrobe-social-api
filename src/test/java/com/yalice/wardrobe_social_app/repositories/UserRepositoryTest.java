package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.User;
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
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByUsername_thenReturnUser() {
        // given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .provider(User.Provider.LOCAL)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByUsername(user.getUsername());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void whenSaveUser_thenPersist() {
        // given
        User user = User.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password")
                .provider(User.Provider.LOCAL)
                .build();

        // when
        User savedUser = entityManager.persist(user);
        entityManager.flush();

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("newuser");
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    public void whenDeleteUser_thenRemove() {
        // given
        User user = User.builder()
                .username("deleteuser")
                .email("delete@example.com")
                .password("password")
                .provider(User.Provider.LOCAL)
                .build();
        User savedUser = entityManager.persist(user);
        entityManager.flush();

        // when
        entityManager.remove(savedUser);
        entityManager.flush();

        // then
        Optional<User> found = userRepository.findById(savedUser.getId());
        assertThat(found).isEmpty();
    }

    @Test
    public void whenFindById_thenReturnUser() {
        // given
        User user = User.builder()
                .username("finduser")
                .email("find@example.com")
                .password("password")
                .provider(User.Provider.LOCAL)
                .build();
        User savedUser = entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findById(savedUser.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("finduser");
        assertThat(found.get().getEmail()).isEqualTo("find@example.com");
    }

    @Test
    public void whenFindById_withNonExistentId_thenReturnEmpty() {
        // when
        Optional<User> found = userRepository.findById(999L);

        // then
        assertThat(found).isEmpty();
    }
}