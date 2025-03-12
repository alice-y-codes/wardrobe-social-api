package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.*;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.mappers.OutfitMapper;
import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.services.core.OutfitServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutfitServiceImplTest {

    @Mock
    private OutfitRepository outfitRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private OutfitMapper outfitMapper;

    @InjectMocks
    private OutfitServiceImpl outfitService;

    private User user;
    private Profile profile;
    private Item item;
    private Outfit outfit;
    private OutfitDto outfitDto;
    private OutfitResponseDto outfitResponseDto;
    private MultipartFile image;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        profile = new Profile();
        profile.setId(1L);
        profile.setUser(user);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setWardrobe(new Wardrobe());
        item.setProfile(profile);

        outfit = new Outfit();
        outfit.setId(1L);
        outfit.setProfile(profile);
        outfit.setName("Summer Outfit");

        outfitDto = new OutfitDto();
        outfitDto.setName("Summer Outfit");
        outfitDto.setDescription("A cool summer outfit");
        outfitDto.setSeason("Summer");
        outfitDto.setFavorite(false);
        outfitDto.setPublic(true);

        outfitResponseDto = new OutfitResponseDto();
        outfitResponseDto.setName("Summer Outfit");
        outfitResponseDto.setDescription("A cool summer outfit");
        outfitResponseDto.setSeason("Summer");
        outfitResponseDto.setFavorite(false);
        outfitResponseDto.setPublic(true);

        image = mock(MultipartFile.class);
    }

    @Test
    void shouldCreateOutfitSuccessfully() {
        // Stubbing
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(outfitRepository.save(argThat(outfitArg -> "Summer Outfit".equals(outfitArg.getName())))).thenReturn(outfit);
        when(outfitMapper.toResponseDto(argThat(outfitArg -> "Summer Outfit".equals(outfitArg.getName())))).thenReturn(outfitResponseDto);

        // Act
        OutfitResponseDto response = outfitService.createOutfit(1L, outfitDto, image);

        // Assert
        assertNotNull(response);
        assertEquals("Summer Outfit", response.getName());
        assertEquals("A cool summer outfit", response.getDescription());
        verify(outfitRepository).save(argThat(outfitArg -> "Summer Outfit".equals(outfitArg.getName())));
        verify(outfitMapper).toResponseDto(argThat(outfitArg -> "Summer Outfit".equals(outfitArg.getName())));
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFoundDuringOutfitCreation() {
        // Stubbing
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> outfitService.createOutfit(1L, outfitDto, image));
    }

    @Test
    void shouldUpdateOutfitSuccessfully() {
        OutfitDto updatedOutfitDto = new OutfitDto();
        updatedOutfitDto.setName("Updated Outfit");
        updatedOutfitDto.setDescription("Updated description");
        updatedOutfitDto.setSeason("Winter");
        updatedOutfitDto.setFavorite(true);
        updatedOutfitDto.setPublic(false);

        OutfitResponseDto updatedOutfitResponseDto = new OutfitResponseDto();
        updatedOutfitResponseDto.setName("Updated Outfit");
        updatedOutfitResponseDto.setDescription("Updated description");
        updatedOutfitResponseDto.setSeason("Winter");
        updatedOutfitResponseDto.setFavorite(true);
        updatedOutfitResponseDto.setPublic(false);

        // Stubbing
        when(outfitRepository.findById(1L)).thenReturn(Optional.of(outfit));
        when(outfitRepository.save(argThat(outfitArg -> "Updated Outfit".equals(outfitArg.getName())))).thenReturn(outfit);
        when(outfitMapper.toResponseDto(argThat(outfitArg -> "Updated Outfit".equals(outfitArg.getName())))).thenReturn(updatedOutfitResponseDto);

        // Act
        OutfitResponseDto response = outfitService.updateOutfit(1L, 1L, updatedOutfitDto, image);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Outfit", response.getName());
        assertEquals("Updated description", response.getDescription());
        verify(outfitRepository).save(argThat(outfitArg -> "Updated Outfit".equals(outfitArg.getName())));
        verify(outfitMapper).toResponseDto(argThat(outfitArg -> "Updated Outfit".equals(outfitArg.getName())));
    }

    @Test
    void shouldThrowExceptionWhenOutfitNotFoundForUpdate() {
        OutfitDto updatedOutfitDto = new OutfitDto();
        updatedOutfitDto.setName("Updated Outfit");
        updatedOutfitDto.setDescription("Updated description");
        updatedOutfitDto.setSeason("Winter");
        updatedOutfitDto.setFavorite(true);
        updatedOutfitDto.setPublic(false);

        // Stubbing
        when(outfitRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> outfitService.updateOutfit(1L, 1L, updatedOutfitDto, image));
    }

    @Test
    void shouldDeleteOutfitSuccessfully() {
        // Stubbing
        when(outfitRepository.findById(1L)).thenReturn(Optional.of(outfit));

        // Act
        outfitService.deleteOutfit(1L, 1L);

        // Assert
        verify(outfitRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenOutfitNotFoundForDeletion() {
        // Stubbing
        when(outfitRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> outfitService.deleteOutfit(1L, 1L));
    }

    @Test
    void shouldReturnUserOutfitsSuccessfully() {
        // Stubbing
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(outfitRepository.findByProfileId(1L)).thenReturn(List.of(outfit));
        when(outfitMapper.toResponseDto(argThat(outfitArg -> "Summer Outfit".equals(outfitArg.getName())))).thenReturn(outfitResponseDto);

        // Act
        List<OutfitResponseDto> response = outfitService.getUserOutfits(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Summer Outfit", response.get(0).getName());
        verify(outfitRepository).findByProfileId(1L);
    }

    @Test
    void shouldReturnOutfitSuccessfully() {
        // Stubbing only the necessary methods
        when(outfitRepository.findById(1L)).thenReturn(Optional.of(outfit));
        when(outfitMapper.toResponseDto(argThat(outfitArg -> "Summer Outfit".equals(outfitArg.getName())))).thenReturn(outfitResponseDto);

        // Act
        OutfitResponseDto response = outfitService.getOutfit(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Summer Outfit", response.getName());
        verify(outfitRepository).findById(1L);
    }
}
