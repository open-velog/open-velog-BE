package com.openvelog.openvelogbe.board.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.Gender;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.member.dto.SignupRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BlogRepository blogRepository;

    @InjectMocks
    private BoardService boardService;

    private BoardRequestDto.BoardAdd boardRequestDto;
    private UserDetailsImpl userDetails;
    private UserDetailsImpl differentUser;
    private Blog blog;
    private Member member;
    private Member differentMember;
    private Board board;

    @BeforeEach
    void setUp() {
        Long blogId = 1L;
        String title = "Test Title";
        String content = "Test Content";
        boardRequestDto = new BoardRequestDto.BoardAdd(blogId, title, content);
        SignupRequestDto signupRequestDto = new SignupRequestDto("test1234", "testUsername", "test1234!","test1234@naver.com", Gender.M, LocalDate.now());
        member = Member.create(signupRequestDto, signupRequestDto.getPassword());
        member.setId(1L);
        differentMember = Member.create(new SignupRequestDto("other1234", "otherUser", "test1234!","other@naver.com", Gender.F, LocalDate.now()),"test1234!");
        differentMember.setId(2L);
        differentUser = new UserDetailsImpl(differentMember, "other1234");
        userDetails = new UserDetailsImpl(member, "test1234");
        blog = Blog.create(member);
        board = Board.create(boardRequestDto, blog);
        board.setId(1L);
    }

    @Test
    @DisplayName("게시글 조회 - 성공")
    void readBoardSuccess() {
        // given
        when(boardRepository.findByIdJPQL(board.getId())).thenReturn(Optional.of(board));

        // when
        BoardResponseDto boardResponseDto = boardService.getBoard(board.getId(), null);

        // then
        assertEquals(board.getId(), boardResponseDto.getId());
        assertEquals(board.getTitle(), boardResponseDto.getTitle());
        assertEquals(board.getContent(), boardResponseDto.getContent());
    }

    @Test
    @DisplayName("게시글 조회 - 블로그 게시글 목록 조회 성공")
    void readBoardListByBlogSuccess() {
        // given
        int page = 1;
        int size = 10;

        List<Board> boardList = new ArrayList<>();
        boardList.add(Board.create(new BoardRequestDto.BoardAdd(blog.getId(), "title1", "content1"), blog));
        boardList.add(Board.create(new BoardRequestDto.BoardAdd(blog.getId(), "title2", "content2"), blog));
        boardList.add(Board.create(new BoardRequestDto.BoardAdd(blog.getId(), "title3", "content3"), blog));

        Page<Board> pageResponse = new PageImpl<>(boardList);
        Pageable pageable = PageRequest.of(page - 1, size);

        when(boardRepository.findBoardsByBlogIdJPQL(blog.getId(),pageable)).thenReturn(pageResponse);

        // when
        Page<BoardResponseDto> boardPage = boardService.getBoardListByBlog(blog.getId(), page, size);

        // then
        assertEquals(boardList.size(), boardPage.getContent().size());
        for (int i = 0; i < boardList.size(); i++) {
            assertEquals(boardList.get(i).getTitle(), boardPage.getContent().get(i).getTitle());
            assertEquals(boardList.get(i).getContent(), boardPage.getContent().get(i).getContent());
        }
    }

    @Test
    @DisplayName("게시글 조회 - 찾을 수 없는 게시글 조회 실패")
    void getBoardFailWhenInvalidBoardId() {
        // given
        Long invalidBoardId = 2L;
        when(boardRepository.findByIdJPQL(invalidBoardId)).thenReturn(Optional.empty());

        //when, then
        assertThrows(EntityNotFoundException.class, () -> boardService.getBoard(invalidBoardId, null));
    }

    @Test
    @DisplayName("게시글 생성 - 성공")
    void createBoardSuccess() {
        // Given
        when(blogRepository.findById(boardRequestDto.getBlogId())).thenReturn(Optional.of(blog));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        // When
        BoardResponseDto result = boardService.createBoard(boardRequestDto, userDetails);

        // Then
        assertEquals(boardRequestDto.getTitle(), result.getTitle());
        assertEquals(boardRequestDto.getContent(), result.getContent());
        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    @DisplayName("게시글 생성 - 블로그 찾을 수 없을때 실패")
    void createBoardBlogNotFound() {
        // Given
        when(blogRepository.findById(boardRequestDto.getBlogId())).thenReturn(Optional.empty());

        // When, Then
        assertThrows(EntityNotFoundException.class, () -> boardService.createBoard(boardRequestDto, userDetails));
    }

    @Test
    @DisplayName("게시글 생성 - 블로그 주인과 작성자 다를때 실패")
    void createBoardAccessDeniedBlog() {
        // Given
        when(blogRepository.findById(boardRequestDto.getBlogId())).thenReturn(Optional.of(blog));


        // When
        assertThrows(AccessDeniedException.class, () -> boardService.createBoard(boardRequestDto, differentUser));

        // Then
        verify(boardRepository, never()).save(board);
    }


    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deleteBoardSuccess() {
        //Given
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        //When
        boardService.deleteBoard(board.getId(), userDetails);
        //Then
        verify(boardRepository, times(1)).deleteById(board.getId());

    }

    @Test
    @DisplayName("게시글 삭제 - 작성자가 아닌 경우 실패")
    void deleteBoardNotAuthorized() {
        // Given
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));


        // When
        assertThrows(AccessDeniedException.class, () -> boardService.deleteBoard(board.getId(), differentUser));

        // Then
        //deleteById() 메서드가 실행되지 않았는지 검증
        verify(boardRepository, never()).deleteById(board.getId());
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    void updateBoardSuccess() {
        //given
        String title = "update title";
        String content = "update content";
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));
        //when
        BoardResponseDto result = boardService.updateBoard(board.getId(), new BoardRequestDto.BoardUpdate(title, content), userDetails);
        //then
        assertEquals(result.getTitle(), title);
        assertEquals(result.getContent(), content);
    }

    @Test
    @DisplayName("게시글 수정 - 작성자가 아닐때 실패")
    void updateBoardNotAuthorized() {
        //given
        String title = "update title";
        String content = "update content";
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        //when, then
        assertThrows(AccessDeniedException.class, () -> boardService.updateBoard(board.getId(), new BoardRequestDto.BoardUpdate(title, content), differentUser));
    }
}