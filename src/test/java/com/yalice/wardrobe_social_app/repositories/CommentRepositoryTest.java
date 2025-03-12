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
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@ActiveProfiles("test")
//public class CommentRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    private User user1;
//    private User user2;
//    private Profile profile1;
//    private Profile profile2;
//    private Post post;
//    private Comment comment1;
//    private Comment comment2;
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
//        // Create post
//        post = new Post();
//        post.setProfile(profile1);
//        post.setTitle("Test Post");
//        post.setContent("Test content");
//        post.setVisibility(Post.PostVisibility.PUBLIC);
//        entityManager.persist(post);
//
//        // Create comments
//        comment1 = new Comment();
//        comment1.setPost(post);
//        comment1.setProfile(profile1);
//        comment1.setContent("First comment");
//        comment1.setCreatedAt(LocalDateTime.now().minusHours(1));
//        entityManager.persist(comment1);
//
//        comment2 = new Comment();
//        comment2.setPost(post);
//        comment2.setProfile(profile2);
//        comment2.setContent("Second comment");
//        comment2.setCreatedAt(LocalDateTime.now());
//        entityManager.persist(comment2);
//
//        entityManager.flush();
//    }
//
//    @Test
//    public void whenFindByPostIdOrderByCreatedAtAsc_thenReturnCommentsInOrder() {
//        // when
//        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId());
//
//        // then
//        assertThat(comments).hasSize(2);
//        assertThat(comments).extracting(Comment::getContent)
//                .containsExactly("First comment", "Second comment");
//    }
//
//    @Test
//    public void whenFindByPostIdOrderByCreatedAtDesc_thenReturnCommentsInOrder() {
//        // when
//        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(post.getId());
//
//        // then
//        assertThat(comments).hasSize(2);
//        assertThat(comments).extracting(Comment::getContent)
//                .containsExactly("Second comment", "First comment");
//    }
//
//    @Test
//    public void whenFindByPostIdOrderByCreatedAtAscWithPagination_thenReturnPagedComments() {
//        // when
//        Page<Comment> commentPage = commentRepository.findByPostIdOrderByCreatedAtAsc(
//                post.getId(),
//                PageRequest.of(0, 1));
//
//        // then
//        assertThat(commentPage.getContent()).hasSize(1);
//        assertThat(commentPage.getTotalElements()).isEqualTo(2);
//        assertThat(commentPage.getContent().getFirst().getContent()).isEqualTo("First comment");
//    }
//
//    @Test
//    public void whenFindByPostIdOrderByCreatedAtAsc_withNonExistentPostId_thenReturnEmptyList() {
//        // when
//        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(999L);
//
//        // then
//        assertThat(comments).isEmpty();
//    }
//}