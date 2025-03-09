package com.yalice.wardrobe_social_app.services.itemServiceTests;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private WardrobeRepository wardrobeRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Profile profile;
    private User user;
    private Wardrobe wardrobe;
    private ItemDto itemDto;
    private Item item;
    private MultipartFile image;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .username("testusername")
                .build();

        user.setId(1L);

        // Create mock entities
        profile = new Profile();
        profile.setId(1L);
        profile.setUser(user);
        wardrobe = new Wardrobe();
        wardrobe.setId(1L);

        itemDto = new ItemDto();
        itemDto.setName("T-shirt");
        itemDto.setBrand("BrandX");
        itemDto.setCategory("Casual");
        itemDto.setSize("M");
        itemDto.setColor("Red");

        item = new Item();
        item.setId(1L);
        item.setName("T-shirt");
        item.setBrand("BrandX");
        item.setCategory("Casual");
        item.setSize("M");
        item.setColor("Red");
        item.setWardrobe(wardrobe);
        item.setProfile(profile);

        image = mock(MultipartFile.class);
    }

    @Test
    void createItem_ShouldCreateItemSuccessfully() {
        // Given
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(wardrobeRepository.findById(1L)).thenReturn(Optional.of(wardrobe));
        when(itemRepository.findByNameAndWardrobeId("T-shirt", 1L)).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // When
        ItemResponseDto response = itemService.createItem(1L, 1L, itemDto, image);

        // Then
        assertNotNull(response);
        assertEquals("T-shirt", response.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_ShouldThrowException_WhenItemAlreadyExists() {
        // Given
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(wardrobeRepository.findById(1L)).thenReturn(Optional.of(wardrobe));
        when(itemRepository.findByNameAndWardrobeId("T-shirt", 1L)).thenReturn(Optional.of(item));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            itemService.createItem(1L, 1L, itemDto, image);
        });
    }

    @Test
    void updateItem_ShouldUpdateItemSuccessfully() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(itemRepository.saveAndFlush(any(Item.class))).thenReturn(item);

        // When
        ItemResponseDto response = itemService.updateItem(1L, 1L, itemDto, image);

        // Then
        assertNotNull(response);
        assertEquals("T-shirt", response.getName());
        verify(itemRepository, times(1)).saveAndFlush(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowException_WhenItemNotFound() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.updateItem(1L, 1L, itemDto, image);
        });
    }

    @Test
    void deleteItem_ShouldDeleteItemSuccessfully() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // When
        itemService.deleteItem(1L, 1L);

        // Then
        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteItem_ShouldThrowException_WhenItemNotFound() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.deleteItem(1L, 1L);
        });
    }

    @Test
    void getUserItems_ShouldReturnItemsSuccessfully() {
        // Given
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(wardrobeRepository.findByProfileId(1L)).thenReturn(Optional.of(wardrobe));
        when(itemRepository.findAllByWardrobeId(1L)).thenReturn(List.of(item));

        // When
        List<ItemResponseDto> items = itemService.getUserItems(1L);

        // Then
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("T-shirt", items.get(0).getName());
    }

    @Test
    void getItem_ShouldReturnItemSuccessfully() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        // When
        ItemResponseDto response = itemService.getItem(1L);

        // Then
        assertNotNull(response);
        assertEquals("T-shirt", response.getName());
    }

    @Test
    void getItem_ShouldThrowException_WhenItemNotFound() {
        // Given
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            itemService.getItem(1L);
        });
    }
}
