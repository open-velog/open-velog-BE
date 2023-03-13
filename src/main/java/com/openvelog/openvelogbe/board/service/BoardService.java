package com.openvelog.openvelogbe.board.service;

import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.repository.MemberRepository;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BlogRepository blogRepository;
    @Transactional
    public BoardResponseDto createBoard(Long blogId, BoardRequestDto requestDto, UserDetailsImpl userDetails) {
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.MEMBER_NOT_FOUND.getMessage())
        );

        Blog blog = blogRepository.findByIdAndMemberId(blogId, member.getId()).orElseThrow(
                ()->new EntityNotFoundException(ErrorMessage.BLOG_NOT_FOUND.getMessage())
        );

        Board board = Board.create(requestDto,blog);
        boardRepository.save(board);
        return BoardResponseDto.of(board);
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoard (Long boardId){
        Board board = boardRepository.findById(boardId).orElseThrow(
                ()->new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );
        return BoardResponseDto.of(board);
    }

    @Transactional
    public BoardResponseDto updateBoard
            (Long boardId,
             BoardRequestDto requestDto,
             UserDetailsImpl userDetails) {
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.MEMBER_NOT_FOUND.getMessage())
        );
        Board board = boardRepository.findById(boardId).orElseThrow(
                        ()->new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );
        if (!member.getId().equals(board.getBlog().getMember().getId())) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED.getMessage());
        }
        board.update(requestDto);
        return BoardResponseDto.of(board);
    }

    @Transactional
    public void deleteBoard
            (Long boardId,
             UserDetailsImpl userDetails)
    {
        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.MEMBER_NOT_FOUND.getMessage())
        );
        Board board = boardRepository.findById(boardId).orElseThrow(
                ()->new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );
        if (!member.getId().equals(board.getBlog().getMember().getId())) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED.getMessage());
        }
        boardRepository.deleteById(board.getId());
    }

}
