    package com.meltin.meltinbackend.jwt;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.meltin.meltinbackend.service.security.CustomUserDetails;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.AuthenticationException;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import java.io.IOException;
    import java.util.Collection;
    import java.util.Iterator;
    import java.util.Map;

    public class LoginFilter extends UsernamePasswordAuthenticationFilter {

        private final AuthenticationManager authenticationManager;
        private final JWTUtil jwtUtil;

        public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
            this.authenticationManager = authenticationManager;
            this.jwtUtil = jwtUtil;

            setFilterProcessesUrl("/api/users/login");
        }

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> jsonRequest = objectMapper.readValue(request.getInputStream(), Map.class);

                String username = jsonRequest.get("username");
                String password = jsonRequest.get("password");

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, password, null);

                return authenticationManager.authenticate(authToken);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication){

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            String username = customUserDetails.getUsername();

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority auth = iterator.next();

            String role = auth.getAuthority();

            String token = jwtUtil.createJwt(username,role,60*60*1000L);

            response.addHeader("Authorization", "Bearer " +token);

        }

        @Override
        protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed){

            response.setStatus(401);
        }
    }
