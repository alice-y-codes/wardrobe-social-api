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
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findByUsername(user.getUsername());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void whenFindByUsernameContainingIgnoreCase_thenReturnMatchingUsers() {
        // given
        User user1 = new User();
        user1.setUsername("JohnDoe");
        user1.setEmail("john@example.com");
        user1.setPassword("password");

        User user2 = new User();
        user2.setUsername("johnnyCash");
        user2.setEmail("johnny@example.com");
        user2.setPassword("password");

        User user3 = new User();
        user3.setUsername("BobSmith");
        user3.setEmail("bob@example.com");
        user3.setPassword("password");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // when
        List<User> foundUsers = userRepository.findByUsernameContainingIgnoreCase("john");

        // then
        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).extracting(User::getUsername)
                .containsExactlyInAnyOrder("JohnDoe", "johnnyCash");
    }

    @Test
    public void whenFindByUsername_withNonExistentUsername_thenReturnEmpty() {
        // when
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    public void whenSaveUser_thenPersist() {
        // given
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("newuser@example.com");
        user.setPassword("password");

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("newuser");
        assertThat(saved.getEmail()).isEqualTo("newuser@example.com");
    }

    @Test
    public void whenDeleteUser_thenRemove() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();

        // when
        userRepository.delete(user);
        entityManager.flush();

        // then
        Optional<User> found = userRepository.findById(user.getId());
        assertThat(found).isEmpty();
    }

    @Test
    public void whenFindById_thenReturnUser() {
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> found = userRepository.findById(user.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void whenFindById_withNonExistentId_thenReturnEmpty() {
        // when
        Optional<User> found = userRepository.findById(999L);

        // then
        assertThat(found).isEmpty();
    }
}