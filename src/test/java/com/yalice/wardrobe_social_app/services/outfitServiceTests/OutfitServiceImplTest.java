package com.yalice.wardrobe_social_app.services.outfitServiceTests;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.*;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.services.OutfitServiceImpl;
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
import jakarta.persistence.EntityNotFoundException;
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

class OutfitServiceImplTest {

    @Mock
    private OutfitRepository outfitRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private DtoConversionService dtoConversionService;

    @InjectMocks
    private OutfitServiceImpl outfitService;

    private Profile profile;
    private Outfit outfit;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Wardrobe wardrobe = new Wardrobe();
        wardrobe.setId(1L); // Set the Wardrobe ID

        // Create mock profile and outfit objects
        profile = new Profile();
        profile.setId(1L);

        User user = new User();
        user.setUsername("testUser");
        user.setId(1L);
        profile.setUser(user);

        outfit = new Outfit();
        outfit.setId(1L);
        outfit.setProfile(profile);
        outfit.setName("Summer Outfit");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setWardrobe(wardrobe);
        item.setProfile(profile);

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

        OutfitResponseDto mockResponseDto = new OutfitResponseDto();
        mockResponseDto.setName("Summer Outfit");
        mockResponseDto.setDescription("A cool summer outfit");
        mockResponseDto.setSeason("Summer");
        mockResponseDto.setFavorite(false);
        mockResponseDto.setPublic(true);

        when(dtoConversionService.convertToOutfitResponseDto(any(Outfit.class))).thenReturn(mockResponseDto);

        // Call the method
        OutfitResponseDto response = outfitService.createOutfit(1L, outfitDto, image);

        // Verify
        assertNotNull(response);
        assertEquals("Summer Outfit", response.getName());
        assertEquals("A cool summer outfit", response.getDescription());
        assertEquals("Summer", response.getSeason());
        assertFalse(response.isFavorite());
        assertTrue(response.isPublic());
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

        OutfitResponseDto mockResponseDto = new OutfitResponseDto();
        mockResponseDto.setName("Updated Outfit");
        mockResponseDto.setDescription("Updated description");
        mockResponseDto.setSeason("Winter");
        mockResponseDto.setFavorite(true);
        mockResponseDto.setPublic(false);

        when(dtoConversionService.convertToOutfitResponseDto(any(Outfit.class))).thenReturn(mockResponseDto);

        // Call method
        OutfitResponseDto response = outfitService.updateOutfit(1L, 1L, outfitDto, image);

        // Verify
        assertNotNull(response);
        assertEquals("Updated Outfit", response.getName());
        assertEquals("Updated description", response.getDescription());
        assertEquals("Winter", response.getSeason());
        assertTrue(response.isFavorite());
        assertFalse(response.isPublic());
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
        // Setup mock Outfit
        Outfit mockOutfit = new Outfit();
        mockOutfit.setName("Summer Outfit");
        mockOutfit.setDescription("A cool summer outfit");
        mockOutfit.setSeason("Summer");
        mockOutfit.setFavorite(false);
        mockOutfit.setPublic(true);

        // Create the expected OutfitResponseDto
        OutfitResponseDto mockResponseDto = new OutfitResponseDto();
        mockResponseDto.setName("Summer Outfit");
        mockResponseDto.setDescription("A cool summer outfit");
        mockResponseDto.setSeason("Summer");
        mockResponseDto.setFavorite(false);
        mockResponseDto.setPublic(true);

        // Mock the repository to return a list containing the mock outfit
        when(outfitRepository.findByProfileId(1L)).thenReturn(List.of(mockOutfit));

        // Mock the conversion service to return the expected OutfitResponseDto
        when(dtoConversionService.convertToOutfitResponseDto(any(Outfit.class))).thenReturn(mockResponseDto);

        // Call the method
        List<OutfitResponseDto> response = outfitService.getUserOutfits(1L);

        // Verify the result
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Summer Outfit", response.get(0).getName());
        assertEquals("A cool summer outfit", response.get(0).getDescription());
        assertEquals("Summer", response.get(0).getSeason());
        assertFalse(response.get(0).isFavorite());
        assertTrue(response.get(0).isPublic());

        // Verify repository interaction
        verify(outfitRepository, times(1)).findByProfileId(1L);
        verify(dtoConversionService, times(1)).convertToOutfitResponseDto(any(Outfit.class));
    }


    @Test
    void testGetOutfit() {
        OutfitResponseDto mockResponseDto = new OutfitResponseDto();

        mockResponseDto.setName("Summer Outfit");
        mockResponseDto.setDescription("A cool summer outfit");
        mockResponseDto.setSeason("Summer");
        mockResponseDto.setFavorite(false);
        mockResponseDto.setPublic(true);

        when(outfitRepository.findById(1L)).thenReturn(Optional.of(outfit));
        when(dtoConversionService.convertToOutfitResponseDto(any(Outfit.class))).thenReturn(mockResponseDto);

        OutfitResponseDto response = outfitService.getOutfit(1L);

        // Verify
        assertNotNull(response);
        assertEquals("Summer Outfit", response.getName());
        verify(outfitRepository, times(1)).findById(1L);
        verify(dtoConversionService, times(1)).convertToOutfitResponseDto(any(Outfit.class));
    }

    @Test
    void testAddItemToOutfit() {
        // Setup
        when(itemService.getItemEntity(1L)).thenReturn(item);
        when(outfitRepository.save(outfit)).thenReturn(outfit);

        OutfitResponseDto mockResponseDto = new OutfitResponseDto();
        mockResponseDto.setName("Summer Outfit");
        mockResponseDto.setDescription("A cool summer outfit");
        mockResponseDto.setSeason("Summer");
        mockResponseDto.setFavorite(false);
        mockResponseDto.setPublic(true);

        when(dtoConversionService.convertToOutfitResponseDto(any(Outfit.class))).thenReturn(mockResponseDto);

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

        OutfitResponseDto mockResponseDto = new OutfitResponseDto();
        mockResponseDto.setName("Summer Outfit");
        mockResponseDto.setDescription("A cool summer outfit");
        mockResponseDto.setSeason("Summer");
        mockResponseDto.setFavorite(false);
        mockResponseDto.setPublic(true);

        when(dtoConversionService.convertToOutfitResponseDto(any(Outfit.class))).thenReturn(mockResponseDto);

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
