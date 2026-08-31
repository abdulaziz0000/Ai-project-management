package com.project_management.security;

import com.project_management.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Creates the signing key from the Base64 encoded secret.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates JWT for authenticated user.
     */
    public String generateToken(User user) {

        return Jwts.builder()
                .claim("userId", user.getId().toString())
                .claim("organizationId", user.getOrganization().getId().toString())
                .claim("role", user.getRole().name())

                .subject(user.getEmail())

                .issuedAt(new Date())

                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))

                .signWith(getSigningKey())

                .compact();
    }

    /**
     * Parses all claims from JWT.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Generic claim extractor.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Extract email.
     */
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration.
     */
    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract role.
     */
    public String extractRole(String token) {

        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extract organization id.
     */
    public UUID extractOrganizationId(String token) {

        String id = extractClaim(token,
                claims -> claims.get("organizationId", String.class));

        return UUID.fromString(id);
    }

    /**
     * Extract user id.
     */
    public UUID extractUserId(String token) {

        String id = extractClaim(token,
                claims -> claims.get("userId", String.class));

        return UUID.fromString(id);
    }

    /**
     * Checks whether token has expired.
     */
    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates JWT.
     */
    public boolean validateToken(String token, User user) {

        String username = extractUsername(token);

        return username.equals(user.getEmail())
                && !isTokenExpired(token);
    }
}