package com.joy.jwt_tutorial.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

//유효한 자격증명을 제공하지않고 접근하려 할때 401에러를 리턴할 JwtAuthenticationEntryPoint 클래스
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401 SC_UNAUTHORIZED에러 SEND
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}