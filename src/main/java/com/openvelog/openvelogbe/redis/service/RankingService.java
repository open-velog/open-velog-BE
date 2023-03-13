package com.openvelog.openvelogbe.redis.service;

import com.openvelog.openvelogbe.redis.dto.ResponseRankingDto;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Pipeline;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RankingService {
    private final RedisTemplate<String, String> redisTemplate;

   /*public void sortedSet() throws Exception{
       String key = "ranking";
       ZSetOperations<String,String> zSet= redisTemplate.opsForZSet();
       Set<String> expect = new HashSet<>();
       expect.add("a");
       expect.add("b");
       expect.add("c");
       expect.add("d");
       expect.add("e");
       expect.add("f");

       zSet.add(key, "d",4);
       zSet.add(key, "c",4);
       zSet.add(key, "a",1);
       zSet.add(key, "b",2);
       zSet.add(key, "f",9);
       zSet.add(key, "e",8);
   }*/
    public List<ResponseRankingDto> getRankingList() {
        String key = "ranking";
        ZSetOperations<String, String> zSet = redisTemplate.opsForZSet();
        Set<String> expect = new HashSet<>();
        expect.add("a");
        expect.add("b");
        expect.add("c");
        expect.add("d");
        expect.add("e");
        expect.add("f");

        zSet.add(key, "d",4);
        zSet.add(key, "c",4);
        zSet.add(key, "a",1);
        zSet.add(key, "b",2);
        zSet.add(key, "f",9);
        zSet.add(key, "e",8);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = zSet.reverseRangeWithScores(key, 0, 9);
        List<ResponseRankingDto> collect = typedTuples.stream().map(ResponseRankingDto::convertToResponseRankingDto).collect(Collectors.toList());
        return collect;
    }
}
