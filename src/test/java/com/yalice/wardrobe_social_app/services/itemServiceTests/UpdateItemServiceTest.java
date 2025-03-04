package com.yalice.wardrobe_social_app.services.itemServiceTests;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UpdateItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private Item item;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test user
        user = User.builder()
                .id(123L)
                .username("alice")
                .email("alice@testemail.com")
                .password("123password")
                .provider(User.Provider.GOOGLE)
                .profilePicture("profilepic.jpeg")
                .build();

        // Initialize test item
        item = Item.builder()
                .id(1L)
                .user(user)
                .name("Test name")
                .category("Test category")
                .imageUrl("Test image url")
                .build();
    }

    // ---- Tests for updateItem ----

    @Test
    public void shouldUpdateItem_WhenItemExists() {
        // Arrange
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto("Updated Name", "Updated Brand", "Updated Category", "M", "Red", "Updated Image URL");
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));
        when(userRepository.findById(item.getUser().getId())).thenReturn(java.util.Optional.of(user));

        // Act
        ItemResponseDto updatedItem = itemService.updateItem(itemId, itemDto);

        // Assert
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo("Updated Name");
        assertThat(updatedItem.getBrand()).isEqualTo("Updated Brand");
        assertThat(updatedItem.getCategory()).isEqualTo("Updated Category");
        assertThat(updatedItem.getSize()).isEqualTo("M");
        assertThat(updatedItem.getColor()).isEqualTo("Red");
        assertThat(updatedItem.getImageUrl()).isEqualTo("Updated Image URL");

        verify(itemRepository).findById(itemId);
        verify(itemRepository).saveAndFlush(any(Item.class)); // Verify save operation
        verify(userRepository).findById(item.getUser().getId());
    }

    @Test
    public void shouldThrowIllegalArgumentException_WhenItemIdIsNull() {
        // Arrange
        ItemDto itemDto = new ItemDto("Updated Name", "Updated Brand", "Updated Category", "M", "Red", "Updated Image URL");

        // Act & Assert
        assertThatThrownBy(() -> itemService.updateItem(null, itemDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item ID and Item cannot be null");

        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    public void shouldThrowIllegalArgumentException_WhenItemDtoIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> itemService.updateItem(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item ID and Item cannot be null");

        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenItemDoesNotExist() {
        // Arrange
        Long itemId = 99L;
        ItemDto itemDto = new ItemDto("Updated Name", "Updated Brand", "Updated Category", "M", "Red", "Updated Image URL");
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.updateItem(itemId, itemDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with ID");

        verify(itemRepository).findById(itemId);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        // Arrange
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto("Updated Name", "Updated Brand", "Updated Category", "M", "Red", "Updated Image URL");
        when(itemRepository.findById(itemId)).thenReturn(java.util.Optional.of(item));
        when(userRepository.findById(item.getUser().getId())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.updateItem(itemId, itemDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(item.getUser().getId());
    }
}
