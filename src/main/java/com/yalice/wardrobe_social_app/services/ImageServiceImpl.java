package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.exceptions.ImageProcessingException;
import com.yalice.wardrobe_social_app.interfaces.ImageService;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageServiceImpl extends BaseService implements ImageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.image.max-size:5242880}") // 5MB default
    private long maxFileSize;

    @Value("${app.image.allowed-types:image/jpeg,image/png,image/gif}")
    private String[] allowedTypes;

    @Override
    public String uploadImage(MultipartFile file, String entityType, Long entityId) {
        if (!validateImage(file)) {
            throw new ImageProcessingException(
                    "Invalid image file. Please ensure the file is an image (JPEG, PNG, or GIF) and under 5MB.");
        }

        try {
            // Create directories if they don't exist
            String relativePath = String.format("%s/%s/%d", uploadDir, entityType, entityId);
            Path uploadPath = Paths.get(StringUtils.cleanPath(relativePath));
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);

            // Save the file with overwrite if exists
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return the relative URL
            return String.format("/api/images/%s/%d/%s", entityType, entityId, filename);
        } catch (IOException e) {
            logger.error("Failed to upload image", e);
            throw new ImageProcessingException("Failed to upload image. Please try again.", e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // Convert URL to file path
            String relativePath = imageUrl.replace("/api/images/", uploadDir + "/");
            Path filePath = Paths.get(StringUtils.cleanPath(relativePath));

            if (!Files.deleteIfExists(filePath)) {
                logger.warn("Image file not found for deletion: {}", filePath);
            }

            // Try to delete parent directories if empty
            Path parent = filePath.getParent();
            while (parent != null && !parent.toString().equals(uploadDir)) {
                if (Files.isDirectory(parent) && Files.list(parent).findFirst().isEmpty()) {
                    Files.delete(parent);
                    parent = parent.getParent();
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Failed to delete image", e);
            throw new ImageProcessingException("Failed to delete image. Please try again.", e);
        }
    }

    @Override
    public boolean validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("Empty file provided for validation");
            return false;
        }

        // Check file size
        if (file.getSize() > maxFileSize) {
            logger.warn("File size {} exceeds maximum allowed size {}", file.getSize(), maxFileSize);
            return false;
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null) {
            logger.warn("No content type provided for file");
            return false;
        }

        for (String allowedType : allowedTypes) {
            if (contentType.equals(allowedType)) {
                return true;
            }
        }

        logger.warn("Invalid content type: {}", contentType);
        return false;
    }
}