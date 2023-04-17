package com.openvelog.openvelogbe.openSearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openvelog.openvelogbe.common.entity.*;
import com.openvelog.openvelogbe.common.entity.enums.AgeRange;
import com.openvelog.openvelogbe.common.repository.KeywordRedisRepository;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.common.util.GetAgeRange;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentResponseAndCountDto;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.elasticsearch.action.index.IndexRequest;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final BoardRepository boardRepository;

    private final KeywordRedisRepository redisRepository;

    private final KafkaTemplate<String, SearchLog> logKafkaTemplate;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final int batchSize = 10000;


    public void indexAllBoards(int firstDataIndex) throws IOException {
        //int firstDataIndex = 910000;
        int currentPage = firstDataIndex / batchSize;
        while (true) {
            // currentPage를 사용하여 batchSize 개의 Board 데이터를 가져옵니다.
            List<Board> boards = boardRepository.findAll(PageRequest.of(currentPage, batchSize)).getContent();
            if (boards.isEmpty()) {
                break;
            }
            HttpEntity entity = createBulkRequestEntity(boards);
            Request request = new Request("POST", "/board/_bulk");
            request.setEntity(entity);
            restClient.performRequest(request);

            // 다음 페이지로 이동합니다.
            currentPage++;
            System.out.println("Current page: " + currentPage);

            // 전송 완료 메시지 출력
            System.out.println("Batch transfer completed for " + (currentPage * batchSize) + " records.");
        }
    }

    private HttpEntity createBulkRequestEntity(List<Board> boards) throws JsonProcessingException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Board board : boards) {
            BoardDocument boardDocument = BoardDocument.create(board);
            IndexRequest indexRequest = new IndexRequest("board");
            indexRequest.id(boardDocument.getId().toString());

            // Create JSON object using Jackson ObjectMapper
            ObjectNode indexNode = objectMapper.createObjectNode();
            indexNode.putObject("index")
                    .put("_index", indexRequest.index())
                    .put("_id", indexRequest.id());
            out.write(objectMapper.writeValueAsBytes(indexNode));
            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));

            // Create JSON object using Jackson ObjectMapper
            ObjectNode boardNode = objectMapper.valueToTree(boardDocument);
            out.write(objectMapper.writeValueAsBytes(boardNode));
            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }
        return new NByteArrayEntity(out.toByteArray(), ContentType.APPLICATION_JSON);
    }

    public BoardDocumentResponseAndCountDto search(String keyword, Integer page, Integer size, UserDetailsImpl userDetails) {
        String endpoint = String.format("/%s/_search", "board");
        Request request = new Request("GET", endpoint);

        Member member = userDetails != null ? userDetails.getUser() : null;

        SearchLog searchLog = SearchLog.create(keyword, member);

        request.setJsonEntity(getRequestBody(keyword, (page-1) * size, size));
        try {

//            GetAgeRange getAgeRange = new GetAgeRange();
//            AgeRange ageRange = member != null ? getAgeRange.getAge(member) : null;
//            Keyword newkeyword = new Keyword (keyword, member, ageRange);
//            redisRepository.save(newkeyword);

            logKafkaTemplate.send("search-log", keyword, searchLog);
            return parseResponse(restClient.performRequest(request), page, size);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getRequestBody(String keyword, int offset, int size) {
        String[] keywords = keyword.split(" ");
        StringBuilder shouldClauses = new StringBuilder();

        if (keywords.length == 1) {
            // 단일 검색어
            shouldClauses.append(String.format("{\"match_phrase\": {\"title\": {\"query\": \"%s\"}}},", keyword));
            shouldClauses.append(String.format("{\"match_phrase\": {\"content\": {\"query\": \"%s\"}}},", keyword));

            // 마지막에 추가된 콤마(,)를 제거하는 작업
            shouldClauses.setLength(shouldClauses.length() - 1);
            return String.format("{\"from\": %d, \"size\": %d, \"query\": {\"bool\": {\"should\": [%s]}}, \"sort\": [{\"id\": {\"order\": \"desc\"}}]}", offset, size, shouldClauses.toString());

        } else {
            // 분할된 키워드 검색
            StringBuilder titleSpanClauses = new StringBuilder();
            StringBuilder contentSpanClauses = new StringBuilder();

            for (String word : keywords) {
                titleSpanClauses.append(String.format("{\"span_term\": {\"title\": \"%s\"}},", word));
                contentSpanClauses.append(String.format("{\"span_term\": {\"content\": \"%s\"}},", word));
            }

            // 마지막에 추가된 콤마(,)를 제거하는 작업
            titleSpanClauses.setLength(titleSpanClauses.length() - 1);
            contentSpanClauses.setLength(contentSpanClauses.length() - 1);

            return String.format("{\"from\": %d, \"size\": %d, \"query\": {\"bool\": {\"should\": [{\"span_near\": {\"clauses\": [%s], \"slop\": 25, \"in_order\": true}}, {\"span_near\": {\"clauses\": [%s], \"slop\": 25, \"in_order\": true}}]}}, \"sort\": [{\"id\": {\"order\": \"desc\"}}]}", offset, size, titleSpanClauses.toString(), contentSpanClauses.toString());
        }
    }

    private BoardDocumentResponseAndCountDto parseResponse(Response response, Integer page, Integer size) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        JsonNode root = objectMapper.readTree(response.getEntity().getContent());
        JsonNode hits = root.get("hits").get("hits");
        Integer totalHits = root.get("hits").get("total").get("value").intValue();

        List<BoardDocumentDto> result = new ArrayList<>();
        for (JsonNode hit : hits) {
            JsonNode source = hit.get("_source");
            BoardDocument boardDocument = objectMapper.treeToValue(source, BoardDocument.class);
            result.add(BoardDocumentDto.of(boardDocument));
        }

        return BoardDocumentResponseAndCountDto.of(result,page-1,size,totalHits);
    }

}



