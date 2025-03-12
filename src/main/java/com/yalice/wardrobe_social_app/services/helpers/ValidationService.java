package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.UnauthorizedAccessException;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public void validateOwnership(Profile resourceOwner, Long requestingProfileId, String resourceType) {
        if (!resourceOwner.getId().equals(requestingProfileId)) {
            throw new UnauthorizedAccessException(
                    String.format("Profile %d does not own this %s", requestingProfileId, resourceType));
        }
    }

    public void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    public void validateStringNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public void validatePositive(Long value, String fieldName) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive number");
        }
    }

    public void validateExists(boolean exists, String message) {
        if (!exists) {
            throw new ResourceNotFoundException(message);
        }
    }
}