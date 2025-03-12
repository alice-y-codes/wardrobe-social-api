package com.yalice.wardrobe_social_app.services.core;

import com.yalice.wardrobe_social_app.exceptions.ImageProcessingException;
import com.yalice.wardrobe_social_app.interfaces.ImageService;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ImageServiceImpl extends BaseService implements ImageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.image.max-size:5242880}") // 5MB default
    private long maxFileSize;

    @Value("${app.image.allowed-types:image/jpeg,image/png,image/gif}")
    private String[] allowedTypes;

    private static final String API_IMAGE_PATH = "/api/images/";

    @PostConstruct
    public void init() {
        try {
            createUploadDirectory();
        } catch (IOException e) {
            throw new ImageProcessingException("Could not create upload directory!", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file, String entityType, Long entityId) {
        logger.info("Uploading image for entity type: {} with ID: {}", entityType, entityId);

        validateImageUploadParameters(file, entityType, entityId);
        validateImage(file);

        try {
            Path uploadPath = createEntityDirectory(entityType, entityId);
            String filename = generateUniqueFilename(file);
            Path filePath = uploadPath.resolve(filename);

            saveImage(file, filePath);
            logger.info("Successfully uploaded image: {}", filePath);

            return buildImageUrl(entityType, entityId, filename);
        } catch (IOException e) {
            logger.error("Failed to upload image for entity type: {} with ID: {}", entityType, entityId, e);
            throw new ImageProcessingException("Failed to upload image. Please try again.", e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            logger.warn("Attempted to delete null or empty image URL");
            return;
        }

        try {
            Path filePath = convertUrlToPath(imageUrl);
            deleteImageFile(filePath);
            cleanupEmptyDirectories(filePath);
            logger.info("Successfully deleted image: {}", imageUrl);
        } catch (IOException e) {
            logger.error("Failed to delete image: {}", imageUrl, e);
            throw new ImageProcessingException("Failed to delete image. Please try again.", e);
        }
    }

    @Override
    public boolean validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageProcessingException("Empty file provided");
        }

        validateFileSize(file);
        validateContentType(file);
        return true;
    }

    private void validateImageUploadParameters(MultipartFile file, String entityType, Long entityId) {
        validationService.validateNotNull(file, "File");
        validationService.validateStringNotEmpty(entityType, "Entity type");
        validationService.validateNotNull(entityId, "Entity ID");
        validationService.validatePositive(entityId, "Entity ID");
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new ImageProcessingException(
                    String.format("File size %d exceeds maximum allowed size %d", file.getSize(), maxFileSize));
        }
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || Arrays.stream(allowedTypes).noneMatch(contentType::equals)) {
            throw new ImageProcessingException(
                    String.format("Invalid content type: %s. Allowed types: %s",
                            contentType, String.join(", ", allowedTypes)));
        }
    }

    private void createUploadDirectory() throws IOException {
        Path path = Paths.get(StringUtils.cleanPath(uploadDir));
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private Path createEntityDirectory(String entityType, Long entityId) throws IOException {
        String relativePath = String.format("%s/%s/%d", uploadDir, entityType, entityId);
        Path uploadPath = Paths.get(StringUtils.cleanPath(relativePath));
        Files.createDirectories(uploadPath);
        return uploadPath;
    }

    private String generateUniqueFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        return UUID.randomUUID().toString() + extension;
    }

    private void saveImage(MultipartFile file, Path filePath) throws IOException {
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private String buildImageUrl(String entityType, Long entityId, String filename) {
        return String.format("%s%s/%d/%s", API_IMAGE_PATH, entityType, entityId, filename);
    }

    private Path convertUrlToPath(String imageUrl) {
        String relativePath = imageUrl.replace(API_IMAGE_PATH, uploadDir + "/");
        return Paths.get(StringUtils.cleanPath(relativePath));
    }

    private void deleteImageFile(Path filePath) throws IOException {
        if (!Files.deleteIfExists(filePath)) {
            logger.warn("Image file not found for deletion: {}", filePath);
        }
    }

    private void cleanupEmptyDirectories(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        while (parent != null && !parent.toString().equals(uploadDir)) {
            if (Files.isDirectory(parent) && Files.list(parent).findFirst().isEmpty()) {
                Files.delete(parent);
                parent = parent.getParent();
            } else {
                break;
            }
        }
    }

    @Override
    protected JpaRepository<?, Long> getRepository() {
        return null; // Image service doesn't use a repository
    }

    @Override
    protected String getEntityName() {
        return "Image"; // Used for logging purposes
    }
}