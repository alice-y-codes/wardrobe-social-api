package com.yalice.wardrobe_social_app.controllers.utilities;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

public class AuthTestUtils {


    /**
     * Sets up authentication for tests by creating a mock authentication context.
     *
     * @param username The username of the authenticated user.
     */
    public static void setupAuthentication(String username) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, null, new ArrayList<>());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}