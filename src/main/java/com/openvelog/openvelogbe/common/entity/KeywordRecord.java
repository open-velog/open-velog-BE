package com.openvelog.openvelogbe.common.entity;


import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.entity.enums.ValidEnum;
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
@NoArgsConstructor
@UniqueElements()
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

}
