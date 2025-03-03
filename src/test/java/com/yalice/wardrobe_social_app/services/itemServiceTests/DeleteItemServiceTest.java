package com.yalice.wardrobe_social_app.services.itemServiceTests;

import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.services.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    public void testDeleteItem() {
        // Arrange
        Long itemId = 1L;

        // Act
        itemService.deleteItem(itemId);

        // Assert
        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    public void testDeleteNonExistentItem() {
        // Arrange
        Long itemId = 99L; // Assume this ID does not exist

        // Act
        itemService.deleteItem(itemId);

        // Assert
        verify(itemRepository, times(1)).deleteById(itemId);
    }
}
