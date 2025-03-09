package com.yalice.wardrobe_social_app.services.wardrobeServiceTests;

import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.WardrobeServiceImpl;
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WardrobeServiceImplTest {

    @Mock
    private WardrobeRepository wardrobeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private DtoConversionService dtoConversionService;

    @InjectMocks
    private WardrobeServiceImpl wardrobeService;

    private User user;
    private Profile profile;
    private Wardrobe wardrobe;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        profile = new Profile();
        profile.setId(1L);
        profile.setUser(user);

        wardrobe = new Wardrobe();
        wardrobe.setId(1L);
        wardrobe.setName("Casual Wardrobe");
        wardrobe.setProfile(profile);
    }

    @Test
    public void createWardrobe_ShouldCreateWardrobe() {
        WardrobeDto wardrobeDto = new WardrobeDto();
        wardrobeDto.setName("Casual Wardrobe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(wardrobeRepository.existsByProfileIdAndName(1L, "Casual Wardrobe")).thenReturn(false);
        when(wardrobeRepository.save(any(Wardrobe.class))).thenReturn(wardrobe);

        WardrobeResponseDto responseDto = wardrobeService.createWardrobe(1L, wardrobeDto);

        assertNotNull(responseDto);
        assertEquals("Casual Wardrobe", responseDto.getName());
        verify(wardrobeRepository, times(1)).save(any(Wardrobe.class));
    }

    @Test
    public void createWardrobe_ThrowsException_WhenUserNotFound() {
        WardrobeDto wardrobeDto = new WardrobeDto();
        wardrobeDto.setName("Casual Wardrobe");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> wardrobeService.createWardrobe(1L, wardrobeDto));

        assertEquals("Profile not found with ID: 1", exception.getMessage());
    }

    @Test
    public void createWardrobe_ThrowsException_WhenProfileNotFound() {
        WardrobeDto wardrobeDto = new WardrobeDto();
        wardrobeDto.setName("Casual Wardrobe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> wardrobeService.createWardrobe(1L, wardrobeDto));

        assertEquals("Profile not found with ID: 1", exception.getMessage());
    }

    @Test
    public void getWardrobeById_ShouldReturnWardrobe() {
        when(wardrobeRepository.findById(1L)).thenReturn(Optional.of(wardrobe));


        WardrobeResponseDto mockResponseDto = new WardrobeResponseDto();
        mockResponseDto.setName("Casual Wardrobe");
        when(dtoConversionService.convertToWardrobeResponseDto(any(Wardrobe.class)))
                .thenReturn(mockResponseDto);


        WardrobeResponseDto responseDto = wardrobeService.getWardrobeById(1L);

        assertNotNull(responseDto);
        assertEquals("Casual Wardrobe", responseDto.getName());
    }

    @Test
    public void getWardrobeById_ThrowsException_WhenWardrobeNotFound() {
        when(wardrobeRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> wardrobeService.getWardrobeById(1L));

        assertEquals("Wardrobe not found with ID: 1", exception.getMessage());
    }

    @Test
    public void getProfileWardrobes_ShouldReturnWardrobes() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(wardrobeRepository.findAllByProfileId(1L)).thenReturn(List.of(wardrobe));

        WardrobeResponseDto mockResponseDto = new WardrobeResponseDto();
        mockResponseDto.setName("Casual Wardrobe");
        when(dtoConversionService.convertToWardrobeResponseDto(any(Wardrobe.class)))
                .thenReturn(mockResponseDto);

        List<WardrobeResponseDto> responseDtos = wardrobeService.getProfileWardrobes(1L);

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals("Casual Wardrobe", responseDtos.getFirst().getName());
    }

    @Test
    public void getUserWardrobes_ThrowsException_WhenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> wardrobeService.getProfileWardrobes(1L));

        assertEquals("Profile not found with ID: 1", exception.getMessage());
    }

    @Test
    public void updateWardrobe_ShouldUpdateWardrobe() {
        WardrobeDto wardrobeDto = new WardrobeDto();
        wardrobeDto.setName("Updated Wardrobe");



        when(wardrobeRepository.findById(1L)).thenReturn(Optional.of(wardrobe));
        when(wardrobeRepository.save(any(Wardrobe.class))).thenReturn(wardrobe);

        WardrobeResponseDto mockResponseDto = new WardrobeResponseDto();
        mockResponseDto.setName("Updated Wardrobe");
        when(dtoConversionService.convertToWardrobeResponseDto(any(Wardrobe.class)))
                .thenReturn(mockResponseDto);

        WardrobeResponseDto responseDto = wardrobeService.updateWardrobe(1L, wardrobeDto);

        assertNotNull(responseDto);
        assertEquals("Updated Wardrobe", responseDto.getName());
    }

    @Test
    public void updateWardrobe_ThrowsException_WhenWardrobeNotFound() {
        WardrobeDto wardrobeDto = new WardrobeDto();
        wardrobeDto.setName("Updated Wardrobe");

        when(wardrobeRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> wardrobeService.updateWardrobe(1L, wardrobeDto));

        assertEquals("Wardrobe not found with ID: 1", exception.getMessage());
    }

    @Test
    public void deleteWardrobe_ShouldDeleteWardrobe() {
        when(wardrobeRepository.existsById(1L)).thenReturn(true);

        boolean result = wardrobeService.deleteWardrobe(1L);

        assertTrue(result);
        verify(wardrobeRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteWardrobe_ThrowsException_WhenWardrobeNotFound() {
        when(wardrobeRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> wardrobeService.deleteWardrobe(1L));

        assertEquals("Wardrobe not found with ID: 1", exception.getMessage());
    }
}
