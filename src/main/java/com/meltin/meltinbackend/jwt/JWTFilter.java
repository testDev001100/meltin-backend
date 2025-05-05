package com.meltin.meltinbackend.jwt;

import com.meltin.meltinbackend.entity.UserEntity;
import com.meltin.meltinbackend.repository.BlacklistedTokenRepository;
import com.meltin.meltinbackend.repository.UserRepository;
import com.meltin.meltinbackend.service.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);

        System.out.println("토큰: " + token);
        System.out.println("토큰 유효성 검사 결과: " + jwtUtil.validateToken(token));
        System.out.println("SecurityContext 인증 객체: " + SecurityContextHolder.getContext().getAuthentication());

        // 토큰 존재 + 유효한 경우에만 처리
        if (token != null && jwtUtil.validateToken(token)) {

            // 블랙리스트 확인
            if (blacklistedTokenRepository.existsByToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("로그아웃된 토큰입니다.");
                return;
            }

            // 사용자 정보 추출
            String username = jwtUtil.getUsername(token);
            UserEntity userEntity = userRepository.findByUsername(username);

            if (userEntity == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // Spring Security 인증 객체 등록
            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);

            // request에도 사용자 정보 저장 (원하면)
            request.setAttribute("user", userEntity);
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
