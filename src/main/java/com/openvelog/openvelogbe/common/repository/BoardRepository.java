package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.Board;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select distinct b from boards b left join b.blog bl " +
            "where :keyword is null or :keyword = '' or b.title like %:keyword% or b.content like %:keyword%")
    @EntityGraph(attributePaths = {
            "wishes", "blog", "blog.member"
    })
    List<Board> searchTitleOrContentOrBlogTitle(String keyword);
    Optional<Board> findById(Long boardId);

    @Query("select b from boards b where b.id = :boardId")
    @EntityGraph(attributePaths = {
            "wishes", "blog", "blog.member"
    })
    Optional<Board> findByIdJPQL(Long boardId);
}
