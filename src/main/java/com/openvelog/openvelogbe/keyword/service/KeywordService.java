package com.openvelog.openvelogbe.keyword.service;

import com.openvelog.openvelogbe.common.entity.Keyword;
import com.openvelog.openvelogbe.common.repository.KeywordRedisRepository;
import com.openvelog.openvelogbe.keyword.dto.KeyWordResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class KeywordService {

    private final KeywordRedisRepository redisRepository;

    public List<KeyWordResponseDto> getKeywords(){
        List<Keyword> keywords = redisRepository.findAll();
        List<KeyWordResponseDto>list = new ArrayList<>();
        for(Keyword findkeyword : keywords){
            list.add(KeyWordResponseDto.of(findkeyword));
        }
        return list;
    }

    public List<KeyWordResponseDto> getKeywordsByKeyword(String keyword){
        List<Keyword> keywords = redisRepository.findByKeyword(keyword);
        List<KeyWordResponseDto> list = new ArrayList<>();
        for(Keyword findkeyword : keywords){
            list.add(KeyWordResponseDto.of(findkeyword));
        }
        return list;
    }

    public List<Map<String, Object>> keywordRanking(){
        List<Map<String, Object>> result = redisRepository.findAll().stream()
                .collect(Collectors.groupingBy(Keyword::getKeyword, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("keyword", entry.getKey());
                    map.put("count", entry.getValue());
                    return map;
                })
                .sorted((map1, map2) -> Long.compare((Long) map2.get("count"), (Long) map1.get("count")))
                .collect(Collectors.toList());;
        return result;
    }
}
