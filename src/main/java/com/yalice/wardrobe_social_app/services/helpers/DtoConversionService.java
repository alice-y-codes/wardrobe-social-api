package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DtoConversionService {

    // User Conversion
    public UserResponseDto convertToUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    // Item Conversion
    public ItemResponseDto convertToItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .brand(item.getBrand())
                .category(item.getCategory())
                .size(item.getSize())
                .color(item.getColor())
                .imageUrl(item.getImageUrl())
                .wardrobeId(item.getWardrobe().getId())
                .profileId(item.getProfile().getId())
                .build();
    }

    // Wardrobe Conversion
    public WardrobeResponseDto convertToWardrobeResponseDto(Wardrobe wardrobe) {
        return WardrobeResponseDto.builder()
                .id(wardrobe.getId())
                .name(wardrobe.getName())
                .profileId(wardrobe.getProfile().getId())
                .build();
    }

    // Outfit Conversion
    public OutfitResponseDto convertToOutfitResponseDto(Outfit outfit) {
        Set<ItemResponseDto> itemResponseDtos = outfit.getItems().stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toSet());

        return OutfitResponseDto.builder()
                .id(outfit.getId())
                .name(outfit.getName())
                .description(outfit.getDescription())
                .season(outfit.getSeason())
                .isFavorite(outfit.isFavorite())
                .isPublic(outfit.isPublic())
                .createdAt(outfit.getCreatedAt())
                .updatedAt(outfit.getUpdatedAt())
                .items(itemResponseDtos)
                .profileId(outfit.getProfile().getId())
                .build();
    }

    // Post Conversion
    public PostResponseDto convertToPostResponseDto(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .featureImage(post.getFeatureImage())
                .content(post.getContent())
                .outfit(post.getOutfit())
                .visibility(post.getVisibility().name())
                .username(post.getProfile().getUser().getUsername())
                .build();
    }

    // Feed Item Conversion
    public FeedItemResponseDto convertToFeedItemResponseDto(Post post) {
        return FeedItemResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .season(post.getOutfit().getSeason())
                .category(post.getOutfit().getCategory())
                .likesCount(post.getLikes().size())
                .commentsCount(post.getComments().size())
                .featureImage(post.getFeatureImage())
                .outfitImage(post.getOutfit().getImageUrl())
                .itemImages(getItemImages(post))
                .user(convertToUserResponseDto(post.getProfile().getUser()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private Set<String> getItemImages(Post post) {
        return post.getOutfit().getItems().stream()
                .map(Item::getImageUrl)
                .collect(Collectors.toSet());
    }

    public ProfileResponseDto convertToProfileResponseDto(Profile profile) {
        return ProfileResponseDto.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .username(profile.getUser().getUsername())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .stylePreferences(profile.getStylePreferences())
                .favoriteBrands(profile.getFavoriteBrands())
                .fashionInspirations(profile.getFashionInspirations())
                .profileImageUrl(profile.getProfileImageUrl())
                .isPublic(profile.getVisibility() == Profile.ProfileVisibility.PUBLIC)
                .build();
    }

    // Friendship Conversion
    public FriendResponseDto convertToFriendshipResponseDto(Friendship friendship) {
        if (friendship.getSender() == null || friendship.getRecipient() == null) {
            throw new IllegalStateException("Friendship is missing sender or recipient.");
        }

        return FriendResponseDto.builder()
                .id(friendship.getId())
                .userId(friendship.getSender().getId().equals(friendship.getRecipient().getId())
                        ? friendship.getRecipient().getId()
                        : friendship.getSender().getId())
                .username(friendship.getSender().getId().equals(friendship.getRecipient().getId())
                        ? friendship.getRecipient().getUsername()
                        : friendship.getSender().getUsername())
                .status(friendship.getStatus().name())
                .build();
    }

    // Comment Conversion
    public CommentResponseDto convertToCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .userId(comment.getProfile().getUser().getId())
                .username(comment.getProfile().getUser().getUsername())
                .postId(comment.getPost().getId())
                .build();
    }
}
