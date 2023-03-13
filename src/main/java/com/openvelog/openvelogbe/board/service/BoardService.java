package com.openvelog.openvelogbe.board.service;

import com.openvelog.openvelogbe.board.dto.BoardRequestDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.repository.BlogRepository;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
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
    private final BlogRepository blogRepository;
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto.BoardAdd dto, UserDetailsImpl userDetails) {

        Blog blog = blogRepository.findById(dto.getBlogId()).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BLOG_NOT_FOUND.getMessage())
        );

        if (!blog.getMember().getId().equals(userDetails.getUser().getId())) {
            throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED.getMessage());
        }

        Board board = Board.create(dto,blog);
        boardRepository.save(board);
        return BoardResponseDto.of(board);
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoard (Long boardId){
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );
        return BoardResponseDto.of(board);
    }

    @Transactional
    public BoardResponseDto updateBoard (
            Long boardId,
            BoardRequestDto.BoardUpdate dto,
            UserDetailsImpl userDetails
    ) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                ()->new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
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
}
