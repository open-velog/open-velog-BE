package com.openvelog.openvelogbe.memberService;

import com.openvelog.openvelogbe.OpenVelogBeApplication;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.jwt.JwtUtil;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardWishMemberRepository;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.common.security.UserDetailsServiceImpl;
import com.openvelog.openvelogbe.config.TestAppMysqlDatabaseConfig;
import com.openvelog.openvelogbe.member.dto.LoginRequestDto;
import com.openvelog.openvelogbe.member.dto.MemberResponseDto;
import com.openvelog.openvelogbe.member.dto.SignupRequestDto;
import com.openvelog.openvelogbe.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestAppMysqlDatabaseConfig.class, MemberService.class,
        JwtUtil.class, UserDetailsServiceImpl.class, OpenVelogBeApplication.class})
@Transactional(transactionManager = "testAppMysqlTransactionManager")
@ActiveProfiles("test")
public class MemberServiceIntegrationTest {
    /*@Autowired
    @Qualifier("testAppMysqlTransactionManager")
    private PlatformTransactionManager testAppMysqlTransactionManager;*/
    @Autowired
    private MemberService memberService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private BoardWishMemberRepository boardWishMemberRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void testSignup() {
        SignupRequestDto signupRequestDto = new SignupRequestDto("testUserId", "testUserName", "testPassword", "test@test.com", Gender.M, LocalDate.of(1990, 1, 1));
        MemberResponseDto response = memberService.signup(signupRequestDto);

        // 회원 가입이 정상적으로 이루어졌는지 검증
        assertNotNull(response);
        assertEquals("testUserId", response.getUserId());
        assertEquals("test@test.com", response.getEmail());

        // 생성된 회원 정보를 데이터베이스에서 확인
        Optional<Member> member = memberRepository.findByUserId("testUserId");
        assertTrue(member.isPresent());
        assertEquals("testUserId", member.get().getUserId());
        assertTrue(passwordEncoder.matches("testPassword", member.get().getPassword()));
        assertEquals("test@test.com", member.get().getEmail());

        // 생성된 블로그 정보를 데이터베이스에서 확인
        Optional<Blog> blog = blogRepository.findByMemberId(member.get().getId());
        assertTrue(blog.isPresent());
        assertEquals(response.getBlogId(), blog.get().getId());
    }

    @Test
    public void testLogin() {
        SignupRequestDto signupRequestDto = new SignupRequestDto("testUserId", "testUserName", "testPassword", "test@test.com", Gender.M, LocalDate.of(1990, 1, 1));
        memberService.signup(signupRequestDto);

        // 로그인 테스트
        LoginRequestDto loginRequestDto = new LoginRequestDto("testUserId", "testPassword");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MemberResponseDto memberResponse = memberService.login(loginRequestDto, response);

        // 로그인이 정상적으로 이루어졌는지 검증
        assertNotNull(memberResponse);
        assertEquals("testUserId", memberResponse.getUserId());
        assertEquals("test@test.com", memberResponse.getEmail());

        //토큰 생성 및 일치 여부 확인 구욷~
        String token = response.getHeader(JwtUtil.AUTHORIZATION_HEADER);
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token.substring(7)));
    }

    @Test
    public void testCheckUserId() {
        // 회원 가입
        SignupRequestDto signupRequestDto = new SignupRequestDto("testUser123", "testNickname", "Test@1234", "test@test.com", Gender.M, LocalDate.of(1990, 1, 1));
        memberService.signup(signupRequestDto);

        // 아이디 중복 확인
        Boolean isUserIdExist = memberService.checkUserId("testUser123");
        assertTrue(isUserIdExist);

        // 존재하지 않는 아이디 확인
        Boolean isUserIdNotExist = memberService.checkUserId("nonexistentUser");
        assertFalse(isUserIdNotExist);
    }

    @Test
    public void testGetUserByToken() {
        // 회원 가입
        SignupRequestDto signupRequestDto = new SignupRequestDto("testUser123", "testNickname", "Test@1234", "test@test.com", Gender.M, LocalDate.of(1990, 1, 1));
        memberService.signup(signupRequestDto);

        // 사용자를 불러옴
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername("testUser123");
        MemberResponseDto memberResponse = memberService.getUserByToken(userDetails);

        // 사용자 정보가 정상적으로 불러와졌는지 확인
        assertNotNull(memberResponse);
        assertEquals("testUser123", memberResponse.getUserId());
        assertEquals("test@test.com", memberResponse.getEmail());
    }
}
