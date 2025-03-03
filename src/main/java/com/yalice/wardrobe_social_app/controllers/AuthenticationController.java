package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.AuthenticationRequest;
import com.yalice.wardrobe_social_app.dtos.AuthenticationResponse;
import com.yalice.wardrobe_social_app.services.UserDetailsServiceImpl;
import com.yalice.wardrobe_social_app.utilities.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling authentication-related endpoints.
 * Provides functionality for user login and logout operations.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Handles user login requests.
     * Authenticates the user and generates a JWT token upon successful
     * authentication.
     *
     * @param authenticationRequest Contains the username and password for
     *                              authentication
     * @param response              HTTP response to set the JWT cookie
     * @return ResponseEntity with authentication response or error message
     * @throws Exception If authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest,
            HttpServletResponse response) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());

            final String token = jwtTokenUtil.generateToken(userDetails);

            // Create a cookie with the JWT token
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            // Don't set max age for session cookie
            response.addCookie(jwtCookie);

            return ResponseEntity.ok(new AuthenticationResponse(token, userDetails.getUsername()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    /**
     * Handles user logout requests.
     * Clears the JWT cookie and security context.
     *
     * @param response HTTP response to clear the JWT cookie
     * @return ResponseEntity with success message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear the JWT cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Delete the cookie
        response.addCookie(jwtCookie);

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok("Logged out successfully");
    }
}
