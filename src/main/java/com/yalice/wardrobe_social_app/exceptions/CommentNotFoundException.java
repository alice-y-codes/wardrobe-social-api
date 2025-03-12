package com.yalice.wardrobe_social_app.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}