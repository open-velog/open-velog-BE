package com.openvelog.openvelogbe.openSearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openvelog.openvelogbe.board.dto.BoardResponseAndCountDto;
import com.openvelog.openvelogbe.board.dto.BoardResponseDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final BoardRepository boardRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final int batchSize = 10000;
    private Long lastProcessedBoardId = 0L;
    @Value("${DATABASE_URL}")
    private String dbUrl;

    @Value("${DATABASE_USERNAME}")
    private String dbUsername;

    @Value("${DATABASE_PASSWORD}")
    private String dbPassword;

    @PostConstruct
    private void initializeLastProcessedBoardId() {
        // 저장된 lastProcessedBoardId 값을 가져옵니다.
        // 이 값은 이전 호출에서 처리한 마지막 Board의 id입니다.
        lastProcessedBoardId = retrieveLastProcessedBoardIdFromDatabase();
    }

    public void indexAllBoards() throws IOException {
        int offset = 0;
        while (true) {
            // lastProcessedBoardId 이후의 batchSize개의 Board 데이터를 가져옵니다.
            List<Board> boards = boardRepository.findByIdGreaterThanOrderByIdAsc(lastProcessedBoardId, PageRequest.of(offset, batchSize)).getContent();
            if (boards.isEmpty()) {
                break;
            }
            HttpEntity entity = createBulkRequestEntity(boards);
            Request request = new Request("POST", "/board/_bulk");
            request.setEntity(entity);
            restClient.performRequest(request);

            // 마지막으로 처리한 Board의 id를 업데이트합니다.
            lastProcessedBoardId = boards.get(boards.size() - 1).getId();
            updateLastProcessedBoardIdInDatabase(lastProcessedBoardId);

            offset += batchSize;

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

    private Connection getConnection() throws SQLException {
        String url = dbUrl;
        String user = dbUsername;
        String password = dbPassword;
        return DriverManager.getConnection(url, user, password);
    }

    private Long retrieveLastProcessedBoardIdFromDatabase() {
        Long lastProcessedBoardId = 0L;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT last_processed_board_id FROM boards";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                lastProcessedBoardId = rs.getLong("last_processed_board_id");
            }
        } catch (SQLException ex) {
            // handle exception
        }
        return lastProcessedBoardId;
    }

    private void updateLastProcessedBoardIdInDatabase(Long lastProcessedBoardId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE some_table SET last_processed_board_id = ?")) {
            ps.setLong(1, lastProcessedBoardId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            // handle exception
        }
    }

    public BoardDocumentResponseAndCountDto search(String keyword, Integer page, Integer size) throws IOException {
        String endpoint = String.format("/%s/_search", "board");
        Request request = new Request("GET", endpoint);
        request.setJsonEntity(getRequestBody(keyword, (page-1) * size, size));
        Response response = restClient.performRequest(request);
        return parseResponse(response,page,size);
    }

    private String getRequestBody(String keyword, int offset, int size) {
        return String.format("{\"track_total_hits\": true,\"from\": %d, \"size\": %d,\"query\": {\"bool\": {\"should\": [{\"query_string\": {\"query\": \"%s\",\"fields\": [\"title\"],\"default_operator\": \"AND\"}},{\"query_string\": {\"query\": \"%s\",\"fields\": [\"content\"],\"default_operator\": \"AND\"}}],\"minimum_should_match\": 1}}, \"sort\": [{\"id\": {\"order\": \"desc\"}}]}", offset, size, keyword, keyword);
        //return String.format("{\"track_total_hits\": true,\"from\": %d, \"size\": %d,\"query\": {\"bool\": {\"should\": [{\"match\": {\"title\": {\"query\": \"%s\",\"operator\": \"AND\"}}},{\"match\": {\"content\": {\"query\": \"%s\",\"operator\": \"AND\"}}}],\"minimum_should_match\": 1}}, \"sort\": [{\"id\": {\"order\": \"desc\"}}]}", offset, size, keyword, keyword);
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



