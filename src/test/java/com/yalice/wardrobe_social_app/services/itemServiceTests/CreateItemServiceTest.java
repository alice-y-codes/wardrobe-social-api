package com.yalice.wardrobe_social_app.services.itemServiceTests;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.ItemServiceImpl;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CreateItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private Item item;
    private User user;
    private ItemDto itemDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock user
        user = User.builder()
                .id(456L)
                .username("alice")
                .email("alice@testemail.com")
                .password("123password")
                .provider(User.Provider.GOOGLE)
                .profilePicture("profilepic.jpeg")
                .items(new ArrayList<>())
                .outfits(new ArrayList<>())
                .build();

        // Mock itemDto
        itemDto = new ItemDto("Test name", "BrandX", "Shoes", "M", "Red", "http://example.com/image.jpg");

        // Mock item
        item = Item.builder()
                .user(user)
                .name("Test name")
                .category("Shoes")
                .imageUrl("http://example.com/image.jpg")
                .build();
    }

    @Test
    public void createItem_userNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            itemService.createItem(user.getId(), itemDto);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void createItem_itemAlreadyExists_throwsIllegalStateException() {
        // Arrange
        when(itemRepository.findByName("Test name")).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            itemService.createItem(user.getId(), itemDto);
        });
        assertEquals("Item with this name already exists", exception.getMessage());
    }

    @Test
    public void createItem_nullItem_throwsIllegalArgumentException() {
        // Arrange
        Long userId = 456L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem(userId, null);
        });
        assertEquals("User ID and Item cannot be null", exception.getMessage());
    }

    @Test
    public void createItem_nullUserId_throwsIllegalArgumentException() {
        // Arrange
        Long userId = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem(userId, itemDto);
        });
        assertEquals("User ID and Item cannot be null", exception.getMessage());
    }

    @Test
    public void createItem_success_returnsItemResponseDto() {
        // Arrange
        when(itemRepository.findByName("Test name")).thenReturn(Optional.empty());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // Act
        ItemResponseDto itemResponseDto = itemService.createItem(user.getId(), itemDto);

        // Assert
        assertNotNull(itemResponseDto);
        assertEquals("Test name", itemResponseDto.getName());
        assertEquals("Shoes", itemResponseDto.getCategory());
        assertEquals("Red", itemResponseDto.getColor());
        assertEquals("http://example.com/image.jpg", itemResponseDto.getImageUrl());
        assertEquals(user.getId(), itemResponseDto.getUserId());

        verify(itemRepository).save(any(Item.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void createItem_itemAlreadyExists_doesNotSaveItem() {
        // Arrange
        when(itemRepository.findByName("Test name")).thenReturn(Optional.of(item));
        Long userId = 456L;

        // Act
        ItemResponseDto itemResponseDto = itemService.createItem(userId, itemDto);

        // Assert
        assertNull(itemResponseDto);
        verify(itemRepository, never()).save(any(Item.class));
    }
}
