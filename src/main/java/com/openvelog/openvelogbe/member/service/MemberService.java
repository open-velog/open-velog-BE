package com.openvelog.openvelogbe.member.service;

import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.jwt.JwtUtil;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.member.dto.LoginRequestDto;
import com.openvelog.openvelogbe.member.dto.MemberResponseDto;
import com.openvelog.openvelogbe.member.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final BlogRepository blogRepository;

    @Transactional
    public MemberResponseDto signup(SignupRequestDto signupRequestDto) {
        String userId = signupRequestDto.getUserId();
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        // userId 중복 확인
        Optional<Member> found = memberRepository.findByUserId(userId);
        if (found.isPresent()) {
            throw new IllegalArgumentException(ErrorMessage.USERID_DUPLICATION.getMessage());
        }

        Member newMember = Member.create(signupRequestDto, encodedPassword);
        memberRepository.save(newMember);
        Blog blog = blogRepository.save(Blog.create(newMember));
        return MemberResponseDto.ofHasBlog(newMember, blog.getId());
    }
    @Transactional(readOnly = true)
    public MemberResponseDto login(LoginRequestDto loginRequestDto, HttpServletResponse response) {

        // 사용자 확인
        Member member = memberRepository.findByUserId(loginRequestDto.getUserId()).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.WRONG_USERID.getMessage())
        );

        /*// 비밀번호 확인
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new BadCredentialsException(ErrorMessage.WRONG_PASSWORD.getMessage());
        }*/

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(member.getUserId()));
        Long blogId = member.getBlog() != null ? member.getBlog().getId() : null;
        return MemberResponseDto.ofHasBlog(member, blogId);
    }

    public Boolean checkUserId(String userId) {
        return memberRepository.findByUserId(userId).isPresent();
    }


    public MemberResponseDto getUserByToken(UserDetailsImpl userDetails) {
        Member member = userDetails.getUser();
        Optional<Blog> blog = blogRepository.findByMemberId(member.getId());
        Long blogId = null;
        if (blog.isPresent()) {
            blogId = blog.get().getId();
        }
        return MemberResponseDto.ofHasBlog(member, blogId);
    }
}
