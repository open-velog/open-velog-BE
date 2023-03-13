package com.openvelog.openvelogbe.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.member.dto.MemberResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import javax.persistence.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponseDto {

    @Schema(type = "integer", example = "2")
    private Long id;

    @Schema(example = "블로그 타이틀")
    private String title;

    @Schema(example = "블로그 소개글")
    private String introduce;

    @Schema(example = "블로그 주인")
    private MemberResponseDto member;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static BlogResponseDto of(Blog blog) {

        BlogResponseDtoBuilder builder = builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .introduce(blog.getIntroduce())
                .createdAt(blog.getCreatedAt())
                .modifiedAt(blog.getModifiedAt());

        return builder.build();
    }

}

