package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.*;
import com.yalice.wardrobe_social_app.exceptions.PostAccessException;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.ImageService;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.mappers.PostMapper;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.core.PostServiceImpl;
import com.yalice.wardrobe_social_app.services.helpers.PostServiceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private ProfileService profileService;
    @Mock
    private OutfitService outfitService;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostServiceHelper postServiceHelper;
    @Mock
    private ImageService imageService;

    @InjectMocks
    private PostServiceImpl postService;

    private Profile profile;
    private Post post;
    private Outfit outfit;
    private PostDto postDto;
    private MockMultipartFile mockImage;

    private static final Long POST_ID = 1L;
    private static final Long PROFILE_ID = 1L;
    private static final Long OUTFIT_ID = 1L;
    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(USER_ID);

        profile = new Profile();
        profile.setId(PROFILE_ID);
        profile.setUser(user);

        outfit = new Outfit();
        outfit.setId(OUTFIT_ID);

        post = new Post();
        post.setId(POST_ID);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setProfile(profile);
        post.setVisibility(Post.PostVisibility.PUBLIC);
        post.setOutfit(outfit);

        postDto = new PostDto();
        postDto.setTitle("Test Title");
        postDto.setFeatureImage("http://image.url");
        postDto.setContent("Test Content");
        postDto.setOutfitId(OUTFIT_ID);
        postDto.setVisibility("PUBLIC");

        mockImage = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());
    }

    @Test
    void createPost_Success() {
        when(profileService.getProfileEntityById(PROFILE_ID)).thenReturn(profile);
        when(outfitService.getOutfitEntityById(OUTFIT_ID)).thenReturn(outfit);
        when(imageService.uploadImage(any(MultipartFile.class), eq("post"), any(Long.class)))
                .thenReturn("http://uploaded-image.url");

        // Strict matching with argThat
        when(postRepository.save(argThat(post -> post.getTitle().equals("Test Title") &&
                post.getContent().equals("Test Content") &&
                post.getVisibility() == Post.PostVisibility.PUBLIC))).thenReturn(post);

        PostResponseDto mockPostResponseDto = createPostResponseDto();
        when(postMapper.toResponseDto(argThat(p -> p.getId().equals(POST_ID)))).thenReturn(mockPostResponseDto);

        PostResponseDto postResponseDto = postService.createPost(PROFILE_ID, postDto, mockImage);

        assertPostResponse(postResponseDto, POST_ID, "Test Title", "Test Content", "PUBLIC");
        verify(postRepository, times(2)).save(any(Post.class));
        verify(imageService).uploadImage(any(MultipartFile.class), eq("post"), any(Long.class));
    }

    @Test
    void createPost_ProfileNotFound() {
        when(profileService.getProfileEntityById(PROFILE_ID)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(PROFILE_ID, postDto, mockImage));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void createPost_OutfitNotFound() {
        when(profileService.getProfileEntityById(PROFILE_ID)).thenReturn(profile);
        when(outfitService.getOutfitEntityById(OUTFIT_ID)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(PROFILE_ID, postDto, mockImage));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void getPost_Success() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(postServiceHelper.isPostAccessibleToUser(post, PROFILE_ID)).thenReturn(true);
        when(postMapper.toResponseDto(post)).thenReturn(createPostResponseDto());

        PostResponseDto postResponseDto = postService.getPost(POST_ID, PROFILE_ID);

        assertPostResponse(postResponseDto, POST_ID, "Test Title", "Test Content", "PUBLIC");
        verify(postRepository).findById(POST_ID);
        verify(postServiceHelper).isPostAccessibleToUser(post, PROFILE_ID);
    }

    @Test
    void getPost_PostNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPost(POST_ID, PROFILE_ID));
        verify(postRepository).findById(POST_ID);
    }

    @Test
    void getPost_NotAccessible() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(postServiceHelper.isPostAccessibleToUser(post, PROFILE_ID)).thenReturn(false);

        assertThrows(PostAccessException.class, () -> postService.getPost(POST_ID, PROFILE_ID));
        verify(postRepository).findById(POST_ID);
    }

    @Test
    void updatePost_Success() {
        PostDto updateDto = new PostDto();
        updateDto.setContent("Updated Content");
        updateDto.setVisibility("PRIVATE");
        updateDto.setOutfitId(OUTFIT_ID);

        Post mockPost = new Post();
        mockPost.setId(POST_ID);
        mockPost.setTitle("Updated Title");
        mockPost.setContent("Updated Content");
        mockPost.setVisibility(Post.PostVisibility.PRIVATE);
        mockPost.setOutfit(outfit);
        mockPost.setProfile(profile);

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(mockPost.getId());
        postResponseDto.setTitle(mockPost.getTitle());
        postResponseDto.setContent(mockPost.getContent());
        postResponseDto.setVisibility(mockPost.getVisibility().toString());

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(mockPost));
        when(imageService.uploadImage(any(MultipartFile.class), eq("post"), any(Long.class)))
                .thenReturn("http://uploaded-image.url");
        when(postRepository.save(argThat(post -> post.getContent().equals("Updated Content") &&
                post.getVisibility() == Post.PostVisibility.PRIVATE))).thenReturn(mockPost);
        when(postMapper.toResponseDto(argThat(p -> p.getId().equals(mockPost.getId())))).thenReturn(postResponseDto);

        PostResponseDto updatedPost = postService.updatePost(POST_ID, PROFILE_ID, updateDto, mockImage);

        assertPostResponse(updatedPost, POST_ID, "Updated Title", "Updated Content", "PRIVATE");
        verify(postRepository).save(any(Post.class));
        verify(imageService).uploadImage(any(MultipartFile.class), eq("post"), any(Long.class));
    }

    @Test
    void updatePost_NotAuthorized() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        assertThrows(PostAccessException.class, () -> postService.updatePost(POST_ID, 2L, postDto, mockImage));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void deletePost_Success() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        postService.deletePost(POST_ID, PROFILE_ID);

        verify(postRepository).deleteById(POST_ID);
    }

    @Test
    void deletePost_PostNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePost(POST_ID, PROFILE_ID));
        verify(postRepository, never()).deleteById(POST_ID);
    }

    @Test
    void deletePost_NotAuthorized() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        assertThrows(PostAccessException.class, () -> postService.deletePost(POST_ID, 2L));
        verify(postRepository, never()).deleteById(POST_ID);
    }

    @Test
    void toggleLikePost_Success() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(profileService.getProfileEntityById(PROFILE_ID)).thenReturn(profile);
        when(postServiceHelper.hasProfileLikedPost(POST_ID, PROFILE_ID)).thenReturn(false);

        boolean result = postService.toggleLikePost(POST_ID, PROFILE_ID);

        assertTrue(result);
        verify(likeRepository).save(any(Like.class));
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void toggleLikePost_LikeNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(profileService.getProfileEntityById(PROFILE_ID)).thenReturn(profile);
        when(postServiceHelper.hasProfileLikedPost(POST_ID, PROFILE_ID)).thenReturn(true);
        when(likeRepository.findByPostAndProfile(post, profile)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.toggleLikePost(POST_ID, PROFILE_ID));
    }

    @Test
    void toggleLikePost_PostNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.toggleLikePost(POST_ID, PROFILE_ID));
        verify(likeRepository, never()).save(any(Like.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    private PostResponseDto createPostResponseDto() {
        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(POST_ID);
        postResponseDto.setTitle("Test Title");
        postResponseDto.setContent("Test Content");
        postResponseDto.setVisibility("PUBLIC");
        return postResponseDto;
    }

    private void assertPostResponse(PostResponseDto postResponseDto, Long id, String title, String content,
            String visibility) {
        assertNotNull(postResponseDto);
        assertEquals(id, postResponseDto.getId());
        assertEquals(title, postResponseDto.getTitle());
        assertEquals(content, postResponseDto.getContent());
        assertEquals(visibility, postResponseDto.getVisibility());
    }
}
