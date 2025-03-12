package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.interfaces.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageHandlerServiceTest {

    @Mock
    private ImageService imageService;

    private ImageHandlerService imageHandlerService;
    private MultipartFile testImage;

    @BeforeEach
    void setUp() {
        imageHandlerService = new ImageHandlerService(imageService);
        testImage = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());
    }

    @Test
    void handleImageUpload_WithNewImage_Success() {
        // Arrange
        String expectedUrl = "new-image-url";
        when(imageService.uploadImage(testImage, "test", 1L)).thenReturn(expectedUrl);

        // Act
        String result = imageHandlerService.handleImageUpload(testImage, "test", 1L, null);

        // Assert
        assertThat(result).isEqualTo(expectedUrl);
        verify(imageService).uploadImage(testImage, "test", 1L);
        verify(imageService, never()).deleteImage(any());
    }

    @Test
    void handleImageUpload_WithExistingImage_DeletesOldImage() {
        // Arrange
        String existingUrl = "old-image-url";
        String expectedUrl = "new-image-url";
        when(imageService.uploadImage(testImage, "test", 1L)).thenReturn(expectedUrl);

        // Act
        String result = imageHandlerService.handleImageUpload(testImage, "test", 1L, existingUrl);

        // Assert
        assertThat(result).isEqualTo(expectedUrl);
        verify(imageService).deleteImage(existingUrl);
        verify(imageService).uploadImage(testImage, "test", 1L);
    }

    @Test
    void handleImageUpload_WithNullImage_ReturnsExistingUrl() {
        // Arrange
        String existingUrl = "existing-image-url";

        // Act
        String result = imageHandlerService.handleImageUpload(null, "test", 1L, existingUrl);

        // Assert
        assertThat(result).isEqualTo(existingUrl);
        verify(imageService, never()).deleteImage(any());
        verify(imageService, never()).uploadImage(any(), any(), any());
    }

    @Test
    void handleImageDelete_WithValidUrl_Success() {
        // Arrange
        String imageUrl = "image-url";

        // Act
        imageHandlerService.handleImageDelete(imageUrl);

        // Assert
        verify(imageService).deleteImage(imageUrl);
    }

    @Test
    void handleImageDelete_WithNullUrl_DoesNothing() {
        // Act
        imageHandlerService.handleImageDelete(null);

        // Assert
        verify(imageService, never()).deleteImage(any());
    }
}