package com.moviescatalog.services;

import com.moviescatalog.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {
    private static final long EXPIRATION_TIME = 86400000;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                   .claim("role", "ROLE_" + user.getRole().name())
                   .subject(user.getEmail())
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                   .signWith(getSigningKey())
                   .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith((SecretKey) this.getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                   .verifyWith((SecretKey) this.getSigningKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
    }



}
