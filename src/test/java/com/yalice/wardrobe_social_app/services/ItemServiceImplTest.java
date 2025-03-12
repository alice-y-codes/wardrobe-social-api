package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.mappers.ItemMapper;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.core.ItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private WardrobeRepository wardrobeRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Profile profile;
    private Wardrobe wardrobe;
    private ItemDto itemDto;
    private Item item;
    private ItemResponseDto itemResponseDto;
    private MultipartFile image;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .username("testusername")
                .build();

        profile = Profile.builder()
                .id(1L)
                .user(user)
                .build();

        wardrobe = Wardrobe.builder()
                .id(1L)
                .build();

        itemDto = ItemDto.builder()
                .name("T-shirt")
                .brand("BrandX")
                .category("Casual")
                .size("M")
                .color("Red")
                .build();

        item = Item.builder()
                .id(1L)
                .name("T-shirt")
                .brand("BrandX")
                .category("Casual")
                .size("M")
                .color("Red")
                .wardrobe(wardrobe)
                .profile(profile)
                .build();

        image = mock(MultipartFile.class);

        itemResponseDto = ItemResponseDto.builder()
                .name("T-shirt")
                .brand("BrandX")
                .category("Casual")
                .size("M")
                .color("Red")
                .build();
    }

    @Test
    void shouldCreateItemSuccessfully() {
        // Arrange
        when(profileRepository.findById(eq(1L))).thenReturn(Optional.of(profile));
        when(wardrobeRepository.findById(eq(1L))).thenReturn(Optional.of(wardrobe));
        when(itemRepository.findByNameAndWardrobeId(eq("T-shirt"), eq(1L))).thenReturn(Optional.empty());
        when(itemRepository.save(argThat(itemArg -> itemArg.getName().equals("T-shirt")))).thenReturn(item);
        when(itemMapper.toResponseDto(argThat(itemArg -> itemArg.getName().equals("T-shirt")))).thenReturn(itemResponseDto);

        // Act
        ItemResponseDto response = itemService.createItem(1L, 1L, itemDto, image);

        // Assert
        assertNotNull(response);
        assertEquals("T-shirt", response.getName());
        assertEquals("BrandX", response.getBrand());
        assertEquals("Casual", response.getCategory());
        assertEquals("M", response.getSize());
        assertEquals("Red", response.getColor());
        verify(itemRepository, times(1)).save(argThat(itemArg -> itemArg.getName().equals("T-shirt")));
    }

    @Test
    void shouldThrowExceptionWhenItemAlreadyExists() {
        // Arrange
        when(profileRepository.findById(eq(1L))).thenReturn(Optional.of(profile));
        when(wardrobeRepository.findById(eq(1L))).thenReturn(Optional.of(wardrobe));
        when(itemRepository.findByNameAndWardrobeId(eq("T-shirt"), eq(1L))).thenReturn(Optional.of(item));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> itemService.createItem(1L, 1L, itemDto, image));
        assertEquals("Item already exists in the wardrobe", exception.getMessage());
    }

    @Test
    void shouldUpdateItemSuccessfully() {
        // Arrange
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item));
        when(itemRepository.saveAndFlush(argThat(itemArg -> itemArg.getId().equals(1L)))).thenReturn(item);
        when(itemMapper.toResponseDto(argThat(itemArg -> itemArg.getName().equals("T-shirt")))).thenReturn(itemResponseDto);

        // Act
        ItemResponseDto response = itemService.updateItem(1L, 1L, itemDto, image);

        // Assert
        assertNotNull(response);
        assertEquals("T-shirt", response.getName());
        verify(itemRepository, times(1)).saveAndFlush(argThat(itemArg -> itemArg.getId().equals(1L)));
    }

    @Test
    void shouldThrowExceptionWhenItemNotFoundForUpdate() {
        // Arrange
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> itemService.updateItem(1L, 1L, itemDto, image));
        assertEquals("Item not found with ID: 1", exception.getMessage());
    }

    @Test
    void shouldDeleteItemSuccessfully() {
        // Arrange
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item));

        // Act
        itemService.deleteItem(1L, 1L);

        // Assert
        verify(itemRepository, times(1)).deleteById(eq(1L));
    }

    @Test
    void shouldThrowExceptionWhenItemNotFoundForDeletion() {
        // Arrange
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItem(1L, 1L));
        assertEquals("Item not found with ID: 1", exception.getMessage());
    }

    @Test
    void shouldReturnItemsSuccessfully() {
        // Arrange
        when(profileRepository.findById(eq(1L))).thenReturn(Optional.of(profile));
        when(wardrobeRepository.findByProfileId(eq(1L))).thenReturn(Optional.of(wardrobe));
        when(itemRepository.findAllByWardrobeId(eq(1L))).thenReturn(List.of(item));
        when(itemMapper.toResponseDto(argThat(itemArg -> itemArg.getName().equals("T-shirt")))).thenReturn(itemResponseDto);

        // Act
        List<ItemResponseDto> items = itemService.getUserItems(1L);

        // Assert
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("T-shirt", items.getFirst().getName());
        assertEquals("BrandX", items.getFirst().getBrand());
        assertEquals("Casual", items.getFirst().getCategory());
        assertEquals("M", items.getFirst().getSize());
        assertEquals("Red", items.getFirst().getColor());
    }

    @Test
    void shouldReturnItemSuccessfully() {
        // Arrange
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.of(item));
        when(itemMapper.toResponseDto(argThat(itemArg -> itemArg.getName().equals("T-shirt")))).thenReturn(itemResponseDto);

        // Act
        ItemResponseDto response = itemService.getItem(1L);

        // Assert
        assertNotNull(response);
        assertEquals("T-shirt", response.getName());
        assertEquals("BrandX", response.getBrand());
        assertEquals("Casual", response.getCategory());
        assertEquals("M", response.getSize());
        assertEquals("Red", response.getColor());
    }

    @Test
    void shouldThrowExceptionWhenItemNotFoundForRetrieval() {
        // Arrange
        when(itemRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> itemService.getItem(1L));
        assertEquals("Item not found with ID: 1", exception.getMessage());
    }
}
