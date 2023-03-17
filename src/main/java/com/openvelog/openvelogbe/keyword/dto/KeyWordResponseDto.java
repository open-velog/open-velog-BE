package com.openvelog.openvelogbe.keyword.dto;

import com.openvelog.openvelogbe.common.entity.Keyword;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;


@Data
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyWordResponseDto {
    @Schema(type = "String", example = "검색한 단어")
    private String keyword;
    @Schema(type = "Long", example = "멤버의 Id")
    private Long memberId;
    @Schema(type = "Gender", example = "성별")
    private Gender gender;
    @Schema(type = "AgeRange", example = "연령대")
    private AgeRange ageRange;
    @Schema(type = "LocalDate", example = "검색된 시점 연,월,일")
    private LocalDate createdAt;

    public static KeyWordResponseDto of (Keyword keyword){
        KeyWordResponseDtoBuilder builder = builder()
                .keyword(keyword.getKeyword())
                .gender(keyword.getGender())
                .memberId(keyword.getMemberId())
                .ageRange(keyword.getAgeRange())
                .createdAt(LocalDate.now());
        return builder.build();
    }
}
