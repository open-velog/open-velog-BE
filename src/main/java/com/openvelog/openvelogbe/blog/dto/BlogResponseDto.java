package com.openvelog.openvelogbe.blog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.BoardWishMember;
import com.openvelog.openvelogbe.member.dto.MemberResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogResponseDto {

    @Schema(type = "integer", example = "2")
    private Long id;

    @Schema(example = "블로그 소개글")
    private String introduce;

    @Schema(example = "블로그 주인")
    private String memberUserId;

    @Schema(example = "블로그 주인")
    private String memberUsername;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(example = "블로그의 게시글들의 총 조회 수")
    private Long viewCountSum;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(example = "블로그의 게시글들의 총 좋아요 수")
    private Long wishCountSum;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public static BlogResponseDto of(Blog blog, Long viewCountSum, Long wishCountSum) {
        BlogResponseDtoBuilder builder = builder()
                .id(blog.getId())
                .memberUserId(blog.getMember().getUserId())
                .memberUsername(blog.getMember().getUsername())
                .introduce(blog.getIntroduce())
                .viewCountSum(ObjectUtils.defaultIfNull(viewCountSum, 0L))
                .wishCountSum(ObjectUtils.defaultIfNull(wishCountSum, 0L))
                .createdAt(blog.getCreatedAt())
                .modifiedAt(blog.getModifiedAt());

        return builder.build();
    }

    public static BlogResponseDto ofNoBoards(Blog blog) {
        BlogResponseDtoBuilder builder = builder()
                .id(blog.getId())
                .introduce(blog.getIntroduce())
                .memberUserId(blog.getMember().getUserId())
                .createdAt(blog.getCreatedAt())
                .modifiedAt(blog.getModifiedAt());

        return builder.build();
    }

}

