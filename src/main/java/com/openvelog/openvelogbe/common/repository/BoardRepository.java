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


//TODO 성능 개선 필요
@Query(value = "SELECT DISTINCT(id), created_at, modified_at, content, title, view_count, blog_id" +
            " FROM (" +
            "(SELECT * FROM boards WHERE MATCH(title) AGAINST(:keyword IN BOOLEAN MODE) ORDER BY id DESC LIMIT :totalCount) " +
            "UNION ALL" +
            " (SELECT * FROM boards WHERE MATCH(content) AGAINST(:keyword IN BOOLEAN MODE) ORDER BY id DESC LIMIT :totalCount)" +
            ") AS combined_results LIMIT :offset, :size", nativeQuery = true)

    List<Board>  searchTitleOrContentOrBlogTitle(String keyword, Integer offset, Integer totalCount, Integer size);
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
