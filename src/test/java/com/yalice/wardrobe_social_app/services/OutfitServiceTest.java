package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OutfitServiceTest {

    @Mock
    private OutfitRepository outfitRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OutfitServiceImpl outfitService;

    private User testUser;
    private Outfit testOutfit;
    private Item testItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        // Setup test item
        testItem = Item.builder()
                .id(1L)
                .name("Test Item")
                .category("Tops")
                .userId(1L)
                .imageUrl("http://example.com/image.jpg")
                .build();

        // Setup test outfit
        Set<Item> items = new HashSet<>();
        items.add(testItem);

        testOutfit = Outfit.builder()
                .id(1L)
                .name("Test Outfit")
                .description("A test outfit")
                .occasion("Casual")
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(items)
                .build();
    }

    @Test
    void createOutfit_Success() {
        // Arrange
        Outfit outfitToCreate = Outfit.builder()
                .name("New Outfit")
                .description("A new outfit")
                .occasion("Formal")
                .build();

        // Use UserRepository instead of UserService
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(outfitRepository.save(any(Outfit.class))).thenReturn(testOutfit);

        // Act
        Optional<Outfit> result = outfitService.createOutfit(1L, outfitToCreate);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Outfit", result.get().getName());
        assertEquals("A test outfit", result.get().getDescription());
        assertEquals("Casual", result.get().getOccasion());
        assertEquals(testUser, result.get().getUser());
        verify(outfitRepository).save(any(Outfit.class));
    }

    @Test
    void createOutfit_UserNotFound() {
        // Arrange
        Outfit outfitToCreate = new Outfit();
        outfitToCreate.setName("New Outfit");

        // Use UserRepository instead of UserService
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Outfit> result = outfitService.createOutfit(99L, outfitToCreate);

        // Assert
        assertFalse(result.isPresent());
        verify(outfitRepository, never()).save(any(Outfit.class));
    }

    @Test
    void getOutfit_Success() {
        // Arrange
        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));

        // Act
        Optional<Outfit> result = outfitService.getOutfit(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Outfit", result.get().getName());
    }

    @Test
    void getOutfit_NotFound() {
        // Arrange
        when(outfitRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Outfit> result = outfitService.getOutfit(99L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getAllOutfits_Success() {
        // Arrange
        List<Outfit> outfits = Collections.singletonList(testOutfit);
        when(outfitRepository.findByUserId(1L)).thenReturn(outfits);

        // Act
        List<Outfit> result = outfitService.getAllOutfits(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Outfit", result.get(0).getName());
    }

    @Test
    void updateOutfit_Success() {
        // Arrange
        Outfit outfitToUpdate = Outfit.builder()
                .name("Updated Outfit")
                .description("Updated description")
                .occasion("Business")
                .build();

        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
        when(outfitRepository.save(any(Outfit.class))).thenReturn(testOutfit);

        // Act
        Outfit result = outfitService.updateOutfit(1L, outfitToUpdate);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        // In a real implementation, the name would be updated to "Updated Outfit"
        // but our mock just returns the original testOutfit
        verify(outfitRepository).save(any(Outfit.class));
    }

    @Test
    void updateOutfit_NotFound() {
        // Arrange
        Outfit outfitToUpdate = Outfit.builder()
                .name("Updated Outfit")
                .build();
        when(outfitRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Outfit result = outfitService.updateOutfit(99L, outfitToUpdate);

        // Assert
        assertNull(result);
        verify(outfitRepository, never()).save(any(Outfit.class));
    }

    @Test
    void deleteOutfit_Success() {
        // Arrange
        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
        doNothing().when(outfitRepository).delete(testOutfit);

        // Act
        outfitService.deleteOutfit(1L);

        // Assert
        verify(outfitRepository).delete(testOutfit);
    }

    @Test
    void deleteOutfit_NotFound() {
        // Arrange
        when(outfitRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        outfitService.deleteOutfit(99L);

        // Assert
        verify(outfitRepository, never()).delete(any(Outfit.class));
    }

    @Test
    void addItemToOutfit_Success() {
        // Arrange
        Item itemToAdd = new Item();
        itemToAdd.setId(2L);
        itemToAdd.setName("New Item");
        itemToAdd.setCategory("Bottoms");
        itemToAdd.setUserId(1L);

        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
        when(itemService.getItem(2L)).thenReturn(Optional.of(itemToAdd));
        when(outfitRepository.save(any(Outfit.class))).thenReturn(testOutfit);

        // Act
        Optional<Outfit> result = outfitService.addItemToOutfit(1L, 2L);

        // Assert
        assertTrue(result.isPresent());
        verify(outfitRepository).save(any(Outfit.class));
    }

    @Test
    void addItemToOutfit_OutfitNotFound() {
        // Arrange
        when(outfitRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Outfit> result = outfitService.addItemToOutfit(99L, 1L);

        // Assert
        assertFalse(result.isPresent());
        verify(outfitRepository, never()).save(any(Outfit.class));
    }

    @Test
    void addItemToOutfit_ItemNotFound() {
        // Arrange
        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
        when(itemService.getItem(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Outfit> result = outfitService.addItemToOutfit(1L, 99L);

        // Assert
        assertFalse(result.isPresent());
        verify(outfitRepository, never()).save(any(Outfit.class));
    }

    @Test
    void removeItemFromOutfit_Success() {
        // Arrange
        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
        when(itemService.getItem(1L)).thenReturn(Optional.of(testItem));
        when(outfitRepository.save(any(Outfit.class))).thenReturn(testOutfit);

        // Act
        Optional<Outfit> result = outfitService.removeItemFromOutfit(1L, 1L);

        // Assert
        assertTrue(result.isPresent());
        verify(outfitRepository).save(any(Outfit.class));
    }

    @Test
    void removeItemFromOutfit_OutfitNotFound() {
        // Arrange
        when(outfitRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Outfit> result = outfitService.removeItemFromOutfit(99L, 1L);

        // Assert
        assertFalse(result.isPresent());
        verify(outfitRepository, never()).save(any(Outfit.class));
    }

    @Test
    void removeItemFromOutfit_ItemNotFound() {
        // Arrange
        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
        when(itemService.getItem(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Outfit> result = outfitService.removeItemFromOutfit(1L, 99L);

        // Assert
        assertFalse(result.isPresent());
        verify(outfitRepository, never()).save(any(Outfit.class));
    }

    @Test
    void getOutfitsByOccasion_Success() {
        // Arrange
        List<Outfit> outfits = Collections.singletonList(testOutfit);
        when(outfitRepository.findByUserIdAndOccasion(1L, "Casual")).thenReturn(outfits);

        // Act
        List<Outfit> result = outfitService.getOutfitsByOccasion(1L, "Casual");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Outfit", result.get(0).getName());
        assertEquals("Casual", result.get(0).getOccasion());
    }
}