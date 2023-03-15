package com.openvelog.openvelogbe.common.repository;

import com.openvelog.openvelogbe.common.entity.Keyword;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface KeywordRedisRepository extends CrudRepository<Keyword, String> {
    @Override
    List<Keyword> findAll();
    List<Keyword> findByKeyword(String keyword);
}
