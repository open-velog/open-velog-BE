package com.openvelog.openvelogbe.openSearch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openvelog.openvelogbe.common.entity.Board;
import com.openvelog.openvelogbe.openSearch.dto.BoardTestDocumentDto;
import com.openvelog.openvelogbe.openSearch.dto.BoardTestDocumentResponseAndCountDto;
import com.openvelog.openvelogbe.openSearch.entity.BoardTestDocument;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.elasticsearch.action.index.IndexRequest;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openvelog.openvelogbe.common.repository.BoardRepository;
import org.opensearch.client.RestClient;
import java.io.IOException;
import static org.junit.Assert.assertEquals;

@Service
@RequiredArgsConstructor
public class OpenSearchAddIndexAndSearch {
    private final BoardRepository boardRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final int batchSize = 10;

    public void createIndexWithAnalyzer() throws IOException {
        // 인덱스 생성 및 설정을 정의합니다.
        String indexSettings = "{\n" +
                "  \"settings\": {\n" +
                "    \"index\": {\n" +
                "      \"number_of_shards\": 10,\n" +
                "      \"number_of_replicas\": 1\n" +
                "    },\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"ngram_analyzer\": {\n" +
                "          \"tokenizer\": \"ngram_tokenizer\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"tokenizer\": {\n" +
                "        \"ngram_tokenizer\": {\n" +
                "          \"type\": \"ngram\",\n" +
                "          \"min_gram\": 1,\n" +
                "          \"max_gram\": 2,\n" +
                "          \"token_chars\": [\n" +
                "            \"letter\",\n" +
                "            \"digit\",\n" +
                "            \"punctuation\"\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"title\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ngram_analyzer\"\n" +
                "      },\n" +
                "      \"content\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ngram_analyzer\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // 인덱스 생성 및 설정을 수행하는 요청을 생성합니다.
        Request createIndexRequest = new Request("PUT", "/board_test");
        createIndexRequest.setJsonEntity(indexSettings);

        // 요청을 실행하고 응답을 확인합니다.
        Response createIndexResponse = restClient.performRequest(createIndexRequest);
        assertEquals(200, createIndexResponse.getStatusLine().getStatusCode());
    }

    public void indexTestBoards(List<Board> testBoards) throws IOException {
        int offset = 0;
        int boardsSize = testBoards.size();

        while (offset < boardsSize) {
            List<Board> boards = boardRepository.findAll(PageRequest.of(offset, batchSize)).getContent();
            if (boards.isEmpty()) {
                break;
            }
            HttpEntity entity = createTestBulkRequestEntity(boards);
            Request request = new Request("POST", "/board_test/_bulk");
            request.setEntity(entity);
            restClient.performRequest(request);

            offset += batchSize;

        }
    }

    private HttpEntity createTestBulkRequestEntity(List<Board> boards) throws JsonProcessingException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Board board : boards) {
            BoardTestDocument boardTestDocument = BoardTestDocument.create(board);
            IndexRequest indexRequest = new IndexRequest("board_test");
            indexRequest.id(boardTestDocument.getId().toString());

            // Create JSON object using Jackson ObjectMapper
            ObjectNode indexNode = objectMapper.createObjectNode();
            indexNode.putObject("index")
                    .put("_index", indexRequest.index())
                    .put("_id", indexRequest.id());
            out.write(objectMapper.writeValueAsBytes(indexNode));
            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));

            // Create JSON object using Jackson ObjectMapper
            ObjectNode boardNode = objectMapper.valueToTree(boardTestDocument);
            out.write(objectMapper.writeValueAsBytes(boardNode));
            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }
        return new NByteArrayEntity(out.toByteArray(), ContentType.APPLICATION_JSON);
    }

    public BoardTestDocumentResponseAndCountDto search(String keyword, Integer page, Integer size) throws IOException {
        String endpoint = String.format("/%s/_search", "board_test");
        Request request = new Request("GET", endpoint);
        request.setJsonEntity(getRequestBody(keyword, (page-1) * size, size));
        Response response = restClient.performRequest(request);
        return parseResponse(response,page,size);
    }

    private String getRequestBody(String keyword, int offset, int size) {
        return String.format("{\"track_total_hits\": true,\"from\": %d, \"size\": %d,\"query\": {\"bool\": {\"should\": [{\"query_string\": {\"query\": \"%s\",\"fields\": [\"title\"],\"default_operator\": \"AND\"}},{\"query_string\": {\"query\": \"%s\",\"fields\": [\"content\"],\"default_operator\": \"AND\"}}],\"minimum_should_match\": 1}}, \"sort\": [{\"id\": {\"order\": \"desc\"}}]}", offset, size, keyword, keyword);
    }

    private BoardTestDocumentResponseAndCountDto parseResponse(Response response, Integer page, Integer size) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        JsonNode root = objectMapper.readTree(response.getEntity().getContent());
        JsonNode hits = root.get("hits").get("hits");
        Integer totalHits = root.get("hits").get("total").get("value").intValue();

        List<BoardTestDocumentDto> result = new ArrayList<>();
        for (JsonNode hit : hits) {
            JsonNode source = hit.get("_source");
            BoardTestDocument boardTestDocument = objectMapper.treeToValue(source, BoardTestDocument.class);
            result.add(BoardTestDocumentDto.of(boardTestDocument));
        }

        return BoardTestDocumentResponseAndCountDto.of(result,page-1,size,totalHits);
    }

}
