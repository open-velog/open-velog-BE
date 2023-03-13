package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select distinct b from boards b left join b.blog bl " +
            "where :keyword is null or :keyword = '' or bl.title like %:keyword% or b.title like %:keyword% or b.content like %:keyword%")
    List<Board> searchTitleOrContentOrBlogTitle(String keyword);
    Optional<Board> findById(Long boardId);
}
