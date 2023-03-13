package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.BoardWishMember;
import com.openvelog.openvelogbe.common.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardWishMemberRepository extends JpaRepository<BoardWishMember, Long> {
    Optional<BoardWishMember> findByMemberAndBoard(Member member, Board board);

}
