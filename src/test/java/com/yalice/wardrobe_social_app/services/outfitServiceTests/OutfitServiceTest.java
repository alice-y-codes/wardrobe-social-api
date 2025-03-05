//package com.yalice.wardrobe_social_app.services.outfitServiceTests;
//
//import com.yalice.wardrobe_social_app.entities.Item;
//import com.yalice.wardrobe_social_app.entities.Outfit;
//import com.yalice.wardrobe_social_app.entities.User;
//import com.yalice.wardrobe_social_app.interfaces.ItemService;
//import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
//import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
//import com.yalice.wardrobe_social_app.repositories.UserRepository;
//import com.yalice.wardrobe_social_app.services.OutfitServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class OutfitServiceTest {
//
//    @Mock
//    private OutfitRepository outfitRepository;
//
//    @Mock
//    private UserSearchService userSearchService;
//
//    @Mock
//    private ItemService itemService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private OutfitServiceImpl outfitService;
//
//    private User testUser;
//    private Outfit testOutfit;
//    private Item testItem;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        testUser = User.builder()
//                .id(1L)
//                .username("testuser")
//                .email("test@example.com")
//                .build();
//
//        testItem = Item.builder()
//                .id(1L)
//                .name("Test Item")
//                .category("Tops")
//                .user(testUser)
//                .imageUrl("http://example.com/image.jpg")
//                .build();
//
//        Set<Item> items = new HashSet<>();
//        items.add(testItem);
//
//        testOutfit = Outfit.builder()
//                .id(1L)
//                .name("Test Outfit")
//                .description("A test outfit")
//                .occasion("Casual")
//                .user(testUser)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .items(items)
//                .build();
//    }
//
//    @Test
//    void addItemToOutfit_Success() {
//        Item itemToAdd = new Item();
//        itemToAdd.setId(2L);
//        itemToAdd.setName("New Item");
//        itemToAdd.setCategory("Bottoms");
//        itemToAdd.setUser(testUser);
//
//        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
//        when(itemService.getItem(2L)).thenReturn(itemToAdd);
//        when(outfitRepository.save(any(Outfit.class))).thenReturn(testOutfit);
//
//        Outfit result = outfitService.addItemToOutfit(1L, 2L);
//
//        assertNotNull(result);
//        verify(outfitRepository).save(any(Outfit.class));
//    }
//
//    @Test
//    void addItemToOutfit_ItemNotFound() {
//        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
//        doThrow(new RuntimeException("Item not found"))
//                .when(itemService).getItem(99L);
//
//        assertThrows(RuntimeException.class, () -> outfitService.addItemToOutfit(1L, 99L));
//        verify(outfitRepository, never()).save(any(Outfit.class));
//    }
//
//    @Test
//    void removeItemFromOutfit_Success() {
//        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
//        when(itemService.getItem(1L)).thenReturn(testItem);
//        when(outfitRepository.save(any(Outfit.class))).thenReturn(testOutfit);
//
//        Outfit result = outfitService.removeItemFromOutfit(1L, 1L);
//
//        assertNotNull(result);
//        verify(outfitRepository).save(any(Outfit.class));
//    }
//
//    @Test
//    void removeItemFromOutfit_ItemNotFound() {
//        when(outfitRepository.findById(1L)).thenReturn(Optional.of(testOutfit));
//        doThrow(new RuntimeException("Item not found"))
//                .when(itemService).getItem(99L);
//
//        assertThrows(RuntimeException.class, () -> outfitService.removeItemFromOutfit(1L, 99L));
//        verify(outfitRepository, never()).save(any(Outfit.class));
//    }
//}
