package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.utils.AuthenticationTestUtils;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        @BeforeEach
        void setUp() {
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

                MockitoAnnotations.openMocks(this);
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
                        .build();

                AuthenticationTestUtils.setupAuthentication("testuser");
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void getUserFeed_returnsFeed() throws Exception {
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
                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
                Pageable pageable = PageRequest.of(0, 20);  // Create a Pageable object

                // Mock the service call
                when(feedService.getUserPosts(eq(1L), eq(1L), eq(pageable)))
                        .thenReturn(new PageImpl<>(Arrays.asList(post)));

                // Perform the request
                mockMvc.perform(get("/api/feed/users/1/posts")
                                .param("page", "0")
                                .param("size", "20"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.content[0].id").value(1))
                        .andExpect(jsonPath("$.content[0].content").value("Test post content"));
        }

}
