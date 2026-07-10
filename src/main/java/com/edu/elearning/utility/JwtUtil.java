package com.edu.elearning.utility;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = System.getenv().getOrDefault(
            "JWT_SECRET", "dev-only-secret-do-not-use-in-production-change-me");
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final long ACCESS_TOKEN_EXPIRY = 1000 * 60 * 20;

    private static final long REFRESH_TOKEN_EXPIRY = 1000 * 60 * 60 * 24;

    public static String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }


    public static String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }


    public static boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public static boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
