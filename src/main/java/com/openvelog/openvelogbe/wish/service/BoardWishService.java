package com.openvelog.openvelogbe.wish.service;

import com.openvelog.openvelogbe.common.dto.ErrorMessage;
import com.openvelog.openvelogbe.common.entity.Blog;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.BoardWishMember;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import com.openvelog.openvelogbe.common.repository.BoardWishMemberRepository;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardWishService {
    private final BoardWishMemberRepository boardWishMemberRepository;
    private final BoardRepository boardRepository;


    @Transactional
    public Boolean setBoardWish (Long boardId, UserDetailsImpl userDetails) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.BOARD_NOT_FOUND.getMessage())
        );

        Optional<BoardWishMember> boardWishMember = boardWishMemberRepository.findByMemberAndBoard(userDetails.getUser(), board);
        Blog blog = board.getBlog();

        if (!boardWishMember.isPresent()) {
            boardWishMemberRepository.save(BoardWishMember.create(board, userDetails.getUser()));
            blog.updateWishCountSum(blog.getWishCountSum() + 1);
            return true;
        }

        boardWishMemberRepository.deleteById(boardWishMember.get().getId());
        blog.updateWishCountSum(blog.getWishCountSum() - 1);
        return false;
    }

}
