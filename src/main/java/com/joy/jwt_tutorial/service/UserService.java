package com.joy.jwt_tutorial.service;

import java.util.Collections;
import java.util.Optional;
import com.joy.jwt_tutorial.dto.UserDto;
import com.joy.jwt_tutorial.entity.Authority;
import com.joy.jwt_tutorial.entity.User;
import com.joy.jwt_tutorial.exception.DuplicateMemberException;
import com.joy.jwt_tutorial.exception.NotFoundMemberException;
import com.joy.jwt_tutorial.repository.UserRepository;
import com.joy.jwt_tutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //회원가입 로직
    @Transactional
    public UserDto signup(UserDto userDto) {
        //가입user 체크
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        //권한정보 생성
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        //user정보 생성
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return UserDto.from(userRepository.save(user));
    }

    //어떤 user정보라도 받음
    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String username) {
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(username).orElse(null));
    }

    //현재 SecurityUtil에 저장되어 있는 user정보만 받음
    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                        .orElseThrow(() -> new NotFoundMemberException("Member not found"))
        );
    }
}