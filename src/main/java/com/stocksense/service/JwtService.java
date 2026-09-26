package com.stocksense.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.stocksense.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs = 7 * 24 * 60 * 60 * 1000L; // 7 days

    public JwtService(@Value("${stocksense.jwt.secret:stocksense-hackathon-super-secret-jwt-key-2026}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer("StockSense").build();
    }

    public String generateToken(String email, Long userId) {
        return JWT.create()
                .withIssuer("StockSense")
                .withSubject(email.toLowerCase())
                .withClaim("userId", userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .sign(algorithm);
    }

    public String validateTokenAndGetEmail(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Authentication token is missing.");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException e) {
            throw new UnauthorizedException("Invalid or expired authentication token.");
        }
    }
}
