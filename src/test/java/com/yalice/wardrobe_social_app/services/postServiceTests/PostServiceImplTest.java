package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.*;
import com.yalice.wardrobe_social_app.exceptions.PostAccessException;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.PostServiceImpl;
import com.yalice.wardrobe_social_app.services.helpers.PostServiceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private OutfitService outfitService;

    @Mock
    private PostServiceHelper postServiceHelper;

    @Mock
    private Profile profile;

    @Mock
    private User user;

    @Mock
    private Post post;

    @Mock
    private PostDto postDto;

    @Mock
    private Outfit outfit;

    @Mock
    private Like like;

    private Long postId = 1L;
    private Long profileId = 1L;
    private Long outfitId = 1L;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(userId);

        // Mock common objects
        profile = new Profile();
        profile.setId(profileId);
        profile.setUser(user);

        outfit = new Outfit();
        outfit.setId(outfitId);

        post = new Post();
        post.setId(postId);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setProfile(profile);
        post.setVisibility(Post.PostVisibility.PUBLIC);
        post.setOutfit(outfit);


        postDto = new PostDto();
        postDto.setTitle("Test Title");
        postDto.setFeatureImage("http://image.url");
        postDto.setContent("Test Content");
        postDto.setOutfitId(outfitId);
        postDto.setVisibility("PUBLIC");
    }

    @Test
    void testCreatePost_Success() {
        when(profileService.getProfileEntityById(profileId)).thenReturn(profile);
        when(outfitService.getOutfitEntityById(outfitId)).thenReturn(outfit);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponseDto postResponseDto = postService.createPost(profileId, postDto);

        assertNotNull(postResponseDto);
        assertEquals(postId, postResponseDto.getId());
        assertEquals("Test Title", postResponseDto.getTitle());
        assertEquals("Test Content", postResponseDto.getContent());
        assertEquals("PUBLIC", postResponseDto.getVisibility());

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testCreatePost_ProfileNotFound() {
        when(profileService.getProfileEntityById(profileId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(profileId, postDto));

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testCreatePost_OutfitNotFound() {
        when(profileService.getProfileEntityById(profileId)).thenReturn(profile);
        when(outfitService.getOutfitEntityById(outfitId)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(profileId, postDto));

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testGetPost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postServiceHelper.isPostAccessibleToUser(post, profileId)).thenReturn(true);

        PostResponseDto postResponseDto = postService.getPost(postId, profileId);

        assertNotNull(postResponseDto);
        assertEquals(postId, postResponseDto.getId());

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testGetPost_PostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(postId, profileId));

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testGetPost_NotAccessible() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postServiceHelper.isPostAccessibleToUser(post, profileId)).thenReturn(false);

        assertThrows(PostAccessException.class, () -> postService.getPost(postId, profileId));

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testUpdatePost_Success() {
        PostDto updateDto = new PostDto();
        updateDto.setContent("Updated Content");
        updateDto.setVisibility("PRIVATE");
        updateDto.setOutfitId(outfitId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(profileService.getProfileEntityById(profileId)).thenReturn(profile);
        when(outfitService.getOutfitEntityById(outfitId)).thenReturn(outfit);
        when(postRepository.saveAndFlush(any(Post.class))).thenReturn(post);

        PostResponseDto updatedPost = postService.updatePost(postId, profileId, updateDto);

        assertNotNull(updatedPost);
        assertEquals("Updated Content", updatedPost.getContent());
        assertEquals("PRIVATE", updatedPost.getVisibility());

        verify(postRepository, times(1)).saveAndFlush(any(Post.class));
    }

    @Test
    void testUpdatePost_NotAuthorized() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(PostAccessException.class, () -> postService.updatePost(postId, 2L, postDto));

        verify(postRepository, never()).saveAndFlush(any(Post.class));
    }

    @Test
    void testDeletePost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId, profileId);

        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    void testDeletePost_PostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePost(postId, profileId));

        verify(postRepository, never()).deleteById(postId);
    }

    @Test
    void testDeletePost_NotAuthorized() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(PostAccessException.class, () -> postService.deletePost(postId, 2L));

        verify(postRepository, never()).deleteById(postId);
    }

    @Test
    void testToggleLikePost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(profileService.getProfileEntityById(profileId)).thenReturn(profile);
        when(postServiceHelper.hasProfileLikedPost(postId, profileId)).thenReturn(false);

        boolean result = postService.toggleLikePost(postId, profileId);

        assertTrue(result);
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testToggleLikePost_LikeNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(profileService.getProfileEntityById(profileId)).thenReturn(profile);
        when(postServiceHelper.hasProfileLikedPost(postId, profileId)).thenReturn(true);
        when(likeRepository.findByPostAndProfile(post, profile)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.toggleLikePost(postId, profileId));
    }

    @Test
    void testToggleLikePost_PostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.toggleLikePost(postId, profileId));

        verify(likeRepository, never()).save(any(Like.class));
        verify(postRepository, never()).save(any(Post.class));
    }
}
