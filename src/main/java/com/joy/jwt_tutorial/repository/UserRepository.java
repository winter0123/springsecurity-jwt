package com.joy.jwt_tutorial.repository;

import com.joy.jwt_tutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //username을 기준으로 user 정보를 가져올때 권한 정보도 같이 가져오게 됨
    @EntityGraph(attributePaths = "authorities") //Eager조회로 연결된 모든 데이터를 가져옴
    Optional<User> findOneWithAuthoritiesByUsername(String username);

    //Eager조회 : 연결된 테이블 전체 데이터를 가져옴(실무에서는 거의 사용안함)
    //Lazy조회 : 사용될때 쿼리가 날아감
}