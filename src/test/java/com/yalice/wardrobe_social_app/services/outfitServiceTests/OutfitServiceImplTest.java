package com.yalice.wardrobe_social_app.services.outfitServiceTests;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.services.OutfitServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OutfitServiceImplTest {

    @Mock
    private OutfitRepository outfitRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private OutfitServiceImpl outfitService;

    private Profile profile;
    private Outfit outfit;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create mock profile and outfit objects
        profile = new Profile();
        profile.setId(1L);

        outfit = new Outfit();
        outfit.setId(1L);
        outfit.setProfile(profile);
        outfit.setName("Test Outfit");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(outfitRepository.findById(1L)).thenReturn(Optional.of(outfit));
    }

    @Test
    void testCreateOutfit() {
        // Setup
        OutfitDto outfitDto = new OutfitDto();
        outfitDto.setName("Summer Outfit");
        outfitDto.setDescription("A cool summer outfit");
        outfitDto.setSeason("Summer");
        outfitDto.setFavorite(false);
        outfitDto.setPublic(true);

        MultipartFile image = mock(MultipartFile.class);

        when(outfitRepository.save(any(Outfit.class))).thenReturn(outfit);

        // Call the method
        OutfitResponseDto response = outfitService.createOutfit(1L, outfitDto, image);

        // Verify
        assertNotNull(response);
        assertEquals("Summer Outfit", response.getName());
        verify(outfitRepository, times(1)).save(any(Outfit.class));
    }

    @Test
    void testCreateOutfit_ProfileNotFound() {
        // Setup
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());

        OutfitDto outfitDto = new OutfitDto();
        outfitDto.setName("Winter Outfit");
        MultipartFile image = mock(MultipartFile.class);

        // Call and assert exception
        assertThrows(EntityNotFoundException.class, () -> outfitService.createOutfit(1L, outfitDto, image));
    }

    @Test
    void testUpdateOutfit() {
        // Setup
        OutfitDto outfitDto = new OutfitDto();
        outfitDto.setName("Updated Outfit");
        outfitDto.setDescription("Updated description");
        outfitDto.setSeason("Winter");
        outfitDto.setFavorite(true);
        outfitDto.setPublic(false);

        MultipartFile image = mock(MultipartFile.class);

        when(outfitRepository.save(any(Outfit.class))).thenReturn(outfit);

        // Call method
        OutfitResponseDto response = outfitService.updateOutfit(1L, 1L, outfitDto, image);

        // Verify
        assertNotNull(response);
        assertEquals("Updated Outfit", response.getName());
        verify(outfitRepository, times(1)).save(any(Outfit.class));
    }

    @Test
    void testUpdateOutfit_OutfitNotFound() {
        // Setup
        when(outfitRepository.findById(1L)).thenReturn(Optional.empty());

        OutfitDto outfitDto = new OutfitDto();
        outfitDto.setName("Updated Outfit");

        MultipartFile image = mock(MultipartFile.class);

        // Call and assert exception
        assertThrows(EntityNotFoundException.class, () -> outfitService.updateOutfit(1L, 1L, outfitDto, image));
    }

    @Test
    void testDeleteOutfit() {
        // Call the method
        outfitService.deleteOutfit(1L, 1L);

        // Verify
        verify(outfitRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteOutfit_OutfitNotFound() {
        // Setup
        when(outfitRepository.findById(1L)).thenReturn(Optional.empty());

        // Call and assert exception
        assertThrows(EntityNotFoundException.class, () -> outfitService.deleteOutfit(1L, 1L));
    }

    @Test
    void testGetUserOutfits() {
        // Setup
        when(outfitRepository.findByProfileId(1L)).thenReturn(List.of(outfit));

        // Call method
        List<OutfitResponseDto> response = outfitService.getUserOutfits(1L);

        // Verify
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test Outfit", response.get(0).getName());
    }

    @Test
    void testGetOutfit() {
        // Call method
        OutfitResponseDto response = outfitService.getOutfit(1L);

        // Verify
        assertNotNull(response);
        assertEquals("Test Outfit", response.getName());
    }

    @Test
    void testAddItemToOutfit() {
        // Setup
        when(itemService.getItemEntity(1L)).thenReturn(item);
        when(outfitRepository.save(outfit)).thenReturn(outfit);

        // Call method
        OutfitResponseDto response = outfitService.addItemToOutfit(1L, 1L);

        // Verify
        assertNotNull(response);
        verify(outfitRepository, times(1)).save(outfit);
    }

    @Test
    void testRemoveItemFromOutfit() {
        // Setup
        when(itemService.getItemEntity(1L)).thenReturn(item);
        when(outfitRepository.save(outfit)).thenReturn(outfit);

        // Call method
        OutfitResponseDto response = outfitService.removeItemFromOutfit(1L, 1L);

        // Verify
        assertNotNull(response);
        verify(outfitRepository, times(1)).save(outfit);
    }

    @Test
    void testAddItemToOutfit_OutfitNotFound() {
        // Setup
        when(outfitRepository.findById(1L)).thenReturn(Optional.empty());

        // Call and assert exception
        assertThrows(EntityNotFoundException.class, () -> outfitService.addItemToOutfit(1L, 1L));
    }

    @Test
    void testRemoveItemFromOutfit_OutfitNotFound() {
        // Setup
        when(outfitRepository.findById(1L)).thenReturn(Optional.empty());

        // Call and assert exception
        assertThrows(EntityNotFoundException.class, () -> outfitService.removeItemFromOutfit(1L, 1L));
    }
}
