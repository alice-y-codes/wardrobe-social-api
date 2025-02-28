package com.yalice.wardrobe_social_app.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yalice.wardrobe_social_app.dtos.CommentDto;
import com.yalice.wardrobe_social_app.dtos.PostDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.PostVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class FeedControllerTest {

        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Mock
        private FeedService feedService;

        @Mock
        private UserService userService;

        @InjectMocks
        private FeedController feedController;

        private User user;
        private Post post;
        private Comment comment;
        private PostDto postDto;
        private CommentDto commentDto;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);

                // Configure PageableHandlerMethodArgumentResolver with default values
                PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
                pageableResolver.setFallbackPageable(PageRequest.of(0, 20));
                pageableResolver.setOneIndexedParameters(false);

                // Configure ObjectMapper to handle Java 8 date/time types and PageImpl
                objectMapper.findAndRegisterModules();
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                // Register custom PageImpl serializer to avoid UnsupportedOperationException
                SimpleModule pageModule = new SimpleModule();
                pageModule.addSerializer(PageImpl.class, new JsonSerializer<PageImpl>() {
                        @Override
                        public void serialize(PageImpl page, JsonGenerator gen, SerializerProvider serializers)
                                        throws IOException {
                                gen.writeStartObject();
                                gen.writeObjectField("content", page.getContent());
                                gen.writeNumberField("number", page.getNumber());
                                gen.writeNumberField("size", page.getSize());
                                gen.writeNumberField("totalElements", page.getTotalElements());
                                gen.writeNumberField("totalPages", page.getTotalPages());
                                gen.writeBooleanField("first", page.isFirst());
                                gen.writeBooleanField("last", page.isLast());
                                gen.writeBooleanField("empty", page.isEmpty());
                                gen.writeEndObject();
                        }
                });
                objectMapper.registerModule(pageModule);

                // Create a message converter with our custom ObjectMapper
                MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
                messageConverter.setObjectMapper(objectMapper);

                mockMvc = MockMvcBuilders.standaloneSetup(feedController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .setCustomArgumentResolvers(pageableResolver)
                                .setMessageConverters(messageConverter)
                                .build();

                user = User.builder()
                                .id(1L)
                                .username("testuser")
                                .email("test@example.com")
                                .build();

                post = Post.builder()
                                .id(1L)
                                .user(user)
                                .content("Test post content")
                                .visibility(PostVisibility.PUBLIC)
                                .build();

                comment = Comment.builder()
                                .id(1L)
                                .post(post)
                                .user(user)
                                .content("Test comment")
                                .build();

                // Ensure post has a non-null createdAt field if it exists
                try {
                        java.lang.reflect.Field createdAtField = Post.class.getDeclaredField("createdAt");
                        createdAtField.setAccessible(true);
                        if (createdAtField.get(post) == null) {
                                createdAtField.set(post, java.time.LocalDateTime.now());
                        }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                        // Field doesn't exist or can't be accessed, which is fine
                }

                // Ensure comment has a non-null createdAt field if it exists
                try {
                        java.lang.reflect.Field createdAtField = Comment.class.getDeclaredField("createdAt");
                        createdAtField.setAccessible(true);
                        if (createdAtField.get(comment) == null) {
                                createdAtField.set(comment, java.time.LocalDateTime.now());
                        }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                        // Field doesn't exist or can't be accessed, which is fine
                }

                postDto = new PostDto();
                postDto.setContent("Test post content");
                postDto.setVisibility(PostVisibility.PUBLIC);

                commentDto = new CommentDto();
                commentDto.setContent("Test comment");
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void getUserFeed_returnsFeed() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
                when(feedService.getUserFeed(eq(1L), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(Arrays.asList(post)));

                mockMvc.perform(get("/api/feed")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].content").value("Test post content"));
        }

        @Test
        void getUserPosts_returnsUserPosts() throws Exception {
                setupAuthentication("testuser");


                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
                when(feedService.getUserPosts(eq(1L), eq(1L), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(Arrays.asList(post)));

                mockMvc.perform(get("/api/feed/users/1")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].content").value("Test post content"));
        }

        @Test
        void createPost_createsAndReturnsPost() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
                when(feedService.createPost(eq(1L), anyString(), any(), any(PostVisibility.class)))
                                .thenReturn(post);

                mockMvc.perform(post("/api/feed/post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.content").value("Test post content"));
        }

        @Test
        void deletePost_deletesPost() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));

                mockMvc.perform(delete("/api/feed/1"))
                                .andExpect(status().isOk());
        }

        @Test
        void likePost_likesPost() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
                when(feedService.likePost(1L, 1L)).thenReturn(true);

                mockMvc.perform(post("/api/feed/1/like"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").value("Post liked"));
        }

        @Test
        void unlikePost_unlikesPost() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
                when(feedService.unlikePost(1L, 1L)).thenReturn(true);

                mockMvc.perform(delete("/api/feed/1/like"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").value("Post unliked"));
        }

        @Test
        void addComment_addsAndReturnsComment() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
                when(feedService.addComment(eq(1L), eq(1L), anyString())).thenReturn(comment);

                mockMvc.perform(post("/api/feed/1/comment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.content").value("Test comment"));
        }

        @Test
        void deleteComment_deletesComment() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));

                mockMvc.perform(delete("/api/feed/comments/1"))
                                .andExpect(status().isOk());
        }

        @Test
        void getPostComments_returnsComments() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(feedService.getPostComments(eq(1L), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(Arrays.asList(comment)));

                mockMvc.perform(get("/api/feed/1/comments")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].content").value("Test comment"));
        }

        @Test
        void testExceptionHandling_returnsProperErrorResponse() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));

                // Mock a specific exception being thrown
                doThrow(new IllegalArgumentException("Invalid post ID")).when(feedService)
                                .getUserPosts(eq(999L), eq(1L), any(Pageable.class));

                // Test that the exception is properly handled
                mockMvc.perform(get("/api/feed/users/999")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Invalid argument provided"))
                                .andExpect(jsonPath("$.details").value("Invalid post ID"));
        }

        private void setupAuthentication(String username) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                username, null, new ArrayList<>());
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
        }
}