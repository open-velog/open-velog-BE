package com.openvelog.openvelogbe.common.entity;

import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("keyword")
public class Keyword {
    @Id
    private String id;
    private String keyword;
    private Long memberId;
    private Gender gender;
    private AgeRange ageRange;
    private LocalDate createdAt;

    @Builder
    public Keyword(String keyword, Member member, AgeRange ageRange){
        this.keyword = keyword;
        this.memberId = member.getId();
        this.gender = member.getGender();
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
