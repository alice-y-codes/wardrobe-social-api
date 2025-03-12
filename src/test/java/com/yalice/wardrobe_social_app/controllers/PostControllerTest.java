package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest {

        @Mock
        private PostService postService;

        @Mock
        private AuthUtils authUtils;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;
        private User testUser;

        @InjectMocks
        private PostController postController;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders
                                .standaloneSetup(postController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
                objectMapper = new ObjectMapper();
                testUser = User.builder()
                                .id(1L)
                                .profile(Profile.builder().id(1L).build())
                                .build();
        }

        @Test
        void createPost() throws Exception {
                PostDto postDto = PostDto.builder()
                                .title("Test Post")
                                .content("Test content")
                                .outfitId(1L)
                                .visibility("PUBLIC")
                                .build();

                PostResponseDto responseDto = PostResponseDto.builder()
                                .id(1L)
                                .title("Test Post")
                                .content("Test content")
                                .outfit(Outfit.builder().id(1L).build())
                                .visibility("PUBLIC")
                                .build();

                MockMultipartFile image = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                "image/jpeg",
                                "test image content".getBytes());

                MockMultipartFile jsonFile = new MockMultipartFile(
                                "post",
                                "",
                                "application/json",
                                objectMapper.writeValueAsString(postDto).getBytes());

                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.createPost(eq(1L), any(PostDto.class), any(MultipartFile.class)))
                                .thenReturn(responseDto);

                mockMvc.perform(multipart("/api/feed")
                                .file(jsonFile)
                                .file(image))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.id").value(1L))
                                .andExpect(jsonPath("$.data.title").value("Test Post"));
        }

        @Test
        void createPost_NoImage() throws Exception {
                PostDto postDto = PostDto.builder()
                                .title("Test Post")
                                .content("Test content")
                                .outfitId(1L)
                                .visibility("PUBLIC")
                                .build();

                PostResponseDto responseDto = PostResponseDto.builder()
                                .id(1L)
                                .title("Test Post")
                                .content("Test content")
                                .outfit(Outfit.builder().id(1L).build())
                                .visibility("PUBLIC")
                                .build();

                MockMultipartFile jsonFile = new MockMultipartFile(
                                "post",
                                "",
                                "application/json",
                                objectMapper.writeValueAsString(postDto).getBytes());

                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.createPost(eq(1L), any(PostDto.class), eq(null))).thenReturn(responseDto);

                mockMvc.perform(multipart("/api/feed")
                                .file(jsonFile))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.id").value(1L))
                                .andExpect(jsonPath("$.data.title").value("Test Post"));
        }

        @Test
        void createPost_Unauthorized() throws Exception {
                PostDto postDto = new PostDto();
                when(authUtils.getCurrentUserOrElseThrow())
                                .thenThrow(new SecurityException("Unauthorized"));

                mockMvc.perform(post("/api/feed/post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postDto)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void updatePost() throws Exception {
                Long postId = 1L;
                PostDto postDto = PostDto.builder()
                                .title("Updated Post")
                                .content("Updated content")
                                .outfitId(1L)
                                .visibility("PUBLIC")
                                .build();

                PostResponseDto responseDto = PostResponseDto.builder()
                                .id(1L)
                                .title("Updated Post")
                                .content("Updated content")
                                .outfit(Outfit.builder().id(1L).build())
                                .visibility("PUBLIC")
                                .build();

                MockMultipartFile image = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                "image/jpeg",
                                "test image content".getBytes());

                MockMultipartFile jsonFile = new MockMultipartFile(
                                "post",
                                "",
                                "application/json",
                                objectMapper.writeValueAsString(postDto).getBytes());

                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.updatePost(eq(1L), eq(1L), any(PostDto.class), any(MultipartFile.class)))
                                .thenReturn(responseDto);

                MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/feed/{postId}",
                                postId);
                builder.with(request -> {
                        request.setMethod("PATCH");
                        return request;
                });

                mockMvc.perform(builder
                                .file(jsonFile)
                                .file(image))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.id").value(1L))
                                .andExpect(jsonPath("$.data.title").value("Updated Post"));
        }

        @Test
        void updatePost_NoImage() throws Exception {
                Long postId = 1L;
                PostDto postDto = PostDto.builder()
                                .title("Updated Post")
                                .content("Updated content")
                                .outfitId(1L)
                                .visibility("PUBLIC")
                                .build();

                PostResponseDto responseDto = PostResponseDto.builder()
                                .id(1L)
                                .title("Updated Post")
                                .content("Updated content")
                                .outfit(Outfit.builder().id(1L).build())
                                .visibility("PUBLIC")
                                .build();

                MockMultipartFile jsonFile = new MockMultipartFile(
                                "post",
                                "",
                                "application/json",
                                objectMapper.writeValueAsString(postDto).getBytes());

                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.updatePost(eq(1L), eq(1L), any(PostDto.class), isNull())).thenReturn(responseDto);

                MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/feed/{postId}",
                                postId);
                builder.with(request -> {
                        request.setMethod("PATCH");
                        return request;
                });

                mockMvc.perform(builder
                                .file(jsonFile))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.id").value(1L))
                                .andExpect(jsonPath("$.data.title").value("Updated Post"));
        }

        @Test
        void updatePost_NotFound() throws Exception {
                Long postId = 1L;
                PostDto postDto = PostDto.builder()
                                .title("Updated Post")
                                .content("Updated content")
                                .outfitId(1L)
                                .visibility("PUBLIC")
                                .build();

                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.updatePost(eq(1L), eq(1L), any(PostDto.class), isNull()))
                                .thenThrow(new ResourceNotFoundException("Post not found"));

                MockMultipartFile jsonFile = new MockMultipartFile(
                                "post",
                                "",
                                "application/json",
                                objectMapper.writeValueAsString(postDto).getBytes());

                MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/feed/{postId}",
                                postId);
                builder.with(request -> {
                        request.setMethod("PATCH");
                        return request;
                });

                mockMvc.perform(builder
                                .file(jsonFile))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Post not found"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void deletePost() throws Exception {
                Long postId = 1L;
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                doNothing().when(postService).deletePost(eq(1L), eq(1L));

                mockMvc.perform(delete("/api/feed/{postId}", postId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        void deletePost_NotFound() throws Exception {
                Long postId = 1L;
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                doThrow(new ResourceNotFoundException("Post not found"))
                                .when(postService).deletePost(eq(1L), eq(1L));

                mockMvc.perform(delete("/api/feed/{postId}", postId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Post not found"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void getPost() throws Exception {
                Long postId = 1L;
                PostResponseDto responseDto = PostResponseDto.builder()
                                .id(1L)
                                .title("Test Post")
                                .content("Test content")
                                .outfit(Outfit.builder().id(1L).build())
                                .visibility("PUBLIC")
                                .build();

                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.getPost(eq(1L), eq(1L))).thenReturn(responseDto);

                mockMvc.perform(get("/api/feed/{postId}", postId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data.id").value(1L));
        }

        @Test
        void getPost_NotFound() throws Exception {
                Long postId = 1L;
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.getPost(any(), any()))
                                .thenThrow(new ResourceNotFoundException("Post not found"));

                mockMvc.perform(get("/api/feed/{postId}", postId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Post not found"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void toggleLikePost() throws Exception {
                Long postId = 1L;
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.toggleLikePost(eq(1L), eq(1L))).thenReturn(true);

                mockMvc.perform(post("/api/feed/{postId}/like", postId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").value("Post liked successfully"));
        }

        @Test
        void toggleLikePost_Unlike() throws Exception {
                Long postId = 1L;
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.toggleLikePost(eq(1L), eq(1L))).thenReturn(false);

                mockMvc.perform(post("/api/feed/{postId}/like", postId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").value("Post unliked successfully"));
        }

        @Test
        void toggleLikePost_NotFound() throws Exception {
                Long postId = 1L;
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.toggleLikePost(eq(1L), eq(1L)))
                                .thenThrow(new ResourceNotFoundException("Post not found"));

                mockMvc.perform(post("/api/feed/{postId}/like", postId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Post not found"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void toggleLikePost_Unauthorized() throws Exception {
                Long postId = 1L;
                when(authUtils.getCurrentUserOrElseThrow())
                                .thenThrow(new SecurityException("Unauthorized"));

                mockMvc.perform(post("/api/feed/{postId}/like", postId))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }
}
