package com.joy.jwt_tutorial.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

//토큰의 생성, 유효성 검증을 담당할 TokenProvider 클래스

            //0.InitializingBean을 implements해서 afterPropertiesSet() @Override 하는이유
@Component  //1.빈이 생성이 되고
public class TokenProvider implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;

    public TokenProvider(                                   //2.의존성 주입을 받은 다음
            @Value("${jwt.secret}") String secret,          //3.주입받은 class 값을
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);   //4.BASE64 decode 해서
        this.key = Keys.hmacShaKeyFor(keyBytes);            //5.key 변수에 할당
    }

    //Authentication 객체의 권한정보를 이용해서 토큰을 생성하는 createToken 메소드
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream() //권한들
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds); //application.yml에서 설정한 token만료 시간

        return Jwts.builder() //jwt token을 생성해서 return
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    //Token에 담겨있는 정보를 이용해 Authentication 객체를 return하는 메소드
    //1.token을 parameter로 받아서
    //2.token을 이용해서 claims을 만듬
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        //3.claims에서 권한정보들을 빼서
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        //4.권한정보들을 이용해서 User객체를 만듬
        User principal = new User(claims.getSubject(), "", authorities);

        //5.User객체(principal), token, 권한정보(authorities)를 이용해서 Authentication 객체를 return
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    //Token을 parameter로 받아서 Token의 유효성 검사를 함
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);          //1. token을 파싱하고
            return true;    //3-1.문제가 없으면 return true
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {    //2.나오는 Exception들을 catch하고
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;       //3-2.문제가 있으면 return false
    }
}