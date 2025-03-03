package com.yalice.wardrobe_social_app.services.itemServiceTests;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UpdateItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private Item existingItem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setUserId(1L);
        existingItem.setName("Old Name");
        existingItem.setCategory("Old Category");
        existingItem.setImageUrl("old-image.jpg");
    }

    @Test
    public void updateItem_whenItemExists_shouldReturnUpdatedItem() {
        // Arrange
        Long itemId = 1L;

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setUserId(1L);
        updatedItem.setName("New Name");
        updatedItem.setCategory("New Category");
        updatedItem.setImageUrl("new-image.jpg");

        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(existingItem));

        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Item result = itemService.updateItem(itemId, updatedItem);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getCategory()).isEqualTo("New Category");
        assertThat(result.getImageUrl()).isEqualTo("new-image.jpg");
        assertThat(result.getUserId()).isEqualTo(1L); // Ensure userId is preserved

        // Verify interactions
        verify(itemRepository).findById(eq(itemId));
        verify(itemRepository).saveAndFlush(any(Item.class));
    }

    @Test
    public void shouldReturnExistingItem_WhenItemIsNull() {
        // Arrange
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // Act
        Item result = itemService.updateItem(itemId, null);

        // Assert
        assertThat(result).isEqualTo(existingItem);
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).saveAndFlush(any());
    }

    @Test
    public void shouldReturnNull_WhenItemIsNullAndItemDoesNotExist() {
        // Arrange
        Long itemId = 99L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act
        Item result = itemService.updateItem(itemId, null);

        // Assert
        assertThat(result).isNull();
        verify(itemRepository).findById(itemId);
        verify(itemRepository, never()).saveAndFlush(any());
    }

    @Test
    public void shouldReturnNull_WhenItemIdIsNull() {
        // Act
        Item result = itemService.updateItem(null, existingItem);

        // Assert
        assertThat(result).isNull();
        verify(itemRepository, never()).findById(any());
        verify(itemRepository, never()).saveAndFlush(any());
    }
}
