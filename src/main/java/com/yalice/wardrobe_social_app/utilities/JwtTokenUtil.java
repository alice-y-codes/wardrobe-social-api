package com.yalice.wardrobe_social_app.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and extraction of claims.
 */
@Component
public class JwtTokenUtil {

    /** Secret key for JWT signing and verification. */
    @Value("${jwt.secret}")
    private String secret;

    /** Token expiration time in milliseconds. */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails The user details to generate the token for
     * @return Generated JWT token
     */
    public String generateToken(final UserDetails userDetails) {
        final Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Creates a JWT token with the given claims and subject.
     *
     * @param claims  The claims to include in the token
     * @param subject The subject (username) of the token
     * @return Generated JWT token
     */
    private String createToken(final Map<String, Object> claims,
            final String subject) {
        final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * Validates a JWT token against user details.
     *
     * @param token       The JWT token to validate
     * @param userDetails The user details to validate against
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(final String token,
            final UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token The JWT token to extract the username from
     * @return The username from the token
     */
    public String getUsernameFromToken(final String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a JWT token.
     *
     * @param token          The JWT token to extract the claim from
     * @param claimsResolver Function to resolve the specific claim
     * @return The extracted claim value
     * @param <T> The type of the claim value
     */
    private <T> T getClaimFromToken(final String token,
            final Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token to extract claims from
     * @return The claims from the token
     */
    private Claims getAllClaimsFromToken(final String token) {
        final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT token to check
     * @return true if the token has expired, false otherwise
     */
    private Boolean isTokenExpired(final String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token The JWT token to extract the expiration date from
     * @return The expiration date from the token
     */
    private Date getExpirationDateFromToken(final String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
}
