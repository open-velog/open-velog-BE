package com.openvelog.openvelogbe.common.entity;

import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("keyword")
public class Keyword {
    public static final Long DEFAULT_TTL = 60*60*24L;

    @Id
    private String id;

    @Indexed
    private String keyword;

    private Long memberId;

    private Gender gender;

    private AgeRange ageRange;

    private LocalDate createdAt;

    @TimeToLive
    private Long expiration = DEFAULT_TTL;

    @Builder
    public Keyword(String keyword, Member member, AgeRange ageRange){
        this.keyword = keyword;
        if (member != null) {
            this.memberId = member.getId();
            this.gender = member.getGender();
        }
        this.createdAt=LocalDate.now();
        this.ageRange = ageRange;
    }

}

/*@Getter
class Address {
    private String address;

    public Address(String address) {
        this.address = address;
    }
}*/
