package com.openvelog.openvelogbe.common.entity;

import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.util.GetAgeRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "search_logs")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchLog extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String keyword;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private AgeRange ageRange;
    private LocalDateTime searchDateTime;

    public static SearchLog create(String keyword, Member member) {
        SearchLogBuilder builder = SearchLog
                .builder()
                .keyword(keyword)
                .searchDateTime(LocalDateTime.now());

        if( member != null) {
            builder
                    .gender(member.getGender())
                    .ageRange(GetAgeRange.getAge(member));
        }

        return builder.build();
    }
}