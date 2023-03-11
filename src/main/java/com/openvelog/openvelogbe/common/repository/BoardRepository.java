package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
