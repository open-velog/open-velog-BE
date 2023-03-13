package com.openvelog.openvelogbe.rank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RankResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RankKeyword {
        String keyword;
        Long count;

        public static RankKeyword of(String keyword, Long count) {
            return builder().count(count).keyword(keyword).build();
        }
    }
}
