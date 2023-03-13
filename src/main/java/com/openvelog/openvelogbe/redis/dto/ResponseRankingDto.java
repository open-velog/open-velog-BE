package com.openvelog.openvelogbe.redis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.redis.core.ZSetOperations;

@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRankingDto {
    @Schema(type = "String", example = "제목")
    private String title;
    @Schema(type = "integer", example = "2")
    private int score;

    public static ResponseRankingDto convertToResponseRankingDto(ZSetOperations.TypedTuple typedTuple){
        ResponseRankingDtoBuilder builder = builder()
                .title(typedTuple.getValue().toString())
                .score(typedTuple.getScore().intValue());
        return builder.build();
    }
}
