package com.yalice.wardrobe_social_app.services.wardrobeServiceTests;

import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.mappers.WardrobeMapper;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.WardrobeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WardrobeServiceImplTest {

    private static final Long PROFILE_ID = 1L;
    private static final Long WARDROBE_ID = 1L;
    private static final String WARDROBE_NAME = "Casual Wardrobe";
    private static final String UPDATED_WARDROBE_NAME = "Updated Wardrobe";

    @Mock
    private WardrobeRepository wardrobeRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private WardrobeMapper wardrobeMapper;

    @InjectMocks
    private WardrobeServiceImpl wardrobeService;

    private Profile profile;
    private Wardrobe wardrobe;

    @BeforeEach
    public void setUp() {
        profile = new Profile();
        profile.setId(PROFILE_ID);

        wardrobe = new Wardrobe();
        wardrobe.setId(WARDROBE_ID);
        wardrobe.setName(WARDROBE_NAME);
        wardrobe.setProfile(profile);
    }

    @Test
    public void createWardrobe_ShouldCreateWardrobe() {
        WardrobeDto wardrobeDto = new WardrobeDto();
        wardrobeDto.setName(WARDROBE_NAME);

        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(wardrobeRepository.existsByProfileIdAndName(PROFILE_ID, WARDROBE_NAME)).thenReturn(false);
        when(wardrobeRepository.save(any(Wardrobe.class))).thenReturn(wardrobe);

        WardrobeResponseDto responseDto = new WardrobeResponseDto();
        responseDto.setName(WARDROBE_NAME);
        when(wardrobeMapper.toResponseDto(any(Wardrobe.class))).thenReturn(responseDto);

        WardrobeResponseDto result = wardrobeService.createWardrobe(PROFILE_ID, wardrobeDto);

        assertNotNull(result);
        assertEquals(WARDROBE_NAME, result.getName());
        verify(wardrobeRepository, times(1)).save(any(Wardrobe.class));
    }

    @Test
    public void deleteWardrobe_ShouldDeleteWardrobe() {
        when(wardrobeRepository.findById(WARDROBE_ID)).thenReturn(Optional.of(wardrobe));

        boolean result = wardrobeService.deleteWardrobe(WARDROBE_ID);

        assertTrue(result);
        verify(wardrobeRepository, times(1)).deleteById(WARDROBE_ID);
    }

    @Test
    public void deleteWardrobe_ShouldThrowException_WhenWardrobeNotFound() {
        when(wardrobeRepository.findById(WARDROBE_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> wardrobeService.deleteWardrobe(WARDROBE_ID)
        );

        assertEquals("Wardrobe not found with ID: " + WARDROBE_ID, exception.getMessage());
    }
}
