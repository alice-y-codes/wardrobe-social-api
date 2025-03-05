package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.services.helpers.PostServiceHelper;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.PostServiceImpl;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UpdatePostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserSearchService userSearchService;

    @Mock
    private PostServiceHelper postServiceHelper;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post postEntity;
    private PostDto postDto;
    private PostResponseDto postResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder().id(1L).username("user1").email("user1@example.com").build();
        Outfit outfit = Outfit.builder().id(1L).name("Test Outfit").user(user).build();

        postDto = PostDto.builder()
                .title("New Post")
                .content("Test post content")
                .outfitId(outfit.getId())
                .visibility("PUBLIC")
                .build();

        postEntity = Post.builder()
                .id(1L)
                .title("Old Post")
                .content("Old post content")
                .outfit(outfit)
                .visibility(PostVisibility.PUBLIC)
                .user(user)
                .build();

        postResponseDto = PostResponseDto.builder()
                .id(1L)
                .title("Updated Post")
                .content("Updated post content")
                .outfitId(outfit.getId())
                .visibility("PRIVATE")
                .username(user.getUsername())
                .build();
    }

    @Test
    void updatePost_whenPostExists_shouldUpdatePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(postEntity));
        when(postRepository.saveAndFlush(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostResponseDto result = postService.updatePost(1L, user.getId(), postDto);

        assertNotNull(result);
        assertEquals(postDto.getContent(), result.getContent());
        verify(postRepository, times(1)).saveAndFlush(any(Post.class));
    }

    @Test
    void updatePost_whenPostDoesNotExist_shouldThrowResourceNotFoundException() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(1L, user.getId(), postDto));
    }

    @Test
    void likePost_whenUserLikesForFirstTime_shouldLikePost() {
        when(postRepository.findById(postEntity.getId())).thenReturn(Optional.of(postEntity));
        when(userSearchService.findById(user.getId())).thenReturn(Optional.of(user));
        when(postServiceHelper.hasUserLikedPost(postEntity.getId(), user.getId())).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(new Like());
        when(postRepository.save(postEntity)).thenReturn(postEntity);

        boolean result = postService.likePost(postEntity.getId(), user.getId());

        assertTrue(result);
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(postRepository, times(1)).save(postEntity);
    }

    @Test
    void likePost_whenUserAlreadyLiked_shouldReturnFalse() {
        when(postRepository.findById(postEntity.getId())).thenReturn(Optional.of(postEntity));
        when(userSearchService.findById(user.getId())).thenReturn(Optional.of(user));
        when(postServiceHelper.hasUserLikedPost(postEntity.getId(), user.getId())).thenReturn(true);

        boolean result = postService.likePost(postEntity.getId(), user.getId());

        assertFalse(result);
        verify(likeRepository, never()).save(any(Like.class));
        verify(postRepository, never()).save(postEntity);
    }

    @Test
    void unlikePost_whenUserHasLiked_shouldUnlikePost() {
        Like like = Like.builder().post(postEntity).user(user).build();
        when(postRepository.findById(postEntity.getId())).thenReturn(Optional.of(postEntity));
        when(userSearchService.findById(user.getId())).thenReturn(Optional.of(user));
        when(postServiceHelper.hasUserLikedPost(postEntity.getId(), user.getId())).thenReturn(true);
        when(likeRepository.findByPostAndUser(postEntity, user)).thenReturn(Optional.of(like));

        boolean result = postService.unlikePost(postEntity.getId(), user.getId());

        assertTrue(result);
        verify(likeRepository, times(1)).delete(like);
        verify(postRepository, times(1)).save(postEntity);
    }

    @Test
    void unlikePost_whenUserHasNotLiked_shouldReturnFalse() {
        when(postRepository.findById(postEntity.getId())).thenReturn(Optional.of(postEntity));
        when(userSearchService.findById(user.getId())).thenReturn(Optional.of(user));
        when(postServiceHelper.hasUserLikedPost(postEntity.getId(), user.getId())).thenReturn(false);

        boolean result = postService.unlikePost(postEntity.getId(), user.getId());

        assertFalse(result);
        verify(likeRepository, never()).delete(any(Like.class));
        verify(postRepository, never()).save(postEntity);
    }
}
