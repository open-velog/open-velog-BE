package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.blog.dto.BlogResponseDto;
import com.openvelog.openvelogbe.common.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("select distinct b from blogs b join fetch b.member m where m.userId = :userId")
    Optional<Blog> findByUserIdJPQL(String userId);

    Optional<Blog> findByIdAndMemberId(Long blogId, Long memberId);

    Optional<Blog> findByMemberId(Long memberId);

    @Query("SELECT b, SUM(bb.viewCount) as viewCountSum FROM blogs b LEFT JOIN b.boards bb GROUP BY b.id ORDER BY viewCountSum DESC")
    Page<Object[]> findAllOrderByBoardsViewCountDesc(Pageable pageable);

    @Query("SELECT b, SUM(bb.wishes.size) as wishesSize FROM blogs b JOIN b.boards bb JOIN bb.wishes GROUP BY b.id ORDER BY wishesSize DESC")
    Page<Object[]> findAllOrderByWish(Pageable pageable);

}
