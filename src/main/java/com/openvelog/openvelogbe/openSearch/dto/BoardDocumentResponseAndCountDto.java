package com.openvelog.openvelogbe.openSearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDocumentResponseAndCountDto {

    @Builder.Default
    List<BoardDocumentDto> content = new ArrayList<>();
    @Builder.Default
    Integer totalPages = 0;
    @Builder.Default
    Integer pageNumber = 0;
    @Builder.Default
    Integer totalElements = 0;
    @Builder.Default
    Integer numberOfElements = 0;
    @Builder.Default
    Boolean last = false;

    public static BoardDocumentResponseAndCountDto of(List<BoardDocumentDto> content, Integer currentPage, Integer perPage, Integer totalCount) {
        BoardDocumentResponseAndCountDtoBuilder builder = builder()
                .content(content)
                .totalElements(totalCount)
                .pageNumber(currentPage)
                .numberOfElements(totalCount);

        if (totalCount == 0) {
            return builder.totalPages(0).last(true).build();
        }

        Integer page = (totalCount / perPage) + 1;

        builder.totalPages(page).last(page.equals(currentPage - 1));

        return builder.build();
    }
}
