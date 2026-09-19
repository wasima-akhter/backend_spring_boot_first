package com.example.backend.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  private final SecretKey secretKey;

  public JwtService() {
    this.secretKey = Keys.hmacShaKeyFor(
        "my-super-secret-key-for-learning-only-123456"
            .getBytes());
  }

  public String generateToken(String email) {

    Date now = new Date();

    Date expiration = new Date(
        now.getTime() + 1000 * 60 * 60);

    return Jwts.builder()
        .subject(email)
        .issuedAt(now)
        .expiration(expiration)
        .signWith(secretKey)
        .compact();
  }
}
