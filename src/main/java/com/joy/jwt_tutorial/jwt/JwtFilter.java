package com.joy.jwt_tutorial.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

//JWT를 위한 커스텀 Filter를 만들기 위한 JwtFilter 클래스
public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";

    //JwtFilter는 TokenProvider를 주입받음
    private TokenProvider tokenProvider;
    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    //실제 Filter 로직
    //토큰의 인증정보를 SecurityContext에 저장하는 여할 수행
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest); //1.Request에서 Token을 받아서 jwt에 저장
        String requestURI = httpServletRequest.getRequestURI();

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {         //2.validateToken에 jwt를 넣어서 token 유효성 검사를 실행
            Authentication authentication = tokenProvider.getAuthentication(jwt);   //3.token이 정상적이면 authentication객체를 받아서
            SecurityContextHolder.getContext().setAuthentication(authentication);   //4.SecurityContextHolder에 set
            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    //Filter를 하기 위해서 request의 Header에서 Token정보를 꺼내오기 위한 resolveToken 메소드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}