package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Wardrobe;

public interface DtoConverterService {

    UserResponseDto convertToUserResponseDto(User user);

    ItemResponseDto convertToItemResponseDto(Item item);

    OutfitResponseDto convertToOutfitResponseDto(Outfit outfit);

    WardrobeResponseDto convertToWardrobeResponseDto(Wardrobe wardrobe);
}
