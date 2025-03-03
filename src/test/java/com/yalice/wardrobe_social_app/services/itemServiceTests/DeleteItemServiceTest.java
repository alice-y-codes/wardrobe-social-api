package com.yalice.wardrobe_social_app.services.itemServiceTests;

import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.services.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void deleteItem_whenItemExists_shouldDeleteItem() {
        // Arrange
        Long itemId = 1L;

        when(itemRepository.existsById(itemId)).thenReturn(true);

        // Act
        itemService.deleteItem(itemId);

        // Assert
        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    public void deleteItem_whenItemDoesNotExist_shouldThrowResourceNotFoundException() {
        // Arrange
        Long itemId = 99L;

        when(itemRepository.existsById(itemId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItem(itemId));
        verify(itemRepository, times(0)).deleteById(itemId);
    }
}
