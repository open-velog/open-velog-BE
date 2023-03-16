package com.openvelog.openvelogbe.dummy;

import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Component
@Lazy
public class BlogDummyGenerator extends DummyGenerator<Blog, BlogRepository> {
    private MemberRepository memberRepository;

    @Autowired
    BlogDummyGenerator(
            BlogRepository repository,
            MemberRepository memberRepository
    ) {
        super(repository);
        this.memberRepository = memberRepository;
    }

    @Override
    public Blog generateDummyEntityOfThis() {
        List<Member> members = memberRepository.findAll();
        if (members.size() == 0) {
            throw new EntityNotFoundException("그 어떠한 member dummy 데이터도 없습니다.");
        }

        Member randomlySelectedMember = members.get(random.nextInt(members.size()));

        Blog dummyBlog = Blog.builder()
                .introduce("회원 " + randomlySelectedMember.getUserId() + "의 한 줄 소개글입니다.")
                .member(randomlySelectedMember)
                .build();

        return dummyBlog;
    }
}
