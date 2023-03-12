package com.openvelog.openvelogbe.common.entity;

import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.member.dto.SignupRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity(name = "members")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends Timestamped {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthday;

    @OneToMany(mappedBy = "member")
    private Set<Blog> blogs = new LinkedHashSet<>();

    public static Member create(SignupRequestDto signupRequestDto, String encodedPassword) {
        return Member.builder()
                .userId(signupRequestDto.getUserId())
                .username(signupRequestDto.getUsername())
                .email(signupRequestDto.getEmail())
                .password(encodedPassword)
                .gender(signupRequestDto.getGender())
                .birthday(signupRequestDto.getBirthday())
                .build();
    }

}
