package com.openvelog.openvelogbe.common.entity;

import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.entity.enums.ValidEnum;
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

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @ValidEnum(enumClass = Gender.class)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthday;

    @OneToOne(mappedBy = "member")
    private Blog blog;

    @OneToMany(mappedBy = "member")
    private Set<BoardWishMember> wishes;

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

    @PreRemove
    private void memberIdSetNullAtBoardWish() {
        this.getWishes().forEach(BoardWishMember::setMemberNull);
    }

}
