package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

//    @Query("select b from boards b left join b.blog bl " +
//            "where :keyword is null or :keyword = '' or b.title like %:keyword% or b.content like %:keyword% " +
//            "group by b.id")
//    @EntityGraph(attributePaths = {
//            "wishes", "blog", "blog.member"
//    })
    @Query(value = "SELECT * FROM boards WHERE MATCH(title, content) AGAINST(:keyword IN BOOLEAN MODE)",nativeQuery = true)
    Page<Board> searchTitleOrContentOrBlogTitle(@Param("keyword") String keyword, Pageable pageable);
    Optional<Board> findById(Long boardId);

    @Query("select b from boards b where b.id = :boardId")
    @EntityGraph(attributePaths = {
            "wishes", "blog", "blog.member"
    })
    Optional<Board> findByIdJPQL(Long boardId);

    @Query("select b from boards b where b.blog.member.id = :memberId")
    @EntityGraph(attributePaths = {
            "blog", "blog.member"
    })
    Page<Board> findBoardsByMemberIdJPQL(Long memberId, Pageable pageable);


    @Query("select b from boards b where b.blog.member.userId = :userId")
    @EntityGraph(attributePaths = {
            "blog", "blog.member"
    })
    Page<Board> findBoardsByUserIdJPQL(String userId, Pageable pageable);

    @Query("select b from boards b where b.blog.id = :blogId")
    Page<Board> findBoardsByBlogIdJPQL(Long blogId, Pageable pageable);
}
