package com.meltin.meltinbackend.jwt;

import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTUtil {

    private final UserRepository userRepository;
    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret, UserRepository userRepository) {
        System.out.println("JWT secret key = " + secret);
        this.userRepository = userRepository;
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public String getUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    public String extractUsername(String token) {
        return getUsername(token);
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String createJwt(String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public LocalDateTime getExpiration(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public UserEntity getUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = getUsername(token);
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
