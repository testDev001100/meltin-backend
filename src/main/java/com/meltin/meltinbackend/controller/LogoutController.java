package com.meltin.meltinbackend.controller;

import com.meltin.meltinbackend.entity.BlacklistedToken;
import com.meltin.meltinbackend.jwt.JWTUtil;
import com.meltin.meltinbackend.repository.BlacklistedTokenRepository;
import com.meltin.meltinbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class LogoutController {

    private final JWTUtil jwtUtil;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);

        if (token != null && jwtUtil.validateToken(token)) {
            LocalDateTime expiration = jwtUtil.getExpiration(token);

            BlacklistedToken blacklisted = new BlacklistedToken();
            blacklisted.setToken(token);
            blacklisted.setExpiration(expiration);
            blacklistedTokenRepository.save(blacklisted);

            return ResponseEntity.ok("로그아웃 완료");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 토큰입니다.");

    }
}
