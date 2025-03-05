package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.interfaces.DtoConverterService;

public abstract class BaseService implements DtoConverterService {

    @Override
    public UserResponseDto convertToUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }

    @Override
    public ItemResponseDto convertToItemResponseDto(Item item, User user) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .brand(item.getBrand())
                .category(item.getCategory())
                .size(item.getSize())
                .color(item.getColor())
                .imageUrl(item.getImageUrl())
                .userId(user.getId())  // Return userId instead of the whole user object
                .build();
    }
}
