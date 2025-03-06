package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Friendship;
import com.yalice.wardrobe_social_app.entities.User;
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
public class FriendRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FriendRepository friendRepository;

    private User user1;
    private User user2;
    private User user3;
    private Friendship friendship1;
    private Friendship friendship2;

    @BeforeEach
    public void setup() {
        // Create users
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password");
        entityManager.persist(user1);

        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password");
        entityManager.persist(user2);

        user3 = new User();
        user3.setUsername("user3");
        user3.setEmail("user3@example.com");
        user3.setPassword("password");
        entityManager.persist(user3);

        // Create friendships
        friendship1 = new Friendship();
        friendship1.setSender(user1);
        friendship1.setRecipient(user2);
        friendship1.setStatus(Friendship.FriendshipStatus.PENDING);
        entityManager.persist(friendship1);

        friendship2 = new Friendship();
        friendship2.setSender(user2);
        friendship2.setRecipient(user3);
        friendship2.setStatus(Friendship.FriendshipStatus.ACCEPTED);
        entityManager.persist(friendship2);

        entityManager.flush();
    }

    @Test
    public void whenExistsBySenderIdAndRecipientId_thenReturnTrue() {
        // when
        boolean exists = friendRepository.existsBySenderIdAndRecipientId(user1.getId(), user2.getId());

        // then
        assertThat(exists).isTrue();
    }

    @Test
    public void whenExistsBySenderIdAndRecipientId_withNonExistentFriendship_thenReturnFalse() {
        // when
        boolean exists = friendRepository.existsBySenderIdAndRecipientId(user1.getId(), user3.getId());

        // then
        assertThat(exists).isFalse();
    }

    @Test
    public void whenFindByRecipientIdAndStatus_thenReturnMatchingFriendships() {
        // when
        List<Friendship> pendingRequests = friendRepository.findByRecipientIdAndStatus(
                user2.getId(),
                Friendship.FriendshipStatus.PENDING);

        // then
        assertThat(pendingRequests).hasSize(1);
        assertThat(pendingRequests.getFirst().getSender().getId()).isEqualTo(user1.getId());
    }

    @Test
    public void whenFindBySenderIdOrRecipientIdAndStatus_thenReturnMatchingFriendships() {
        // when
        List<Friendship> acceptedFriendships = friendRepository.findBySenderIdOrRecipientIdAndStatus(
                user2.getId(),
                Friendship.FriendshipStatus.ACCEPTED);

        // then
        assertThat(acceptedFriendships).hasSize(1);
        assertThat(acceptedFriendships.getFirst().getRecipient().getId()).isEqualTo(user3.getId());
    }

    @Test
    public void whenFindFriendshipBetweenUsers_thenReturnFriendship() {
        // when
        Optional<Friendship> found = friendRepository.findFriendshipBetweenUsers(user1.getId(), user2.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getSender().getId()).isEqualTo(user1.getId());
        assertThat(found.get().getRecipient().getId()).isEqualTo(user2.getId());
    }

    @Test
    public void whenFindFriendshipBetweenUsers_withNonExistentFriendship_thenReturnEmpty() {
        // when
        Optional<Friendship> found = friendRepository.findFriendshipBetweenUsers(user1.getId(), user3.getId());

        // then
        assertThat(found).isEmpty();
    }
}