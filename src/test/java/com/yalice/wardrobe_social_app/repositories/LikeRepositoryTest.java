//package com.yalice.wardrobe_social_app.repositories;
//
//import com.yalice.wardrobe_social_app.entities.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@ActiveProfiles("test")
//public class LikeRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private LikeRepository likeRepository;
//
//    private User user;
//    private Profile profile;
//    private Post post;
//    private Like like;
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
//        // Create post
//        post = new Post();
//        post.setProfile(profile);
//        post.setTitle("Test Post");
//        post.setContent("Test content");
//        post.setVisibility(Post.PostVisibility.PUBLIC);
//        entityManager.persist(post);
//
//        // Create like
//        like = new Like();
//        like.setPost(post);
//        like.setProfile(profile);
//        entityManager.persist(like);
//
//        entityManager.flush();
//    }
//
//    @Test
//    public void whenFindByPostAndProfile_thenReturnLike() {
//        // when
//        Optional<Like> found = likeRepository.findByPostAndProfile(post, profile);
//
//        // then
//        assertThat(found).isPresent();
//        assertThat(found.get().getPost().getId()).isEqualTo(post.getId());
//        assertThat(found.get().getProfile().getId()).isEqualTo(profile.getId());
//    }
//
//    @Test
//    public void whenFindByPostAndProfile_withNonExistentLike_thenReturnEmpty() {
//        // Create another profile that hasn't liked the post
//        Profile otherProfile = new Profile();
//        otherProfile.setUser(user);
//        otherProfile.setBio("Other bio");
//        entityManager.persist(otherProfile);
//
//        // when
//        Optional<Like> found = likeRepository.findByPostAndProfile(post, otherProfile);
//
//        // then
//        assertThat(found).isEmpty();
//    }
//
//    @Test
//    public void whenExistsByPostAndProfile_thenReturnTrue() {
//        // when
//        boolean exists = likeRepository.existsByPostAndProfile(post, profile);
//
//        // then
//        assertThat(exists).isTrue();
//    }
//
//    @Test
//    public void whenExistsByPostAndProfile_withNonExistentLike_thenReturnFalse() {
//        // Create another profile that hasn't liked the post
//        Profile otherProfile = new Profile();
//        otherProfile.setUser(user);
//        otherProfile.setBio("Other bio");
//        entityManager.persist(otherProfile);
//
//        // when
//        boolean exists = likeRepository.existsByPostAndProfile(post, otherProfile);
//
//        // then
//        assertThat(exists).isFalse();
//    }
//}