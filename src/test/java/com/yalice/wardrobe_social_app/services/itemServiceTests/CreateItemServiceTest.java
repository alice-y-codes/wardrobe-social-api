package com.yalice.wardrobe_social_app.services.itemServiceTests;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static java.lang.Long.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateItemServiceTest {

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

        user = new User(valueOf(456), "alice", "alice@testemail.com", "123password", "google", "profilepic.jpeg", new ArrayList<>(), new ArrayList<>());

        item = new Item();
        item.setUserId(valueOf(456));
        item.setName("Test name");
        item.setCategory("Test category");
        item.setImageUrl("Test image url");
    }

    @Test
    public void shouldCreateItem() {
        // Arrange
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Long userId = valueOf(456);


        // Act
        Optional<Item> createdItem = itemService.createItem(userId, item);

        // Assert
        assertThat(createdItem).isPresent();
        assertThat(createdItem.get().getName()).isEqualTo("Test name");
        assertThat(createdItem.get().getCategory()).isEqualTo("Test category");
        assertThat(createdItem.get().getImageUrl()).isEqualTo("Test image url");

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void shouldNotCreateItem_WhenItemIsNull() {
        // Arrange
        Long userId = valueOf(456);

        // Act
        Optional<Item> createdItem = itemService.createItem(userId, null);

        // Assert
        assertThat(createdItem).isNotPresent();
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void shouldSaveUser_WhenCreatingItem() {
        // Arrange
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        Long userId = valueOf(456);

        // Act
        Optional<Item> createdItem = itemService.createItem(userId, item);

        // Assert
        assertThat(createdItem).isPresent();
        assertThat(createdItem.get().getName()).isEqualTo("Test name");
        assertThat(createdItem.get().getCategory()).isEqualTo("Test category");

        verify(userRepository).save(user);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void shouldNotCreateItem_WhenItemAlreadyExists() {
        // Arrange
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemRepository.findByItemName("Test name")).thenReturn(Optional.of(item));
        Long userId = valueOf(456);

        // Act
        Optional<Item> createdItem = itemService.createItem(userId, item);

        // Assert
        assertThat(createdItem).isNotPresent();
        verify(itemRepository, never()).save(any(Item.class));
    }
}
