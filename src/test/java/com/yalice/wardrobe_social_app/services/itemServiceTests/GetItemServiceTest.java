package com.yalice.wardrobe_social_app.services.itemServiceTests;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
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

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
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
        user = new User(valueOf(123), "alice", "alice@testemail.com", "123password", "google", "profilepic.jpeg", new ArrayList<>(), new ArrayList<>());

        item = new Item();
        item.setId(1L);
        item.setUserId(valueOf(123));
        item.setName("Test name");
        item.setCategory("Test category");
        item.setImageUrl("Test image url");
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
        when(itemRepository.findByItemName(itemName)).thenReturn(Optional.of(item));

        // Act
        Optional<Item> foundItem = itemService.getItemByName(itemName);

        // Assert
        assertThat(foundItem).isPresent().contains(item);
        verify(itemRepository).findByItemName(itemName);
    }

    @Test
    public void shouldReturnEmpty_WhenItemDoesNotExistByName() {
        // Arrange
        String itemName = "Nonexistent Item";
        when(itemRepository.findByItemName(itemName)).thenReturn(Optional.empty());

        // Act
        Optional<Item> foundItem = itemService.getItemByName(itemName);

        // Assert
        assertThat(foundItem).isNotPresent();
        verify(itemRepository).findByItemName(itemName);
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
        Item testItem1 = new Item();
        testItem1.setUserId(userId);
        testItem1.setName("T-Shirt");
        testItem1.setCategory("Test category 1");
        testItem1.setImageUrl("Test image url 1");

        Item testItem2 = new Item();
        testItem2.setUserId(userId);
        testItem2.setName("Jeans");
        testItem2.setCategory("Test category 2");
        testItem2.setImageUrl("Test image url 2");

        Item testItem3 = new Item();
        testItem3.setUserId(userId);
        testItem3.setName("Shoes");
        testItem3.setCategory("Test category 3");
        testItem3.setImageUrl("Test image url 3");


        List<Item> mockItems = List.of(
                testItem1, testItem2, testItem3
        );
        return mockItems;
    }


    @Test
    public void shouldReturnEmpty_WhenItemNameIsNull() {
        // Act
        Optional<Item> foundItem = itemService.getItemByName(null);

        // Assert
        assertThat(foundItem).isNotPresent();
        verify(itemRepository, never()).findByItemName(any());
    }

    @Test
    public void shouldReturnEmpty_WhenItemNameIsEmpty() {
        // Act
        Optional<Item> foundItem = itemService.getItemByName("");

        // Assert
        assertThat(foundItem).isNotPresent();
        verify(itemRepository, never()).findByItemName(any());
    }


}
