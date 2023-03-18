package com.openvelog.openvelogbe.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.openvelog.openvelogbe.blog.dto.BlogResponseDto;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MemberResponseDto {
    @Schema(type = "integer", example = "2")
    private Long id;

    @Schema(example = "userId")
    private String userId;

    @Schema(example = "apple123")
    private String username;

    @Schema(example = "user@gmail.com")
    private String email;

    @Schema(example = "user@gmail.com")
    private Gender gender;

    @Schema(example = "user@gmail.com")
    private LocalDate birthday;

    private Long blogId;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .userId(member.getUserId())
                .email(member.getEmail())
                .gender(member.getGender())
                .birthday(member.getBirthday())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }

    public static MemberResponseDto ofHasBlog(Member member, Long blogId) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .userId(member.getUserId())
                .email(member.getEmail())
                .blogId(blogId)
                .gender(member.getGender())
                .birthday(member.getBirthday())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }
}
