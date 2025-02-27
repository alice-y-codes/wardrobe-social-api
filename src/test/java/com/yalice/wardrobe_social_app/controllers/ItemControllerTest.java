package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
    private UserService userService;

    private ItemController itemController;

    @BeforeEach
    public void setup() {
        itemController = new ItemController(itemService, userService);

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void shouldCreateItem() throws Exception {
        // Arrange
        Item item = new Item();
        item.setName("Test name");
        item.setUserId(1L);
        item.setCategory("Test category");
        item.setImageUrl("Test image url");
        when(itemService.createItem(any(Long.class), any(Item.class))).thenReturn(Optional.of(item));

        // Act & Assert
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test name"))
                .andExpect(jsonPath("$.category").value("Test category"))
                .andExpect(jsonPath("$.imageUrl").value("Test image url"));

        verify(itemService).createItem(any(Long.class), any(Item.class));
    }

    @Test
    public void shouldGetAllItems() throws Exception {
        // Arrange
        Item item = new Item();
        item.setName("Test name");
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
        Item item = new Item();
        item.setId(1L);
        item.setName("Test name");
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
        Item item = new Item();
        item.setName("Test name");
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
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Old name");

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Updated name");

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
}
