package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    private UserSearchService userSearchService;

    @InjectMocks
    private ItemController itemController;

    private User user;
    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    public void setup() {
        // Create a new MockMvc instance for each test
        itemController = new ItemController(itemService, userSearchService);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        // Set up test data
        user = User.builder().id(1L).username("testuser").build();
        itemDto = ItemDto.builder()
                .name("Test name")
                .category("Test category")
                .imageUrl("Test image url")
                .build();
        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Test name")
                .category("Test category")
                .imageUrl("Test image url")
                .build();
    }

    @AfterEach
    public void tearDown() {
        // Clear the security context after each test
        SecurityContextHolder.clearContext();
    }

    /**
     * Sets up authentication for tests by creating a mock authentication context.
     *
     * @param username The username of the authenticated user.
     */
    public static void setupAuthentication(String username) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, null, Collections.emptyList());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Test to create an item for a user.
     */
    @Test
    public void shouldCreateItem() throws Exception {
        // Arrange
        setupAuthentication("testuser");
        when(userSearchService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(itemService.createItem(eq(1L), any(ItemDto.class))).thenReturn(itemResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Test name"))
                .andExpect(jsonPath("$.data.category").value("Test category"))
                .andExpect(jsonPath("$.data.imageUrl").value("Test image url"));

        verify(userSearchService).findUserByUsername("testuser");
        verify(itemService).createItem(eq(1L), any(ItemDto.class));
    }

    /**
     * Test to retrieve the items belonging to the authenticated user.
     */
    @Test
    public void shouldGetMyItems() throws Exception {
        // Arrange
        setupAuthentication("testuser");
        when(userSearchService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(itemService.getAllItems(1L)).thenReturn(Collections.singletonList(itemResponseDto));

        // Act & Assert
        mockMvc.perform(get("/api/items/my-items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Test name"));

        verify(userSearchService).findUserByUsername("testuser");
        verify(itemService).getAllItems(1L);
    }

    /**
     * Test to retrieve all items for a specific user by their ID.
     */
    @Test
    public void shouldGetAllItems() throws Exception {
        // Arrange
        when(itemService.getAllItems(1L)).thenReturn(Collections.singletonList(itemResponseDto));

        // Act & Assert
        mockMvc.perform(get("/api/items/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Test name"));

        verify(itemService).getAllItems(1L);
    }

    /**
     * Test to retrieve a specific item by its ID.
     */
    @Test
    public void shouldGetItemById() throws Exception {
        // Arrange
        when(itemService.getItem(1L)).thenReturn(itemResponseDto);

        // Act & Assert
        mockMvc.perform(get("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test name"));

        verify(itemService).getItem(1L);
    }

    /**
     * Test to handle when an item does not exist for the given ID.
     */
    @Test
    public void shouldReturnNotFoundWhenItemByIdDoesNotExist() throws Exception {
        // Arrange
        when(itemService.getItem(1L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService).getItem(1L);
    }

    /**
     * Test to retrieve an item by its name.
     */
    @Test
    public void shouldGetItemByName() throws Exception {
        // Arrange
        when(itemService.getItemByName("Test name")).thenReturn(itemResponseDto);

        // Act & Assert
        mockMvc.perform(get("/api/items/names/Test name")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test name"));

        verify(itemService).getItemByName("Test name");
    }

    /**
     * Test to update an existing item.
     */
    @Test
    public void shouldUpdateItem() throws Exception {
        // Arrange
        ItemDto updatedItemDto = ItemDto.builder()
                .name("Updated name")
                .category("Updated category")
                .imageUrl("Updated image url")
                .build();
        ItemResponseDto updatedItem = ItemResponseDto.builder()
                .id(1L)
                .name("Updated name")
                .category("Updated category")
                .imageUrl("Updated image url")
                .build();
        when(itemService.updateItem(eq(1L), any(ItemDto.class))).thenReturn(updatedItem);

        // Act & Assert
        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated name"))
                .andExpect(jsonPath("$.data.category").value("Updated category"));

        verify(itemService).updateItem(eq(1L), any(ItemDto.class));
    }

    /**
     * Test to delete an item by its ID.
     */
    @Test
    public void shouldDeleteItem() throws Exception {
        // Arrange
        long itemId = 1L;
        when(itemService.deleteItem(itemId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(itemService).deleteItem(itemId);
    }

    /**
     * Test to return a BadRequest status when the user is not authenticated.
     */
    @Test
    public void shouldReturnBadRequestWhenUserNotAuthenticated() throws Exception {
        // Arrange - no authentication setup
        when(userSearchService.findUserByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test to return a BadRequest status when the user is not found.
     */
    @Test
    public void shouldReturnBadRequestWhenUserNotFound() throws Exception {
        // Arrange
        setupAuthentication("testuser");
        when(userSearchService.findUserByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(userSearchService).findUserByUsername("testuser");
    }
}
