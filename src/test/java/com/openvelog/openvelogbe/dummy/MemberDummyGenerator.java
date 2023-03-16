package com.openvelog.openvelogbe.dummy;

import com.mifmif.common.regex.Generex;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Lazy
public class MemberDummyGenerator extends DummyGenerator<Member, MemberRepository> {
    private final Long DUMMY_COUNT = 1000L;

    private final Generex memberIdGenerex = new Generex("^(?=.*?[0-9])(?=.*?[a-z]).{6,16}$");

    private final Generex memberNameGenerex = new Generex("^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9]{3,10}$");

    private final Generex memberPasswordGenerex = new Generex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{8,}$");

    private final Generex memberEmailGenerex = new Generex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

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
        Member randomMember = Member.builder()
                .userId(memberIdGenerex.random())
                .username(memberNameGenerex.random())
                .password(memberPasswordGenerex.random())
                .email(memberEmailGenerex.random())
                .gender(values[random.nextInt(values.length)])
                .birthday(localDateDummyGenerator.generateRandomLocalDateFromTo(lowerBoundBirthday, upperBoundBirthday))
                .build();

        return randomMember;
    }
}
