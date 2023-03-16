package com.openvelog.openvelogbe.dummy;

import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import com.openvelog.openvelogbe.util.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Lazy
public class MemberDummyGenerator extends DummyGenerator<Member, MemberRepository> {
    private final Gender[] values = Gender.values();

    private final LocalDate lowerBoundBirthday = LocalDate.of(1920, 1, 1);

    private final LocalDate upperBoundBirthday = LocalDate.of(2023, 3, 16);

    private RandomLocalDateGenerator localDateDummyGenerator;

    @Autowired
    MemberDummyGenerator(
            MemberRepository repository,
            RandomLocalDateGenerator localDateDummyGenerator
    ) {
        super(repository);
        this.localDateDummyGenerator = localDateDummyGenerator;
    }

    @Override
    public Member generateDummyEntityOfThis() {
        Member dummyMember = Member.builder()
                .userId(RandomStringGenerator.generateMemberId())
                .username(RandomStringGenerator.generateMemberName())
                .password(RandomStringGenerator.generateMemberPassword())
                .email(RandomStringGenerator.generateMemberEmail())
                .gender(values[random.nextInt(values.length)])
                .birthday(localDateDummyGenerator.generateRandomLocalDateFromTo(lowerBoundBirthday, upperBoundBirthday))
                .build();

        return dummyMember;
    }
}
