package com.joy.jwt_tutorial.config;

import com.joy.jwt_tutorial.jwt.JwtSecurityConfig;
import com.joy.jwt_tutorial.jwt.JwtAccessDeniedHandler;
import com.joy.jwt_tutorial.jwt.JwtAuthenticationEntryPoint;
import com.joy.jwt_tutorial.jwt.TokenProvider;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig{
    private final TokenProvider tokenProvider;
    //private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    //SecurityConfig는 4가지를 주입받음 tokenProvider/corsFilter/jwtAuthenticationEntryPoint/jwtAccessDeniedHandler
    public SecurityConfig(
            TokenProvider tokenProvider,
            //CorsFilter corsFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        //this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
            .csrf(AbstractHttpConfigurer::disable)

            //.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

            //exceptionHandling 할때 직접 만든 클래스 추가 jwtAccessDeniedHandler/jwtAuthenticationEntryPoint
            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )

            //토큰을 받기위한 api와 회원가입을 위한 api는 둘다 허용(permitAll)
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests //HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정하겠다는 의미
                    .requestMatchers("/api/hello", "/api/authenticate", "/api/signup").permitAll() //"/api/hello"인증없이 접근을 허용하겠다는 의미
                    .anyRequest().authenticated() //나머지 요청들은 모두 인증되어야 한다는 의미
            )

            // 세션을 사용하지 않기 때문에 STATELESS로 설정
            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            //JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig 클래스 적용
            .with(new JwtSecurityConfig(tokenProvider), customizer -> {});

        return http.build();
    }
}
