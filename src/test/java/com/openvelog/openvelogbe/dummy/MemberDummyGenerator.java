package com.openvelog.openvelogbe.dummy;

import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import com.openvelog.openvelogbe.util.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
public class MemberDummyGenerator extends DummyGenerator<Member, MemberRepository> {
    private final Gender[] values = Gender.values();

    private final LocalDate lowerBoundBirthday = LocalDate.of(1920, 1, 1);

    private final LocalDate upperBoundBirthday = LocalDate.of(2023, 3, 16);

    private RandomLocalDateGenerator localDateDummyGenerator;

    private BlogRepository blogRepository;

    @Autowired
    MemberDummyGenerator(
            MemberRepository repository,
            RandomLocalDateGenerator localDateDummyGenerator,
            BlogRepository blogRepository
    ) {
        super(repository);
        this.localDateDummyGenerator = localDateDummyGenerator;
        this.blogRepository = blogRepository;
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

    @Override
    public boolean insertDummiesIntoDatabase(int dummyCount) {
        int memberAndBlogTargetCount = 9000;
        int memberAndBlogBatchSize = 100;
        int memberAndBlogInsertedCount = 0;

        while (memberAndBlogInsertedCount < memberAndBlogTargetCount) {
            List<Member> batchMembers = new ArrayList<>(memberAndBlogBatchSize);
            for (int i = 0; i < memberAndBlogBatchSize; ++i) {
                batchMembers.add(generateDummyEntityOfThis());
            }

            try {
                this.repository.saveAll(batchMembers);
            } catch (Exception e) {
                System.out.println("Exception occurs saving batch members");
                System.out.println("Retrying...");
                continue;
            }

            List<Blog> batchBlogs = new ArrayList<>(memberAndBlogBatchSize);
            for (int i = 0; i < batchMembers.size(); ++i) {
                Blog dummyBlog = Blog.builder()
                        .introduce("회원 " + batchMembers.get(i).getUserId() + "의 한 줄 소개글입니다.")
                        .member(batchMembers.get(i))
                        .build();
                batchBlogs.add(dummyBlog);
            }

            this.blogRepository.saveAll(batchBlogs);

            memberAndBlogInsertedCount += memberAndBlogBatchSize;
            System.out.println(memberAndBlogInsertedCount + "만큼의 사용자와 블로그 데이터가 삽입되었습니다.");
        }

        return true;
    }
}
