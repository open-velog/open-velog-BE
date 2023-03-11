package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
