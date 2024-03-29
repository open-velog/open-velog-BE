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
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    @Query("SELECT b FROM blogs b WHERE b.id=:id")
    @EntityGraph(attributePaths = "boards.wishes")
    Optional<Blog> findByIdJPQL(Long id);

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

    @Query("SELECT b from blogs b WHERE b.member.userId= :userId")
    Optional<Blog> findByUserId(String userId);

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
            "FROM blogs b ")
    Page<Blog> findAllByViewCountSum(Pageable pageable);


    @Query("SELECT b " +
            "FROM blogs b " )
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
