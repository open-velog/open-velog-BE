package com.openvelog.openvelogbe.keyword.service;

import com.openvelog.openvelogbe.common.entity.Keyword;
import com.openvelog.openvelogbe.common.repository.KeywordRedisRepository;
import com.openvelog.openvelogbe.keyword.dto.KeyWordResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
