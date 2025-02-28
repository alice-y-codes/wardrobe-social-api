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

        user = User.builder()
                .id(valueOf(456))
                .username("alice")
                .email("alice@testemail.com")
                .password("123password")
                .provider(User.Provider.GOOGLE)
                .profilePicture("profilepic.jpeg")
                .items(new ArrayList<>())
                .outfits(new ArrayList<>())
                .build();

        item = Item.builder()
                .userId(valueOf(456))
                .name("Test name")
                .category("Test category")
                .imageUrl("Test image url")
                .build();
    }

    @Test
    public void shouldCreateItem() {
        // Arrange
        when(itemRepository.findByName("Test name")).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        Long userId = valueOf(456);

        // Act
        Optional<Item> createdItem = itemService.createItem(userId, item);

        // Assert
        assertThat(createdItem).isPresent();
        assertThat(createdItem.get().getName()).isEqualTo("Test name");
        assertThat(createdItem.get().getCategory()).isEqualTo("Test category");
        assertThat(createdItem.get().getImageUrl()).isEqualTo("Test image url");
        assertThat(createdItem.get().getUserId()).isEqualTo(userId);

        verify(itemRepository).save(any(Item.class));
        verify(userRepository).save(any(User.class));
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
        when(itemRepository.findByName("Test name")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        Long userId = valueOf(456);

        // Act
        Optional<Item> createdItem = itemService.createItem(userId, item);

        // Assert
        assertThat(createdItem).isPresent();
        assertThat(createdItem.get().getName()).isEqualTo("Test name");
        assertThat(createdItem.get().getCategory()).isEqualTo("Test category");
        assertThat(createdItem.get().getUserId()).isEqualTo(userId);

        verify(userRepository).save(any(User.class));
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void shouldNotCreateItem_WhenItemAlreadyExists() {
        // Arrange
        when(itemRepository.findByName("Test name")).thenReturn(Optional.of(item));
        Long userId = valueOf(456);

        // Act
        Optional<Item> createdItem = itemService.createItem(userId, item);

        // Assert
        assertThat(createdItem).isNotPresent();
        verify(itemRepository, never()).save(any(Item.class));
    }
}
