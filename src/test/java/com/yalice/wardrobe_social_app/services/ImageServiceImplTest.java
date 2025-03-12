package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.exceptions.ImageProcessingException;
import com.yalice.wardrobe_social_app.services.core.ImageServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ImageServiceImplTest {

    @Autowired
    private ImageServiceImpl imageService;

    @Value("${app.upload.dir:uploads-test}")
    private String uploadDir;

    private Path testUploadPath;

    @BeforeEach
    void setUp() throws IOException {
        testUploadPath = Paths.get(uploadDir);
        Files.createDirectories(testUploadPath);
    }

    @AfterEach
    void cleanup() throws IOException {
        FileSystemUtils.deleteRecursively(testUploadPath);
    }

    @Test
    void uploadImage_ValidImage_Success() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // Act
        String imageUrl = imageService.uploadImage(file, "test", 1L);

        // Assert
        assertTrue(imageUrl.startsWith("/api/images/test/1/"));
        assertTrue(imageUrl.endsWith(".jpg"));
        assertTrue(Files.exists(Paths.get(uploadDir, "test", "1", imageUrl.substring(imageUrl.lastIndexOf("/") + 1))));
    }

    @Test
    void uploadImage_InvalidType_ThrowsException() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "test content".getBytes());

        // Act & Assert
        assertThrows(ImageProcessingException.class, () -> {
            imageService.uploadImage(file, "test", 1L);
        });
    }

    @Test
    void deleteImage_ExistingImage_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());
        String imageUrl = imageService.uploadImage(file, "test", 1L);
        Path imagePath = Paths.get(uploadDir, "test", "1", imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
        assertTrue(Files.exists(imagePath));

        // Act
        imageService.deleteImage(imageUrl);

        // Assert
        assertFalse(Files.exists(imagePath));
        assertFalse(Files.exists(imagePath.getParent())); // Parent directory should be deleted if empty
    }

    @Test
    void validateImage_ValidImage_ReturnsTrue() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "test.jpg",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());

        // Act & Assert
        assertTrue(imageService.validateImage(file));
    }

    @Test
    void validateImage_InvalidType_ReturnsFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "test.txt",
                "test.txt",
                "text/plain",
                "test content".getBytes());

        // Act & Assert
        assertFalse(imageService.validateImage(file));
    }

    @Test
    void validateImage_NullFile_ReturnsFalse() {
        // Act & Assert
        assertFalse(imageService.validateImage(null));
    }

    @Test
    void validateImage_EmptyFile_ReturnsFalse() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "empty.jpg",
                "empty.jpg",
                "image/jpeg",
                new byte[0]);

        // Act & Assert
        assertFalse(imageService.validateImage(file));
    }
}