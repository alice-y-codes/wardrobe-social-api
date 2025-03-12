package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.interfaces.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageHandlerService {
    private final ImageService imageService;

    public ImageHandlerService(ImageService imageService) {
        this.imageService = imageService;
    }

    public String handleImageUpload(MultipartFile image, String entityType, Long entityId, String existingImageUrl) {
        if (image != null && !image.isEmpty()) {
            if (existingImageUrl != null) {
                imageService.deleteImage(existingImageUrl);
            }
            return imageService.uploadImage(image, entityType, entityId);
        }
        return existingImageUrl;
    }

    public void handleImageDelete(String imageUrl) {
        if (imageUrl != null) {
            imageService.deleteImage(imageUrl);
        }
    }
}