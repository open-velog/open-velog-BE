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

    @Query("SELECT b " +
            "FROM blogs b " +
            "LEFT JOIN b.member " +
            "ORDER BY b.viewCountSum DESC, b.createdAt DESC")
    Page<Blog> findAllByViewCountSum(Pageable pageable);


    @Query("SELECT b " +
            "FROM blogs b " +
            "LEFT JOIN b.member " +
            "ORDER BY b.wishCountSum DESC, b.createdAt DESC")
    Page<Blog> findAllByWishCountSum(Pageable pageable);

    @Modifying
    @Query(value = "UPDATE blogs b " +
            "INNER JOIN (" +
            "SELECT blog_id, SUM(view_count) AS total_views " +
            "FROM boards " +
            "GROUP BY blog_id" +
            ") v ON b.id = v.blog_id " +
            "SET b.view_count_sum = v.total_views",
            nativeQuery = true)
    void updateViewCountSum();

    @Modifying
    @Query(value = "UPDATE blogs b " +
            "INNER JOIN (" +
            "SELECT blog_id, COUNT(*) as total_likes " +
            "FROM boards brd " +
            "LEFT JOIN board_wish_members bwm " +
            "ON brd.id = bwm.board_id " +
            "GROUP BY blog_id" +
            ") v ON b.id = v.blog_id " +
            "SET b.wish_count_sum = v.total_likes",
            nativeQuery = true)
    void updateWishCountSum();

}
