package com.yalice.wardrobe_social_app.services.itemServiceTests;

import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.services.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class DeleteItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    private static final Long ITEM_ID = 1L;

    @BeforeEach
    public void setUp() {
        // Initialize the mocks before each test
        MockitoAnnotations.openMocks(this);
    }

    // ---- Tests for deleteItem ----

    @Test
    public void shouldDeleteItem_WhenItemExists() {
        // Arrange
        when(itemRepository.existsById(ITEM_ID)).thenReturn(true);

        // Act
        itemService.deleteItem(ITEM_ID);

        // Assert
        verify(itemRepository).existsById(ITEM_ID);
        verify(itemRepository).deleteById(ITEM_ID); // Ensure the delete operation is called
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenItemDoesNotExist() {
        // Arrange
        when(itemRepository.existsById(ITEM_ID)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> itemService.deleteItem(ITEM_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with ID");

        // Verify that neither delete nor exists operation was called
        verify(itemRepository).existsById(ITEM_ID);
        verify(itemRepository, never()).deleteById(ITEM_ID);
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenItemIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> itemService.deleteItem(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item ID cannot be null");

        // Verify that neither exists nor delete operation was called
        verify(itemRepository, never()).existsById(anyLong());
        verify(itemRepository, never()).deleteById(anyLong());
    }
}
