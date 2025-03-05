package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Item;

public interface DtoConverterService {

    UserResponseDto convertToUserResponseDto(User user);

    ItemResponseDto convertToItemResponseDto(Item item, User user);
}
