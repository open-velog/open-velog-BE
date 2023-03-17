package com.openvelog.openvelogbe.keywordRecord.dto;

import com.openvelog.openvelogbe.common.entity.KeywordRecord;
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

public class KeywordRecordResponseDto {
    @Schema(type = "Long", example = "데이터의 Id")
    private Long Id;
    @Schema(type = "String", example = "검색한 단어")
    private String keyword;
    @Schema(type = "Long", example = "중복된 데이터의 수")
    private Long count;
    @Schema(type = "Gender", example = "성별")
    private Gender gender;
    @Schema(type = "AgeRange", example = "연령대")
    private AgeRange ageRange;
    @Schema(type = "LocalDate", example = "검색된 시점 연,월,일")
    private LocalDate searchedDate;


    public static KeywordRecordResponseDto of(KeywordRecord keywordRecord) {
        KeywordRecordResponseDtoBuilder builder = builder()
                .Id(keywordRecord.getId())
                .keyword(keywordRecord.getKeyword())
                .count(keywordRecord.getCount())
                .gender(keywordRecord.getGender())
                .ageRange(keywordRecord.getAgeRange())
                .searchedDate(keywordRecord.getSearchedDate());
        return builder.build();
    }

}
