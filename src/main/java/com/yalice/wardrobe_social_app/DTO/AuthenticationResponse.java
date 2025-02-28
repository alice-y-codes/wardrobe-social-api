package com.yalice.wardrobe_social_app.DTO;

public class AuthenticationResponse {
    private final String token;
    
    public AuthenticationResponse(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
}
