package com.yalice.wardrobe_social_app.exceptions;

public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException(String message) {
        super(message);
    }
}
