package com.openvelog.openvelogbe.openSearch.dto;

import com.openvelog.openvelogbe.common.entity.BoardDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardDocumentDto {
    @Schema(type = "integer", example = "게시글 아이디")
    private Long id;
    @Schema(example = "게시글 제목")
    private String title;
    @Schema(example = "게시글 내용")
    private String content;
    @Schema(example = "생성 날짜")
    private LocalDateTime created_at;
    @Schema(example = "수정 날짜")
    private LocalDateTime modified_at;
    @Schema(example = "게시글 조회 수")
    private Long view_count;



    public static BoardDocumentDto of(BoardDocument boardDocument) {
        BoardDocumentDtoBuilder builder = BoardDocumentDto.builder()
                .id(boardDocument.getId())
                .title(boardDocument.getTitle())
                .content(boardDocument.getContent())
                .created_at(boardDocument.getCreated_at())
                .modified_at(boardDocument.getModified_at())
                .view_count(boardDocument.getView_count());
        return builder.build();
    }


}
