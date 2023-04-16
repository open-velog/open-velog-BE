package com.openvelog.openvelogbe.openSearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openvelog.openvelogbe.common.entity.Member;
import com.openvelog.openvelogbe.common.entity.SearchLog;
import com.openvelog.openvelogbe.common.security.UserDetailsImpl;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardDocumentResponseAndCountDto;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.elasticsearch.action.index.IndexRequest;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.common.entity.BoardDocument;
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
            logKafkaTemplate.send("search-log", keyword, searchLog);
            return parseResponse(restClient.performRequest(request), page, size);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getRequestBody(String keyword, int offset, int size) {
        return String.format("{\"from\": %d, \"size\": %d, \"query\": {\"bool\": {\"should\": [{\"match_phrase\": {\"title\": \"%s\"}},{\"match_phrase\": {\"content\": \"%s\"}}],\"minimum_should_match\": 1}}, \"sort\": [{\"id\": {\"order\": \"desc\"}}]}", offset, size, keyword, keyword);
        //return String.format("{\"track_total_hits\": true,\"from\": %d, \"size\": %d, \"_source\": [\"title\", \"content\"],\"query\": {\"bool\": {\"should\": [{\"query_string\": {\"query\": \"%s\",\"fields\": [\"title\"],\"default_operator\": \"AND\",\"minimum_should_match\": \"100%%\"}},{\"query_string\": {\"query\": \"%s\",\"fields\": [\"content\"],\"default_operator\": \"AND\",\"minimum_should_match\": \"100%%\"}}],\"minimum_should_match\": 1}}, \"sort\": [{\"id\": {\"order\": \"desc\"}}]}", offset, size, keyword, keyword);
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



