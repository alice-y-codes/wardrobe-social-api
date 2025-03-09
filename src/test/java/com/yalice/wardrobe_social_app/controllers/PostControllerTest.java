//package com.yalice.wardrobe_social_app.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yalice.wardrobe_social_app.dtos.post.PostDto;
//import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
//import com.yalice.wardrobe_social_app.entities.Outfit;
//import com.yalice.wardrobe_social_app.entities.User;
//import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
//import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
//import com.yalice.wardrobe_social_app.interfaces.PostService;
//import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.Matchers.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class PostControllerTest {
//
//        private MockMvc mockMvc;
//
//        @Mock
//        private PostService postService;
//
//        @Mock
//        private AuthUtils authUtils;
//
//        @InjectMocks
//        private PostController postController;
//
//        private final ObjectMapper objectMapper = new ObjectMapper();
//        private User testUser;
//        private PostDto testPostDto;
//        private PostResponseDto testPostResponseDto;
//        private Outfit testOutfit;
//        private List<PostResponseDto> testPostList;
//
//        @BeforeEach
//        void setUp() {
//                MockitoAnnotations.openMocks(this);
//                mockMvc = MockMvcBuilders.standaloneSetup(postController)
//                        .setControllerAdvice(new GlobalExceptionHandler())
//                        .build();
//
//                initializeTestData();
//        }
//
//        private void initializeTestData() {
//                testUser = User.builder()
//                        .username("testuser")
//                        .email("test@example.com")
//                        .build();
//                testUser.setId(1L);
//
//                testOutfit = Outfit.builder()
//                        .name("Test Outfit")
//                        .description("A test outfit")
//                        .season("SUMMER")
//                        .favorite(false)
//                        .isPublic(true)
//                        .build();
//                testOutfit.setId(1L);
//                testOutfit.setCreatedAt(LocalDateTime.now());
//                testOutfit.setUpdatedAt(LocalDateTime.now());
//
//                testPostDto = PostDto.builder()
//                        .title("Test Post")
//                        .content("Test post content")
//                        .featureImage("https://example.com/post.jpg")
//                        .outfitId(1L)
//                        .visibility("PUBLIC")
//                        .build();
//
//                testPostResponseDto = PostResponseDto.builder()
//                        .id(1L)
//                        .title("Test Post")
//                        .content("Test post content")
//                        .featureImage("https://example.com/post.jpg")
//                        .outfit(testOutfit)
//                        .visibility("PUBLIC")
//                        .username("testuser")
//                        .build();
//
//                testPostList = Arrays.asList(
//                        testPostResponseDto,
//                        PostResponseDto.builder()
//                                .id(2L)
//                                .title("Another Post")
//                                .content("Another post content")
//                                .featureImage("https://example.com/another-post.jpg")
//                                .outfit(testOutfit)
//                                .visibility("PUBLIC")
//                                .username("otheruser")
//                                .build());
//        }
//
//        private void mockLikePostResponse(boolean isLiked) {
//                when(postService.toggleLikePost(anyLong(), anyLong())).thenReturn(isLiked);
//        }
//
//        private void mockUnlikePostResponse(boolean isUnliked) {
//                when(postService.toggleLikePost(anyLong(), anyLong())).thenReturn(isUnliked);
//        }
//
//        @Test
//        void createPost_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(postService.createPost(anyLong(), any(PostDto.class))).thenReturn(testPostResponseDto);
//
//                mockMvc.perform(post("/api/feed/post")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(testPostDto)))
//                        .andExpect(status().isCreated())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Post created successfully")))
//                        .andExpect(jsonPath("$.data.id", is(1)))
//                        .andExpect(jsonPath("$.data.title", is("Test Post")))
//                        .andExpect(jsonPath("$.data.content", is("Test post content")))
//                        .andExpect(jsonPath("$.data.visibility", is("PUBLIC")))
//                        .andExpect(jsonPath("$.data.username", is("testuser")));
//
//                verify(postService).createPost(anyLong(), any(PostDto.class));
//        }
//
//        @Test
//        void createPost_Failure_Validation() throws Exception {
//                testPostDto.setTitle("");  // Invalid title for failure case
//                mockMvc.perform(post("/api/feed/post")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(testPostDto)))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", containsString("Title must not be empty")));
//        }
//
//        @Test
//        void createPost_Failure_MissingContent() throws Exception {
//                testPostDto.setContent("");  // Empty content for failure case
//                mockMvc.perform(post("/api/feed/post")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(testPostDto)))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", containsString("Content must not be empty")));
//        }
//
//        @Test
//        void createPost_Failure_InvalidOutfitId() throws Exception {
//                testPostDto.setOutfitId(999L);  // Invalid outfitId for failure case
//                mockMvc.perform(post("/api/feed/post")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(testPostDto)))
//                        .andExpect(status().isBadRequest())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", containsString("Invalid outfit ID")));
//        }
//
//        @Test
//        void getPost_NotFound() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(postService.getPost(anyLong(), anyLong()))
//                        .thenThrow(new PostNotFoundException("Post not found with ID: 999"));
//
//                mockMvc.perform(get("/api/feed/999"))
//                        .andExpect(status().isNotFound())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Post not found with ID: 999")));
//
//                verify(postService).getPost(anyLong(), anyLong());
//        }
//
//        @Test
//        void createPost_WithPrivateVisibility() throws Exception {
//                testPostDto.setVisibility("PRIVATE");
//
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(postService.createPost(anyLong(), any(PostDto.class))).thenReturn(testPostResponseDto);
//
//                mockMvc.perform(post("/api/feed/post")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(testPostDto)))
//                        .andExpect(status().isCreated())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Post created successfully")))
//                        .andExpect(jsonPath("$.data.visibility", is("PRIVATE")));
//
//                verify(postService).createPost(anyLong(), any(PostDto.class));
//        }
//
//        @Test
//        void updatePost_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(postService.updatePost(anyLong(), anyLong(), any(PostDto.class))).thenReturn(testPostResponseDto);
//
//                mockMvc.perform(put("/api/feed/1")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(testPostDto)))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Post updated successfully")))
//                        .andExpect(jsonPath("$.data.id", is(1)))
//                        .andExpect(jsonPath("$.data.title", is("Test Post")))
//                        .andExpect(jsonPath("$.data.content", is("Test post content")))
//                        .andExpect(jsonPath("$.data.visibility", is("PUBLIC")));
//
//                verify(postService).updatePost(anyLong(), anyLong(), any(PostDto.class));
//        }
//
//        @Test
//        void deletePost_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                doNothing().when(postService).deletePost(anyLong(), anyLong());
//
//                mockMvc.perform(delete("/api/feed/1"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Post deleted successfully")))
//                        .andExpect(jsonPath("$.data", is("Post deleted")));
//
//                verify(postService).deletePost(anyLong(), anyLong());
//        }
//
//        @Test
//        void deletePost_Failure_PostNotFound() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                doThrow(new PostNotFoundException("Post not found with ID: 999")).when(postService).deletePost(anyLong(), anyLong());
//
//                mockMvc.perform(delete("/api/feed/999"))
//                        .andExpect(status().isNotFound())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Post not found with ID: 999")));
//
//                verify(postService).deletePost(anyLong(), anyLong());
//        }
//
//        @Test
//        void likePost_Success() throws Exception {
//                mockLikePostResponse(true);  // Mock post as liked
//                mockMvc.perform(post("/api/feed/1/like"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Post liked successfully")))
//                        .andExpect(jsonPath("$.data", is("Post liked")));
//
//                verify(postService).toggleLikePost(anyLong(), anyLong());
//        }
//
//        @Test
//        void likePost_AlreadyLiked() throws Exception {
//                mockLikePostResponse(false);  // Mock post already liked
//                mockMvc.perform(post("/api/feed/1/like"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Post already liked")))
//                        .andExpect(jsonPath("$.data", is("Post already liked")));
//
//                verify(postService).toggleLikePost(anyLong(), anyLong());
//        }
//
//        @Test
//        void unlikePost_Success() throws Exception {
//                mockUnlikePostResponse(true);  // Mock post unliked
//                mockMvc.perform(delete("/api/feed/1/like"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Post unliked successfully")))
//                        .andExpect(jsonPath("$.data", is("Post unliked")));
//
//                verify(postService).toggleLikePost(anyLong(), anyLong());
//        }
//
//        @Test
//        void unlikePost_NotLiked() throws Exception {
//                mockUnlikePostResponse(false);  // Mock post not liked
//                mockMvc.perform(delete("/api/feed/1/like"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Post was not liked")))
//                        .andExpect(jsonPath("$.data", is("Post was not liked")));
//
//                verify(postService).toggleLikePost(anyLong(), anyLong());
//        }
//
//        @Test
//        void testGlobalExceptionHandler() throws Exception {
//                when(postService.createPost(anyLong(), any(PostDto.class)))
//                        .thenThrow(new RuntimeException("Unexpected error"));
//
//                mockMvc.perform(post("/api/feed/post")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(testPostDto)))
//                        .andExpect(status().isInternalServerError())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Unexpected error")));
//        }
//}
