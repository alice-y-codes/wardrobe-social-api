package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WardrobeService {
    WardrobeResponseDto createWardrobe(Long profileId, WardrobeDto wardrobeDto);
    List<WardrobeResponseDto> getProfileWardrobes(Long profileId);
    WardrobeResponseDto getWardrobeById(Long wardrobeId);
    WardrobeResponseDto updateWardrobe(Long wardrobeId, WardrobeDto wardrobeDto);
    boolean deleteWardrobe(Long wardrobeId);
}
