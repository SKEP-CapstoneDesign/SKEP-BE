package com.example.myapi.security;

import com.example.myapi.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtUtil.resolveToken(request);
            String bearerToken = request.getHeader("Authorization");
            System.out.println("[DEBUG] Authorization 헤더 원본: " + bearerToken);
            System.out.println("[DEBUG] 요청 URI: " + request.getRequestURI());
            System.out.println("[DEBUG] 추출된 토큰: " + token);

            if (token != null && jwtUtil.isTokenValid(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                System.out.println("[DEBUG] 토큰 유효함. userId = " + userId);

                UserDetailsImpl userDetails = new UserDetailsImpl(jwtUtil.getUserFromUserId(userId));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("[DEBUG] SecurityContext에 인증 객체 설정 완료");
            } else {
                System.out.println("[DEBUG] 토큰이 없거나 유효하지 않음");
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            System.out.println("[DEBUG] 예외 발생: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 또는 SC_UNAUTHORIZED
            response.setContentType("application/json;charset=UTF-8");

            ErrorResponse errorResponse = new ErrorResponse("Forbidden", "유효하지 않은 토큰입니다.");
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(errorResponse);

            response.getWriter().write(json);
        }
    }

}
