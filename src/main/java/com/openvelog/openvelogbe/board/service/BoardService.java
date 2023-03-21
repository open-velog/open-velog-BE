package com.openvelog.openvelogbe.board.service;

import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.Keyword;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.repository.KeywordRedisRepository;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.common.util.GetAgeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BlogRepository blogRepository;
    private final KeywordRedisRepository redisRepository;
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto.BoardAdd dto, UserDetailsImpl userDetails) {

        Blog blog = blogRepository.findById(dto.getBlogId()).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BLOG_NOT_FOUND.getMessage())
        );

        if (!blog.getMember().getId().equals(userDetails.getUser().getId())) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED.getMessage());
        }

        Board board = Board.create(dto,blog);
        board = boardRepository.save(board);
        return BoardResponseDto.of(board);
    }

    @Transactional
    public BoardResponseDto getBoard (Long boardId, UserDetailsImpl userDetails){
        Member member = userDetails != null ? userDetails.getUser() : null;
        Board board = boardRepository.findByIdJPQL(boardId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );

        board.addViewCount();

        return BoardResponseDto.of(board, member);
    }

    @Transactional
    public BoardResponseDto updateBoard (
            Long boardId,
            BoardRequestDto.BoardUpdate dto,
            UserDetailsImpl userDetails
    ) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );

        if (!userDetails.getUser().getId().equals(board.getBlog().getMember().getId())) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED.getMessage());
        }

        board.update(dto);

        return BoardResponseDto.of(board);
    }

    @Transactional
    public void deleteBoard
            (Long boardId,
             UserDetailsImpl userDetails)
    {
        Board board = boardRepository.findById(boardId).orElseThrow(
                ()->new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );

        if (!userDetails.getUser().getId().equals(board.getBlog().getMember().getId())) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED.getMessage());
        }

        boardRepository.deleteById(board.getId());
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> searchBoards (String keyword, Integer page, Integer size, UserDetailsImpl userDetails){
        Member member = userDetails != null ? userDetails.getUser() : null;

        Pageable pageable = PageRequest.of(page - 1, size);

//        Page<Board> boards = boardRepository.searchTitleOrContentOrBlogTitle(keyword + "*", pageable);
        List<Board> boards = boardRepository.searchTitleOrContentOrBlogTitle(keyword, (page-1) * size, page * size, size);

        GetAgeRange getAgeRange = new GetAgeRange();
        AgeRange ageRange = member != null ? getAgeRange.getAge(member) : null;
        Keyword newkeyword = new Keyword (keyword, member, ageRange);

        redisRepository.save(newkeyword);

//        return boards.map(board -> BoardResponseDto.of(board, member));
        return boards.stream().map(board -> BoardResponseDto.of(board, member)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardListByBlog(Long blogId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return boardRepository.findBoardsByBlogIdJPQL(blogId, pageable).map(BoardResponseDto::of);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardListByBlogMemberId(Long memberId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return boardRepository.findBoardsByMemberIdJPQL(memberId, pageable).map(BoardResponseDto::of);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponseDto> getBoardListByBlogUserId(String userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return boardRepository.findBoardsByUserIdJPQL(userId, pageable).map(BoardResponseDto::of);
    }
}
