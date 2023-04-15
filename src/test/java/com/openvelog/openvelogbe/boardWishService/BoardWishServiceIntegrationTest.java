package com.openvelog.openvelogbe.boardWishService;

import com.openvelog.openvelogbe.OpenVelogBeApplication;
import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.board.service.BoardService;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.jwt.JwtUtil;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.common.security.UserDetailsServiceImpl;
import com.openvelog.openvelogbe.config.TestAppMysqlDatabaseConfig;
import com.openvelog.openvelogbe.member.dto.MemberResponseDto;
import com.openvelog.openvelogbe.member.dto.SignupRequestDto;
import com.openvelog.openvelogbe.member.service.MemberService;
import com.openvelog.openvelogbe.wish.service.BoardWishService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {TestAppMysqlDatabaseConfig.class, BoardWishService.class,
        JwtUtil.class, UserDetailsServiceImpl.class, OpenVelogBeApplication.class})
@Transactional(transactionManager = "testAppMysqlTransactionManager")
@ActiveProfiles("test")
public class BoardWishServiceIntegrationTest {
    @Autowired
    private BoardWishService boardWishService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testSetBoardWish() {
        // Part 1. 회원 가입 및 유저 정보 만들기
        SignupRequestDto signupRequestDto = new SignupRequestDto("testUserId", "testUserName", "testPassword", "test@test.com", Gender.M, LocalDate.of(1990, 1, 1));
        MemberResponseDto memberResponse = memberService.signup(signupRequestDto);

        // 회원 가입이 정상적으로 이루어졌는지 확인
        assertNotNull(memberResponse);
        assertEquals("testUserId", memberResponse.getUserId());
        assertEquals("test@test.com", memberResponse.getEmail());

        // 생성된 멤버와 블로그 정보를 데이터베이스에서 확인
        Optional<Member> member = memberRepository.findByUserId("testUserId");
        Assertions.assertTrue(member.isPresent());
        Optional<Blog> blog = blogRepository.findByMemberId(member.get().getId());
        Assertions.assertTrue(blog.isPresent());
        assertEquals(memberResponse.getBlogId(), blog.get().getId());


        //Part 2. 게시글 작성 및 찜하기
        // 게시글 작성 및 확인
        BoardRequestDto.BoardAdd dto = new BoardRequestDto.BoardAdd(blog.get().getId(),"testTitle","testContent");
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername("testUserId");
        BoardResponseDto boardResponse = boardService.createBoard(dto,userDetails);
        Optional<Board> board = boardRepository.findByTitle("testTitle");
        assertEquals("testTitle", boardResponse.getTitle());
        assertEquals("testContent", boardResponse.getContent());

        // 게시글 찜하기
        Boolean addedToWishList = boardWishService.setBoardWish(board.get().getId(), userDetails);

        // 게시글 찜이 정상적으로 이루어졌는지 검증
        assertNotNull(addedToWishList);
        assertEquals(true, addedToWishList);

        // 블로그의 wishCountSum 값 확인
        Blog updatedBlog = board.get().getBlog();
        assertEquals(1, updatedBlog.getWishCountSum());

        // 찜 목록에서 제거
        Boolean removedFromWishList = boardWishService.setBoardWish(board.get().getId(), userDetails);

        // 게시글 찜 제거가 정상적으로 이루어졌는지 검증
        assertNotNull(removedFromWishList);
        assertEquals(false, removedFromWishList);

        // 블로그의 wishCountSum 값 감소 확인
        Blog updatedBlogAfterRemoval = board.get().getBlog();
        assertEquals(0, updatedBlogAfterRemoval.getWishCountSum());
    }
}
