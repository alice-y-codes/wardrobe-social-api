package com.yalice.wardrobe_social_app.exceptions;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String message) {
        super(message);
    }
}
