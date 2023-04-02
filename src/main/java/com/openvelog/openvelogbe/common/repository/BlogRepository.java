package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.blog.dto.BlogResponseDto;
import com.openvelog.openvelogbe.common.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("SELECT b, " +
            "SUM(bb.viewCount) as viewCountSum, " +
            "SUM(bb.wishes.size) as wishCountSum " +
            "FROM blogs b " +
            "JOIN b.member m " +
            "LEFT JOIN b.boards bb " +
            "LEFT JOIN bb.wishes " +
            "WHERE m.userId = :userId " +
            "GROUP BY b.id")
    @EntityGraph(attributePaths = {
            "member", "boards","boards.wishes"
    })
    List<Object[]> findByUserIdJPQL(String userId);

    Optional<Blog> findByIdAndMemberId(Long blogId, Long memberId);

    Optional<Blog> findByMemberId(Long memberId);

    @Query("SELECT b, " +
            "SUM(bb.viewCount) as viewCountSum, " +
            "SUM(bb.wishes.size) as wishCountSum " +
            "FROM blogs b " +
            "LEFT JOIN b.boards bb " +
            "LEFT JOIN bb.wishes " +
            "LEFT JOIN b.member " +
            "GROUP BY b.id")
    @EntityGraph(attributePaths = {
            "member", "boards","boards.wishes"
    })
    Page<Object[]> findAllOrderByBoardsCountedDesc(Pageable pageable);

    @Modifying
    @Query(value = "UPDATE blogs b SET " +
            "b.view_count_sum = COALESCE((SELECT SUM(bb.view_count) FROM boards bb WHERE bb.blog_id = b.id), 0), " +
            "b.wish_count_sum = COALESCE((SELECT SUM(wishes_count) FROM (SELECT bb.id, COUNT(w.board_id) as wishes_count FROM boards bb LEFT JOIN board_wish_members w ON bb.id = w.board_id WHERE bb.blog_id = b.id GROUP BY bb.id) as subquery), 0)",
            nativeQuery = true)
    void updateViewCountSumAndWishCountSum();
}
