package com.openvelog.openvelogbe.openSearch.dto;


import com.openvelog.openvelogbe.openSearch.entity.BoardTestDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardTestDocumentDto {
    @Schema(type = "integer", example = "게시글 아이디")
    private Long id;
    @Schema(example = "게시글 제목")
    private String title;
    @Schema(example = "게시글 내용")
    private String content;
    @Schema(example = "생성 날짜")
    private LocalDateTime createdAt;
    @Schema(example = "수정 날짜")
    private LocalDateTime modifiedAt;
    @Schema(example = "게시글 조회 수")
    private Long viewCount;
    @Schema(example = "게시글 좋아요 수")
    private Integer wishCount;



    public static BoardTestDocumentDto of(BoardTestDocument boardTestDocument) {
        BoardTestDocumentDtoBuilder builder = BoardTestDocumentDto.builder()
                .id(boardTestDocument.getId())
                .title(boardTestDocument.getTitle())
                .content(boardTestDocument.getContent())
                .createdAt(boardTestDocument.getCreated_at())
                .modifiedAt(boardTestDocument.getModified_at())
                .viewCount(boardTestDocument.getView_count())
                .wishCount(boardTestDocument.getWish_count());

        return builder.build();
    }


}
