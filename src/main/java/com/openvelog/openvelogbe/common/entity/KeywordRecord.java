package com.openvelog.openvelogbe.common.entity;


import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.entity.enums.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "keyword_records")
@Getter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"keyword", "gender", "ageRange", "searchedDate"})
        }
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordRecord extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeRange ageRange;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long count;

    @Column(nullable = false)
    private LocalDate searchedDate;

    public static KeywordRecord create(String keyword, Long count, Gender gender,
                                             AgeRange ageRange, LocalDate createdAt) {
        return KeywordRecord.builder()
                .keyword(keyword)
                .count(count)
                .gender(gender)
                .ageRange(ageRange)
                .searchedDate(createdAt)
                .build();
    }

}
