//package com.yalice.wardrobe_social_app.services.helpers;
//
//import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
//import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
//import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
//import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
//import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
//import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
//import com.yalice.wardrobe_social_app.entities.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class BaseServiceTest {
//
//    @InjectMocks
//    private BaseService baseService;
//
//    @Mock
//    private User user;
//
//    @Mock
//    private Item item;
//
//    @Mock
//    private Wardrobe wardrobe;
//
//    @Mock
//    private Profile profile;
//
//    @Mock
//    private Outfit outfit;
//
//    @Mock
//    private Post post;
//
//    @Mock
//    private Set<Item> items;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Mock the basic entities using builders
//        user = User.builder()
//                .id(1L)
//                .username("testuser")
//                .email("testuser@example.com")
//                .build();
//
//        profile = Profile.builder()
//                .id(1L)
//                .user(user)
//                .build();
//
//        wardrobe = Wardrobe.builder()
//                .id(1L)
//                .name("Test Wardrobe")
//                .profile(profile)
//                .build();
//
//        item = Item.builder()
//                .id(1L)
//                .name("Test Item")
//                .brand("Test Brand")
//                .category("Category")
//                .size("M")
//                .color("Red")
//                .imageUrl("https://example.com/item.jpg")
//                .wardrobe(wardrobe)
//                .profile(profile)
//                .build();
//
//        items = new HashSet<>();
//        items.add(item);
//
//        outfit = Outfit.builder()
//                .id(1L)
//                .name("Test Outfit")
//                .description("Description of test outfit")
//                .season("Summer")
//                .favorite(true)
//                .items(items)
//                .profile(profile)
//                .build();
//
//        post = Post.builder()
//                .id(1L)
//                .title("Test Post")
//                .content("Content of test post")
//                .featureImage("https://example.com/post.jpg")
//                .outfit(outfit)
//                .profile(profile)
//                .build();
//    }
//
//    @Test
//    void testConvertToUserResponseDto() {
//        UserResponseDto userResponseDto = baseService.convertToUserResponseDto(user);
//
//        assertNotNull(userResponseDto);
//        assertEquals(user.getId(), userResponseDto.getId());
//        assertEquals(user.getUsername(), userResponseDto.getUsername());
//        assertEquals(user.getEmail(), userResponseDto.getEmail());
//    }
//
//    @Test
//    void testConvertToItemResponseDto() {
//        ItemResponseDto itemResponseDto = baseService.convertToItemResponseDto(item);
//
//        assertNotNull(itemResponseDto);
//        assertEquals(item.getId(), itemResponseDto.getId());
//        assertEquals(item.getName(), itemResponseDto.getName());
//        assertEquals(item.getBrand(), itemResponseDto.getBrand());
//        assertEquals(item.getCategory(), itemResponseDto.getCategory());
//        assertEquals(item.getSize(), itemResponseDto.getSize());
//        assertEquals(item.getColor(), itemResponseDto.getColor());
//        assertEquals(item.getImageUrl(), itemResponseDto.getImageUrl());
//        assertEquals(item.getWardrobe().getId(), itemResponseDto.getWardrobeId());
//        assertEquals(item.getProfile().getId(), itemResponseDto.getProfileId());
//    }
//
//    @Test
//    void testConvertToWardrobeResponseDto() {
//        WardrobeResponseDto wardrobeResponseDto = baseService.convertToWardrobeResponseDto(wardrobe);
//
//        assertNotNull(wardrobeResponseDto);
//        assertEquals(wardrobe.getId(), wardrobeResponseDto.getId());
//        assertEquals(wardrobe.getName(), wardrobeResponseDto.getName());
//        assertEquals(wardrobe.getProfile().getId(), wardrobeResponseDto.getProfileId());
//    }
//
//    @Test
//    void testConvertToOutfitResponseDto() {
//        OutfitResponseDto outfitResponseDto = baseService.convertToOutfitResponseDto(outfit);
//
//        assertNotNull(outfitResponseDto);
//        assertEquals(outfit.getId(), outfitResponseDto.getId());
//        assertEquals(outfit.getName(), outfitResponseDto.getName());
//        assertEquals(outfit.getDescription(), outfitResponseDto.getDescription());
//        assertEquals(outfit.getSeason(), outfitResponseDto.getSeason());
//        assertTrue(outfitResponseDto.isFavorite());
//        assertTrue(outfitResponseDto.isPublic());
//        assertNotNull(outfitResponseDto.getItems());
//        assertEquals(1, outfitResponseDto.getItems().size());
//        assertEquals(outfit.getProfile().getId(), outfitResponseDto.getProfileId());
//    }
//
//    @Test
//    void testConvertToPostResponseDto() {
//        PostResponseDto postResponseDto = baseService.convertToPostResponseDto(post);
//
//        assertNotNull(postResponseDto);
//        assertEquals(post.getId(), postResponseDto.getId());
//        assertEquals(post.getTitle(), postResponseDto.getTitle());
//        assertEquals(post.getFeatureImage(), postResponseDto.getFeatureImage());
//        assertEquals(post.getContent(), postResponseDto.getContent());
//        assertNotNull(postResponseDto.getOutfit());
//        assertEquals(post.getOutfit().getId(), postResponseDto.getOutfit().getId());
//        assertEquals(post.getProfile().getUser().getUsername(), postResponseDto.getUsername());
//    }
//
//    @Test
//    void testConvertToFeedItemDto() {
//        FeedItemResponseDto feedItemResponseDto = baseService.convertToFeedItemDto(post);
//
//        assertNotNull(feedItemResponseDto);
//        assertEquals(post.getId(), feedItemResponseDto.getId());
//        assertEquals(post.getTitle(), feedItemResponseDto.getTitle());
//        assertEquals(post.getContent(), feedItemResponseDto.getContent());
//        assertEquals(post.getOutfit().getSeason(), feedItemResponseDto.getSeason());
//        assertEquals(post.getOutfit().getCategory(), feedItemResponseDto.getCategory());
//        assertEquals(post.getLikes().size(), feedItemResponseDto.getLikesCount());
//        assertEquals(post.getComments().size(), feedItemResponseDto.getCommentsCount());
//        assertEquals(post.getFeatureImage(), feedItemResponseDto.getFeatureImage());
//        assertEquals(post.getOutfit().getImageUrl(), feedItemResponseDto.getOutfitImage());
//        assertTrue(feedItemResponseDto.getItemImages().contains(item.getImageUrl()));
//        assertEquals(post.getProfile().getUser().getId(), feedItemResponseDto.getUser().getId());
//        assertEquals(post.getCreatedAt(), feedItemResponseDto.getCreatedAt());
//        assertEquals(post.getUpdatedAt(), feedItemResponseDto.getUpdatedAt());
//    }
//}
