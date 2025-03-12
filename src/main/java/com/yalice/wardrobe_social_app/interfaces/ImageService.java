package com.yalice.wardrobe_social_app.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    /**
     * Uploads an image file and returns the URL where it can be accessed
     *
     * @param file       The image file to upload
     * @param entityType The type of entity (e.g., "item", "outfit", "post")
     * @param entityId   The ID of the entity the image belongs to
     * @return The URL where the image can be accessed
     */
    String uploadImage(MultipartFile file, String entityType, Long entityId);

    /**
     * Deletes an image by its URL
     *
     * @param imageUrl The URL of the image to delete
     */
    void deleteImage(String imageUrl);

    /**
     * Validates an image file
     *
     * @param file The image file to validate
     * @return true if the image is valid, false otherwise
     */
    boolean validateImage(MultipartFile file);
}