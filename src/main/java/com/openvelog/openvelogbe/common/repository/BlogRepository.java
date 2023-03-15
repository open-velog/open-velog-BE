package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.Blog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("select distinct b from blogs b join fetch b.member m where m.id = :userId")
    Optional<Blog> findByMemberIdJPQL(Long userId);

    Optional<Blog> findByIdAndMemberId(Long blogId, Long memberId);

    Optional<Blog> findByMemberId(Long memberId);
}
