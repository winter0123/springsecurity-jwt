package com.joy.jwt_tutorial.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig{
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
//                .csrf(AbstractHttpConfigurer::disable)

//                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling(exceptionHandling -> exceptionHandling
//                        .accessDeniedHandler(jwtAccessDeniedHandler)
//                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                )

                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests //HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정하겠다는 의미
                        .requestMatchers("/api/hello", "/api/authenticate", "/api/signup").permitAll() //"/api/hello"인증없이 접근을 허용하겠다는 의미
                        .anyRequest().authenticated() //나머지 요청들은 모두 인증되어야 한다는 의미
                );

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
//                .sessionManagement(sessionManagement ->
//                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .with(new JwtSecurityConfig(tokenProvider), customizer -> {});
        return http.build();
    }
}
