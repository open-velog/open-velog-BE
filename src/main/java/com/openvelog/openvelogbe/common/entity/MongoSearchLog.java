package com.openvelog.openvelogbe.common.entity;

import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.util.GetAgeRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import java.time.LocalDateTime;

@Document("search_logs")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MongoSearchLog extends Timestamped {

    @Id
    private ObjectId id;
    private String keyword;

    private Gender gender;

    private AgeRange ageRange;
    private LocalDateTime searchDateTime;

    public static MongoSearchLog create(String keyword, Member member) {
        MongoSearchLog.MongoSearchLogBuilder builder = MongoSearchLog
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

    public static MongoSearchLog create(String keyword, Gender gender, AgeRange ageRange, LocalDateTime searchDateTime) {
        MongoSearchLog.MongoSearchLogBuilder builder = MongoSearchLog
                .builder()
                .keyword(keyword)
                .searchDateTime(searchDateTime);
        builder
                .gender(gender)
                .ageRange(ageRange);

        return builder.build();
    }
}