//package com.yalice.wardrobe_social_app.repositories;
//
//import com.yalice.wardrobe_social_app.entities.Friendship;
//import com.yalice.wardrobe_social_app.entities.User;
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
//public class FriendRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private FriendRepository friendRepository;
//
//    private User user1;
//    private User user2;
//    private User user3;
//    private Friendship friendship1;
//    private Friendship friendship2;
//
//    @BeforeEach
//    void setUp() {
//        // Create test users
//        user1 = User.builder()
//                .username("user1")
//                .email("user1@example.com")
//                .password("password")
//                .provider(User.Provider.LOCAL)
//                .build();
//        entityManager.persist(user1);
//
//        user2 = User.builder()
//                .username("user2")
//                .email("user2@example.com")
//                .password("password")
//                .provider(User.Provider.LOCAL)
//                .build();
//        entityManager.persist(user2);
//
//        user3 = User.builder()
//                .username("user3")
//                .email("user3@example.com")
//                .password("password")
//                .provider(User.Provider.LOCAL)
//                .build();
//        entityManager.persist(user3);
//
//        // Create test friendships
//        friendship1 = Friendship.builder()
//                .sender(user1)
//                .recipient(user2)
//                .status(Friendship.FriendshipStatus.PENDING)
//                .build();
//        entityManager.persist(friendship1);
//
//        friendship2 = Friendship.builder()
//                .sender(user2)
//                .recipient(user3)
//                .status(Friendship.FriendshipStatus.ACCEPTED)
//                .build();
//        entityManager.persist(friendship2);
//
//        entityManager.flush();
//    }
//
//    @Test
//    void whenExistsBySenderIdAndRecipientId_thenReturnTrue() {
//        // when
//        boolean exists = friendRepository.existsBySenderIdAndRecipientId(user1.getId(), user2.getId());
//
//        // then
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    void whenFindByRecipientIdAndStatus_thenReturnFriendships() {
//        // when
//        List<Friendship> found = friendRepository.findByRecipientIdAndStatus(user2.getId(),
//                Friendship.FriendshipStatus.PENDING);
//
//        // then
//        assertThat(found).hasSize(1);
//        assertThat(found.get(0).getSender()).isEqualTo(user1);
//    }
//
//    @Test
//    void whenFindBySenderIdOrRecipientIdAndStatus_thenReturnFriendships() {
//        // when
//        List<Friendship> found = friendRepository.findBySenderIdOrRecipientIdAndStatus(user2.getId(),
//                Friendship.FriendshipStatus.ACCEPTED);
//
//        // then
//        assertThat(found).hasSize(1);
//        assertThat(found.get(0).getRecipient()).isEqualTo(user3);
//    }
//
//    @Test
//    void whenFindFriendshipBetweenUsers_thenReturnFriendship() {
//        // when
//        Optional<Friendship> found = friendRepository.findFriendshipBetweenUsers(user1.getId(), user2.getId());
//
//        // then
//        assertThat(found).isPresent();
//        assertThat(found.get().getSender()).isEqualTo(user1);
//        assertThat(found.get().getRecipient()).isEqualTo(user2);
//    }
//
//    @Test
//    void whenFindFriendshipBetweenUsers_withNonExistentFriendship_thenReturnEmpty() {
//        // when
//        Optional<Friendship> found = friendRepository.findFriendshipBetweenUsers(user1.getId(), user3.getId());
//
//        // then
//        assertThat(found).isEmpty();
//    }
//}