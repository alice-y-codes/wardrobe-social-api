//package com.yalice.wardrobe_social_app.services;
//
//import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
//import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
//import com.yalice.wardrobe_social_app.entities.Item;
//import com.yalice.wardrobe_social_app.entities.Outfit;
//import com.yalice.wardrobe_social_app.entities.User;
//import com.yalice.wardrobe_social_app.interfaces.ItemService;
//import com.yalice.wardrobe_social_app.interfaces.OutfitService;
//import com.yalice.wardrobe_social_app.interfaces.UserService;
//import com.yalice.wardrobe_social_app.repositories.OutfitRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class OutfitServiceImpl implements OutfitService {
//
//    private final UserService userService;
//    private final ItemService itemService;
//    private final OutfitRepository outfitRepository;
//
//    @Autowired
//    public OutfitServiceImpl(OutfitRepository outfitRepository, UserService userService, ItemService itemService) {
//        this.outfitRepository = outfitRepository;
//        this.userService = userService;
//        this.itemService = itemService;
//    }
//
//    @Override
//    @Transactional
//    public OutfitResponseDto createOutfit(Long userId, OutfitDto outfitDto) {
//        User user = userService.getUserById(userId);
//        Outfit outfit = new Outfit();
//        outfit.setUser(user);
//        outfit.setName(outfitDto.getName());
//        outfit.setDescription(outfitDto.getDescription());
//        outfit.setSeason(outfitDto.getSeason());
//        outfit.setFavorite(outfitDto.isFavorite());
//        outfit.setPublic(outfitDto.isPublic());
//        Outfit savedOutfit = outfitRepository.save(outfit);
//        return new OutfitResponseDto(savedOutfit);
//    }
//
//    @Override
//    public OutfitResponseDto getOutfit(Long outfitId) {
//        Outfit outfit = outfitRepository.findById(outfitId)
//                .orElseThrow(() -> new RuntimeException("Outfit not found"));
//        return new OutfitResponseDto(outfit);
//    }
//
//    @Override
//    public List<OutfitResponseDto> getAllOutfits(Long userId) {
//        List<Outfit> outfits = outfitRepository.findByUserId(userId);
//        return outfits.stream().map(OutfitResponseDto::new).collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public OutfitResponseDto updateOutfit(Long outfitId, OutfitDto outfitDto) {
//        Outfit outfit = outfitRepository.findById(outfitId)
//                .orElseThrow(() -> new RuntimeException("Outfit not found"));
//        outfit.setName(outfitDto.getName());
//        outfit.setDescription(outfitDto.getDescription());
//        outfit.setSeason(outfitDto.getSeason());
//        outfit.setFavorite(outfitDto.isFavorite());
//        outfit.setPublic(outfitDto.isPublic());
//        Outfit updatedOutfit = outfitRepository.save(outfit);
//        return new OutfitResponseDto(updatedOutfit);
//    }
//
//    @Override
//    @Transactional
//    public void deleteOutfit(Long outfitId) {
//        if (!outfitRepository.existsById(outfitId)) {
//            throw new RuntimeException("Outfit not found");
//        }
//        outfitRepository.deleteById(outfitId);
//    }
//
//    @Override
//    @Transactional
//    public OutfitResponseDto addItemToOutfit(Long outfitId, Long itemId) {
//        Outfit outfit = outfitRepository.findById(outfitId)
//                .orElseThrow(() -> new RuntimeException("Outfit not found"));
//        Item item = itemService.getItemById(itemId);
//        outfit.addItem(item);
//        Outfit updatedOutfit = outfitRepository.save(outfit);
//        return new OutfitResponseDto(updatedOutfit);
//    }
//
//    @Override
//    @Transactional
//    public OutfitResponseDto removeItemFromOutfit(Long outfitId, Long itemId) {
//        Outfit outfit = outfitRepository.findById(outfitId)
//                .orElseThrow(() -> new RuntimeException("Outfit not found"));
//        Item item = itemService.getItemById(itemId);
//        outfit.removeItem(item);
//        Outfit updatedOutfit = outfitRepository.save(outfit);
//        return new OutfitResponseDto(updatedOutfit);
//    }
//}
