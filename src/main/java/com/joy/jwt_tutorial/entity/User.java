package com.joy.jwt_tutorial.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.Set;

@Entity //데이터베이스의 테이블과 1:1매핑되는 객체
@Table(name = "`user`") //table명 user로 지정
@Getter
@Setter
@Builder
@AllArgsConstructor //모든 속성을 받을 수 있는 user를 만들어주는 생성자(lombok에서 해주는 어노테이션)
@NoArgsConstructor //비어있는 기본 생성자를 만들어줌(lombok에서 해주는 어노테이션)
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "activated")
    private boolean activated;

    @ManyToMany //다대다 관계를 JoinTable로 정의함(User와 Authority사이에 매핑테이블 자동 생성)
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;
}