package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Mock
        private ItemService itemService;

        @Mock
        private UserService userService;

        private ItemController itemController;

        @BeforeEach
        public void setup() {
                itemController = new ItemController(itemService, userService);

                mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        }

        @AfterEach
        public void tearDown() {
                // Clear the security context after each test
                SecurityContextHolder.clearContext();
        }

        @Test
        public void shouldCreateItem() throws Exception {
                // Arrange
                // 1. Create a real authentication token instead of a mock
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testuser",
                                null,
                                new ArrayList<>());

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);

                // 2. Mock the user service to return a user
                User user = User.builder()
                                .id(1L)
                                .username("testuser")
                                .build();
                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));

                // 3. Mock the item service
                Item item = Item.builder()
                                .userId(1L)
                                .name("Test name")
                                .category("Test category")
                                .imageUrl("Test image url")
                                .build();
                when(itemService.createItem(eq(1L), any(Item.class))).thenReturn(Optional.of(item));

                // Act & Assert
                mockMvc.perform(post("/api/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Test name"))
                                .andExpect(jsonPath("$.category").value("Test category"))
                                .andExpect(jsonPath("$.imageUrl").value("Test image url"));

                // Verify that the service methods were called with the correct parameters
                verify(userService).findUserByUsername("testuser");
                verify(itemService).createItem(eq(1L), any(Item.class));
        }

        // Add a test for the new my-items endpoint
        @Test
        public void shouldGetMyItems() throws Exception {
                // Arrange
                // 1. Create a real authentication token instead of a mock
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testuser",
                                null,
                                new ArrayList<>());

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);

                // 2. Mock the user service to return a user
                User user = User.builder()
                                .id(1L)
                                .username("testuser")
                                .build();
                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));

                // 3. Mock the item service
                Item item = Item.builder()
                                .userId(1L)
                                .name("Test name")
                                .build();
                when(itemService.getAllItems(1L)).thenReturn(Collections.singletonList(item));

                // Act & Assert
                mockMvc.perform(get("/api/items/my-items")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].name").value("Test name"));

                verify(userService).findUserByUsername("testuser");
                verify(itemService).getAllItems(1L);
        }

        @Test
        public void shouldGetAllItems() throws Exception {
                // Arrange
                Item item = Item.builder()
                                .userId(1L)
                                .name("Test name")
                                .build();
                when(itemService.getAllItems(1L)).thenReturn(Collections.singletonList(item));

                // Act & Assert
                mockMvc.perform(get("/api/items/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].name").value("Test name"));

                verify(itemService, Mockito.times(1)).getAllItems(1L);
        }

        @Test
        public void shouldGetItemById() throws Exception {
                // Arrange
                Item item = Item.builder()
                                .id(1L)
                                .userId(1L)
                                .name("Test name")
                                .build();
                when(itemService.getItem(1L)).thenReturn(Optional.of(item));

                // Act & Assert
                mockMvc.perform(get("/api/items/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Test name"));

                verify(itemService, Mockito.times(1)).getItem(1L);
        }

        @Test
        public void shouldReturnNotFoundWhenItemByIdDoesNotExist() throws Exception {
                // Arrange
                when(itemService.getItem(1L)).thenReturn(Optional.empty());

                // Act & Assert
                mockMvc.perform(get("/api/items/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(itemService, Mockito.times(1)).getItem(1L);
        }

        @Test
        public void shouldGetItemByName() throws Exception {
                // Arrange
                Item item = Item.builder()
                                .userId(1L)
                                .name("Test name")
                                .build();
                when(itemService.getItemByName("Test name")).thenReturn(Optional.of(item));

                // Act & Assert
                mockMvc.perform(get("/api/items/names/Test name")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Test name"));

                verify(itemService, Mockito.times(1)).getItemByName("Test name");
        }

        @Test
        public void shouldUpdateItem() throws Exception {
                // Arrange
                Item existingItem = Item.builder()
                                .id(1L)
                                .userId(1L)
                                .name("Old name")
                                .build();

                Item updatedItem = Item.builder()
                                .id(1L)
                                .userId(1L)
                                .name("Updated name")
                                .build();

                when(itemService.updateItem(eq(1L), any(Item.class))).thenReturn(updatedItem);

                // Act & Assert
                mockMvc.perform(put("/api/items/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedItem)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Updated name"));

                verify(itemService, Mockito.times(1)).updateItem(eq(1L), any(Item.class));
        }

        @Test
        public void shouldDeleteItem() throws Exception {
                // Arrange
                long itemId = 1L;

                // Act & Assert
                mockMvc.perform(delete("/api/items/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());

                verify(itemService, Mockito.times(1)).deleteItem(itemId);
        }

        @Test
        public void shouldReturnBadRequestWhenUserNotAuthenticated() throws Exception {
                // Arrange - no authentication setup
                // SecurityContextHolder is empty by default

                Item item = Item.builder()
                                .name("Test name")
                                .category("Test category")
                                .build();

                // Act & Assert
                mockMvc.perform(post("/api/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenUserNotFound() throws Exception {
                // Arrange
                // 1. Create a real authentication token instead of a mock
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testuser",
                                null,
                                new ArrayList<>());

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);

                // 2. Mock the user service to return empty (user not found)
                when(userService.findUserByUsername("testuser")).thenReturn(Optional.empty());

                Item item = Item.builder()
                                .name("Test name")
                                .category("Test category")
                                .build();

                // Act & Assert
                mockMvc.perform(post("/api/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item)))
                                .andExpect(status().isBadRequest());

                verify(userService).findUserByUsername("testuser");
        }
}
