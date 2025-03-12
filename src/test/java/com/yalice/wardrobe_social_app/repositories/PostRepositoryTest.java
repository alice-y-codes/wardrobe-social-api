//package com.yalice.wardrobe_social_app.repositories;
//
//import com.yalice.wardrobe_social_app.entities.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@ActiveProfiles("test")
//public class PostRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    private User user1;
//    private User user2;
//    private Profile profile1;
//    private Profile profile2;
//    private Outfit outfit1;
//    private Outfit outfit2;
//    private Post post1;
//    private Post post2;
//
//    @BeforeEach
//    public void setup() {
//        // Create users
//        user1 = new User();
//        user1.setUsername("user1");
//        user1.setEmail("user1@example.com");
//        user1.setPassword("password");
//        entityManager.persist(user1);
//
//        user2 = new User();
//        user2.setUsername("user2");
//        user2.setEmail("user2@example.com");
//        user2.setPassword("password");
//        entityManager.persist(user2);
//
//        // Create profiles
//        profile1 = new Profile();
//        profile1.setUser(user1);
//        profile1.setBio("Test bio 1");
//        entityManager.persist(profile1);
//
//        profile2 = new Profile();
//        profile2.setUser(user2);
//        profile2.setBio("Test bio 2");
//        entityManager.persist(profile2);
//
//        // Create outfits
//        outfit1 = new Outfit();
//        outfit1.setName("Summer outfit");
//        outfit1.setSeason("SUMMER");
//        outfit1.setCategory("CASUAL");
//        outfit1.setProfile(profile1);
//        entityManager.persist(outfit1);
//
//        outfit2 = new Outfit();
//        outfit2.setName("Winter outfit");
//        outfit2.setSeason("WINTER");
//        outfit2.setCategory("FORMAL");
//        outfit2.setProfile(profile2);
//        entityManager.persist(outfit2);
//
//        // Create posts
//        post1 = new Post();
//        post1.setProfile(profile1);
//        post1.setOutfit(outfit1);
//        post1.setTitle("Summer Vibes");
//        post1.setContent("Enjoying the summer weather!");
//        post1.setVisibility(Post.PostVisibility.PUBLIC);
//        post1.setCreatedAt(LocalDateTime.now().minusDays(1));
//        entityManager.persist(post1);
//
//        post2 = new Post();
//        post2.setProfile(profile2);
//        post2.setOutfit(outfit2);
//        post2.setTitle("Winter Formal");
//        post2.setContent("Ready for the winter party!");
//        post2.setVisibility(Post.PostVisibility.FRIENDS_ONLY);
//        post2.setCreatedAt(LocalDateTime.now());
//        entityManager.persist(post2);
//
//        entityManager.flush();
//    }
//
//    @Test
//    public void whenFindByUserIdAndVisibility_thenReturnMatchingPosts() {
//        // when
//        Page<Post> publicPosts = postRepository.findByUserIdAndVisibilityOrderByCreatedAtDesc(
//                user1.getId(),
//                Post.PostVisibility.PUBLIC,
//                PageRequest.of(0, 10));
//
//        // then
//        assertThat(publicPosts.getContent()).hasSize(1);
//        assertThat(publicPosts.getContent().getFirst().getTitle()).isEqualTo("Summer Vibes");
//    }
//
//    @Test
//    public void whenFindFeedPostsForUser_thenReturnPostsFromUsers() {
//        // when
//        Page<Post> feedPosts = postRepository.findFeedPostsForUser(
//                Arrays.asList(user1.getId(), user2.getId()),
//                PageRequest.of(0, 10));
//
//        // then
//        assertThat(feedPosts.getContent()).hasSize(2);
//        assertThat(feedPosts.getContent())
//                .extracting(Post::getTitle)
//                .containsExactly("Winter Formal", "Summer Vibes");
//    }
//
//    @Test
//    public void whenFindFeedPostsForUserBySeason_thenReturnFilteredPosts() {
//        // when
//        Page<Post> summerPosts = postRepository.findFeedPostsForUserBySeason(
//                Arrays.asList(user1.getId(), user2.getId()),
//                "SUMMER",
//                PageRequest.of(0, 10));
//
//        // then
//        assertThat(summerPosts.getContent()).hasSize(1);
//        assertThat(summerPosts.getContent().getFirst().getOutfit().getSeason()).isEqualTo("SUMMER");
//    }
//
//    @Test
//    public void whenFindFeedPostsForUserByCategory_thenReturnFilteredPosts() {
//        // when
//        Page<Post> formalPosts = postRepository.findFeedPostsForUserByCategory(
//                Arrays.asList(user1.getId(), user2.getId()),
//                "FORMAL",
//                PageRequest.of(0, 10));
//
//        // then
//        assertThat(formalPosts.getContent()).hasSize(1);
//        assertThat(formalPosts.getContent().getFirst().getOutfit().getCategory()).isEqualTo("FORMAL");
//    }
//}