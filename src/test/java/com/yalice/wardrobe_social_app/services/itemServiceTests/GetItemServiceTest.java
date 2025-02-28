package com.yalice.wardrobe_social_app.services.itemServiceTests;

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.ItemServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        user = User.builder()
                .id(valueOf(123))
                .username("alice")
                .email("alice@testemail.com")
                .password("123password")
                .provider(User.Provider.GOOGLE)
                .profilePicture("profilepic.jpeg")
                .items(new ArrayList<>())
                .outfits(new ArrayList<>())
                .build();

        item = Item.builder()
                .id(1L)
                .userId(valueOf(123))
                .name("Test name")
                .category("Test category")
                .imageUrl("Test image url")
                .build();
    }

    // ==============================
    // GET ITEM BY ID TESTS
    // ==============================

    @Test
    public void shouldReturnItem_WhenItemExistsById() {
        // Arrange
        Long itemId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        // Act
        Optional<Item> foundItem = itemService.getItem(itemId);

        // Assert
        assertThat(foundItem).isPresent().contains(item);
        verify(itemRepository).findById(itemId);
    }

    @Test
    public void shouldReturnEmpty_WhenItemDoesNotExistById() {
        // Arrange
        Long itemId = 99L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act
        Optional<Item> foundItem = itemService.getItem(itemId);

        // Assert
        assertThat(foundItem).isNotPresent();
        verify(itemRepository).findById(itemId);
    }

    @Test
    public void shouldReturnEmpty_WhenIdIsNull() {
        // Act
        Optional<Item> foundItem = itemService.getItem(null);

        // Assert
        assertThat(foundItem).isNotPresent();
        verify(itemRepository, never()).findById(any());
    }

    @Test
    public void shouldReturnItem_WhenItemExistsByName() {
        // Arrange
        String itemName = "Test name";
        when(itemRepository.findByName(itemName)).thenReturn(Optional.of(item));

        // Act
        Optional<Item> foundItem = itemService.getItemByName(itemName);

        // Assert
        assertThat(foundItem).isPresent().contains(item);
        verify(itemRepository).findByName(itemName);
    }

    @Test
    public void shouldReturnEmpty_WhenItemDoesNotExistByName() {
        // Arrange
        String itemName = "Nonexistent Item";
        when(itemRepository.findByName(itemName)).thenReturn(Optional.empty());

        // Act
        Optional<Item> foundItem = itemService.getItemByName(itemName);

        // Assert
        assertThat(foundItem).isNotPresent();
        verify(itemRepository).findByName(itemName);
    }

    @Test
    public void shouldReturnAllItemsForUser_WhenUserHasItems() {
        // Arrange
        Long userId = 1L;
        List<Item> mockItems = getItems(userId);

        when(itemRepository.findByUserId(userId)).thenReturn(mockItems);

        // Act
        List<Item> items = itemService.getAllItems(userId);

        // Assert
        assertThat(items).isNotEmpty();
        assertThat(items).hasSize(3);
        assertThat(items.get(0).getName()).isEqualTo("T-Shirt");
        assertThat(items.get(1).getName()).isEqualTo("Jeans");
        assertThat(items.get(2).getName()).isEqualTo("Shoes");

        verify(itemRepository).findByUserId(userId);
    }

    private static List<Item> getItems(Long userId) {
        Item testItem1 = Item.builder()
                .userId(userId)
                .name("T-Shirt")
                .category("Test category 1")
                .imageUrl("Test image url 1")
                .build();

        Item testItem2 = Item.builder()
                .userId(userId)
                .name("Jeans")
                .category("Test category 2")
                .imageUrl("Test image url 2")
                .build();

        Item testItem3 = Item.builder()
                .userId(userId)
                .name("Shoes")
                .category("Test category 3")
                .imageUrl("Test image url 3")
                .build();

        List<Item> mockItems = List.of(
                testItem1, testItem2, testItem3);
        return mockItems;
    }

    @Test
    public void shouldReturnEmpty_WhenItemNameIsNull() {
        // Act
        Optional<Item> foundItem = itemService.getItemByName(null);

        // Assert
        assertThat(foundItem).isNotPresent();
        verify(itemRepository, never()).findByName(any());
    }

    @Test
    public void shouldReturnEmpty_WhenItemNameIsEmpty() {
        // Act
        Optional<Item> foundItem = itemService.getItemByName("");

        // Assert
        assertThat(foundItem).isNotPresent();
        verify(itemRepository, never()).findByName(any());
    }

}
