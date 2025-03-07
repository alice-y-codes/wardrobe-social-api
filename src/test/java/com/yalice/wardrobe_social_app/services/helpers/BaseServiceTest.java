package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BaseServiceTest {

    @InjectMocks
    private BaseService baseService;

    @Mock
    private User user;

    @Mock
    private Item item;

    @Mock
    private Wardrobe wardrobe;

    @Mock
    private Profile profile;

    @Mock
    private Outfit outfit;

    @Mock
    private Post post;

    @Mock
    private Set<Item> items;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the basic entities
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        profile = new Profile();
        profile.setId(1L);
        profile.setUser(user);

        wardrobe = new Wardrobe();
        wardrobe.setId(1L);
        wardrobe.setName("Test Wardrobe");
        wardrobe.setProfile(profile);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setBrand("Test Brand");
        item.setCategory("Category");
        item.setSize("M");
        item.setColor("Red");
        item.setImageUrl("https://example.com/item.jpg");
        item.setWardrobe(wardrobe);
        item.setProfile(profile);

        items = new HashSet<>();
        items.add(item);

        outfit = new Outfit();
        outfit.setId(1L);
        outfit.setName("Test Outfit");
        outfit.setDescription("Description of test outfit");
        outfit.setSeason("Summer");
        outfit.setFavorite(true);
        outfit.setPublic(true);
        outfit.setItems(items);
        outfit.setProfile(profile);

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Content of test post");
        post.setFeatureImage("https://example.com/post.jpg");
        post.setOutfit(outfit);
        post.setProfile(profile);
    }

    @Test
    void testConvertToUserResponseDto() {
        UserResponseDto userResponseDto = baseService.convertToUserResponseDto(user);

        assertNotNull(userResponseDto);
        assertEquals(user.getId(), userResponseDto.getId());
        assertEquals(user.getUsername(), userResponseDto.getUsername());
        assertEquals(user.getEmail(), userResponseDto.getEmail());
    }

    @Test
    void testConvertToItemResponseDto() {
        ItemResponseDto itemResponseDto = baseService.convertToItemResponseDto(item);

        assertNotNull(itemResponseDto);
        assertEquals(item.getId(), itemResponseDto.getId());
        assertEquals(item.getName(), itemResponseDto.getName());
        assertEquals(item.getBrand(), itemResponseDto.getBrand());
        assertEquals(item.getCategory(), itemResponseDto.getCategory());
        assertEquals(item.getSize(), itemResponseDto.getSize());
        assertEquals(item.getColor(), itemResponseDto.getColor());
        assertEquals(item.getImageUrl(), itemResponseDto.getImageUrl());
        assertEquals(item.getWardrobe().getId(), itemResponseDto.getWardrobeId());
        assertEquals(item.getProfile().getId(), itemResponseDto.getProfileId());
    }

    @Test
    void testConvertToWardrobeResponseDto() {
        WardrobeResponseDto wardrobeResponseDto = baseService.convertToWardrobeResponseDto(wardrobe);

        assertNotNull(wardrobeResponseDto);
        assertEquals(wardrobe.getId(), wardrobeResponseDto.getId());
        assertEquals(wardrobe.getName(), wardrobeResponseDto.getName());
        assertEquals(wardrobe.getProfile().getId(), wardrobeResponseDto.getProfileId());
    }

    @Test
    void testConvertToOutfitResponseDto() {
        OutfitResponseDto outfitResponseDto = baseService.convertToOutfitResponseDto(outfit);

        assertNotNull(outfitResponseDto);
        assertEquals(outfit.getId(), outfitResponseDto.getId());
        assertEquals(outfit.getName(), outfitResponseDto.getName());
        assertEquals(outfit.getDescription(), outfitResponseDto.getDescription());
        assertEquals(outfit.getSeason(), outfitResponseDto.getSeason());
        assertTrue(outfitResponseDto.isFavorite());
        assertTrue(outfitResponseDto.isPublic());
        assertNotNull(outfitResponseDto.getItems());
        assertEquals(1, outfitResponseDto.getItems().size());
        assertEquals(outfit.getProfile().getId(), outfitResponseDto.getProfileId());
    }

    @Test
    void testConvertToPostResponseDto() {
        PostResponseDto postResponseDto = baseService.convertToPostResponseDto(post);

        assertNotNull(postResponseDto);
        assertEquals(post.getId(), postResponseDto.getId());
        assertEquals(post.getTitle(), postResponseDto.getTitle());
        assertEquals(post.getFeatureImage(), postResponseDto.getFeatureImage());
        assertEquals(post.getContent(), postResponseDto.getContent());
        assertNotNull(postResponseDto.getOutfit());
        assertEquals(post.getOutfit().getId(), postResponseDto.getOutfit().getId());
        assertEquals(post.getProfile().getUser().getUsername(), postResponseDto.getUsername());
    }

    @Test
    void testConvertToFeedItemDto() {
        FeedItemDto feedItemDto = baseService.convertToFeedItemDto(post);

        assertNotNull(feedItemDto);
        assertEquals(post.getId(), feedItemDto.getId());
        assertEquals("POST", feedItemDto.getType());
        assertEquals(post.getProfile().getUser().getId(), feedItemDto.getUser().getId());
        assertEquals(post.getCreatedAt(), feedItemDto.getCreatedAt());
        assertEquals(post.getUpdatedAt(), feedItemDto.getUpdatedAt());
        assertEquals(post.getOutfit().getSeason(), feedItemDto.getSeason());
        assertEquals(post.getOutfit().getCategory(), feedItemDto.getCategory());
        assertEquals(post.getLikes().size(), feedItemDto.getLikesCount());
        assertEquals(post.getComments().size(), feedItemDto.getCommentsCount());
        assertFalse(feedItemDto.isLikedByCurrentUser());
    }
}
