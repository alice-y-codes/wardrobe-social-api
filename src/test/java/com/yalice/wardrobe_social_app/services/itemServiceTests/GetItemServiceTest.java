package com.yalice.wardrobe_social_app.services.itemServiceTests;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class GetItemServiceTest {

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
                .items(new ArrayList<>())
                .outfits(new ArrayList<>())
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

    // ---- Tests for getItem ----

    @Test
    public void shouldReturnItemResponseDto_WhenItemExistsById() {
        // Arrange
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(item.getUser().getId())).thenReturn(Optional.of(user));

        // Act
        ItemResponseDto itemResponseDto = itemService.getItem(itemId);

        // Assert
        assertThat(itemResponseDto).isNotNull();
        assertThat(itemResponseDto.getName()).isEqualTo("Test name");
        assertThat(itemResponseDto.getCategory()).isEqualTo("Test category");
        assertThat(itemResponseDto.getImageUrl()).isEqualTo("Test image url");
        assertThat(itemResponseDto.getUserId()).isEqualTo(user.getId());

        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(item.getUser().getId());
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenItemDoesNotExistById() {
        // Arrange
        Long itemId = 99L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.getItem(itemId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with ID");

        verify(itemRepository).findById(itemId);
        verify(userRepository, never()).findById(any());
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenUserNotFound() {
        // Arrange
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(item.getUser().getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.getItem(itemId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(item.getUser().getId());
    }

    // ---- Tests for getAllItems ----

    @Test
    public void shouldReturnAllItemResponseDtos_WhenUserHasItems() {
        // Arrange
        Long userId = 123L;
        List<Item> mockItems = getItems(user);
        when(itemRepository.findByUserId(userId)).thenReturn(mockItems);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        List<ItemResponseDto> itemResponseDtos = itemService.getAllItems(userId);

        // Assert
        assertThat(itemResponseDtos).isNotEmpty();
        assertThat(itemResponseDtos).hasSize(3);
        assertThat(itemResponseDtos.get(0).getName()).isEqualTo("T-Shirt");
        assertThat(itemResponseDtos.get(1).getName()).isEqualTo("Jeans");
        assertThat(itemResponseDtos.get(2).getName()).isEqualTo("Shoes");

        verify(itemRepository).findByUserId(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    public void shouldThrowIllegalArgumentException_WhenUserIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> itemService.getAllItems(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User ID cannot be null");

        verify(itemRepository, never()).findByUserId(any());
    }

    @Test
    public void shouldReturnEmptyList_WhenUserHasNoItems() {
        // Arrange
        Long userId = 123L;
        List<Item> mockItems = new ArrayList<>();
        when(itemRepository.findByUserId(userId)).thenReturn(mockItems);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        List<ItemResponseDto> itemResponseDtos = itemService.getAllItems(userId);

        // Assert
        assertThat(itemResponseDtos).isEmpty();
        verify(itemRepository).findByUserId(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenUserNotFoundForGetAllItems() {
        // Arrange
        Long userId = 123L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.getAllItems(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(itemRepository, never()).findByUserId(userId);
        verify(userRepository).findById(userId);
    }

    // ---- Tests for getItemByName ----

    @Test
    public void shouldReturnItemResponseDto_WhenItemExistsByName() {
        // Arrange
        String itemName = "Test name";
        when(itemRepository.findByName(itemName)).thenReturn(Optional.of(item));
        when(userRepository.findById(item.getUser().getId())).thenReturn(Optional.of(user));

        // Act
        ItemResponseDto itemResponseDto = itemService.getItemByName(itemName);

        // Assert
        assertThat(itemResponseDto).isNotNull();
        assertThat(itemResponseDto.getName()).isEqualTo("Test name");
        assertThat(itemResponseDto.getCategory()).isEqualTo("Test category");
        assertThat(itemResponseDto.getImageUrl()).isEqualTo("Test image url");
        assertThat(itemResponseDto.getUserId()).isEqualTo(user.getId());

        verify(itemRepository).findByName(itemName);
        verify(userRepository).findById(item.getUser().getId());
    }

    @Test
    public void shouldThrowResourceNotFoundException_WhenItemDoesNotExistByName() {
        // Arrange
        String itemName = "Nonexistent Item";
        when(itemRepository.findByName(itemName)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.getItemByName(itemName))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Item not found with name");

        verify(itemRepository).findByName(itemName);
        verify(userRepository, never()).findById(any());
    }

    @Test
    public void shouldThrowIllegalArgumentException_WhenItemNameIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> itemService.getItemByName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item name cannot be null or empty");

        verify(itemRepository, never()).findByName(any());
    }

    @Test
    public void shouldThrowIllegalArgumentException_WhenItemNameIsEmpty() {
        // Act & Assert
        assertThatThrownBy(() -> itemService.getItemByName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item name cannot be null or empty");

        verify(itemRepository, never()).findByName(any());
    }

    private static List<Item> getItems(User user) {
        Item testItem1 = Item.builder()
                .user(user)
                .name("T-Shirt")
                .category("Test category 1")
                .imageUrl("Test image url 1")
                .build();

        Item testItem2 = Item.builder()
                .user(user)
                .name("Jeans")
                .category("Test category 2")
                .imageUrl("Test image url 2")
                .build();

        Item testItem3 = Item.builder()
                .user(user)
                .name("Shoes")
                .category("Test category 3")
                .imageUrl("Test image url 3")
                .build();

        return List.of(testItem1, testItem2, testItem3);
    }
}
