package com.openvelog.openvelogbe.blog.dto;

import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.member.dto.MemberResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Schema(example = "블로그 타이틀")
    private String title;

    @Schema(example = "블로그 소개글")
    private String introduce;

    @Schema(example = "블로그 주인")
    private MemberResponseDto member;

    @Schema(example = "블로그에 달린 게시글 목록")
    private List<BoardResponseDto> boards = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static BlogResponseDto of(Blog blog) {

        BlogResponseDtoBuilder builder = builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .introduce(blog.getIntroduce())
                .createdAt(blog.getCreatedAt())
                .modifiedAt(blog.getModifiedAt());

        if (blog.getBoards() != null) {
            builder.boards(blog.getBoards().stream().map(BoardResponseDto::of).collect(Collectors.toList()));
        }



        return builder.build();
    }

}

