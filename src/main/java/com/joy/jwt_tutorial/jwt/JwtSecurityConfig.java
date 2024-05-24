package com.joy.jwt_tutorial.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//JwtFilter와 TokenProbider을 SecurityConfig에 적용할 때 사용할 JwtSecurityConfig 클래스
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;
    public JwtSecurityConfig(TokenProvider tokenProvider) { //TokenProvider를 주입받아서
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(  //JwtFilter를 통해 Security로직에 Filter를 등록
                new JwtFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}